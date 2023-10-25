package com.example.demo;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.SmartLifecycle;
import org.springframework.core.task.TaskDecorator;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleGracefulShutdownSmartLifecycleSupport implements SmartLifecycle, TaskDecorator {

	@Getter
	private volatile boolean running = false;

	@Getter
	@Setter
	private int phase = DEFAULT_PHASE - 1024;

	private int activeCount = 0;
	private Runnable completedCallback = null;
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		lock.lock();
		try {
			Assert.state(!running, "running is true");
			running = true;
			activeCount = 0;
			completedCallback = null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(Runnable callback) {
		Assert.notNull(callback, "callback is null");
		lock.lock();
		try {
			if (!running) {
				callback.run();
				return;
			}

			running = false;
			completedCallback = callback;
		} finally {
			lock.unlock();
		}

		stop();

		notifyIfCompleted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Runnable decorate(Runnable runnable) {
		return new Task(runnable);
	}

	private void notifyIfCompleted() {
		lock.lock();
		try {
			if (!running && activeCount == 0 && completedCallback != null) {
				completedCallback.run();
				completedCallback = null;
				// ※呼び出したコールバックの先で start() → stop() と呼ばれると破綻するが、そのケースは想定しない。
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 */
	@RequiredArgsConstructor
	private class Task implements Runnable {

		private final Runnable origin;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			// 前処理
			lock.lock();
			try {
				log.debug("task pre-process: count={} task={}", activeCount, origin.getClass().getName());
				
				if (!running) {
					log.debug("not running: task={}", origin.getClass().getName());
					return;
				}
				activeCount++;
			} finally {
				lock.unlock();
			}

			try {
				// 実処理
				origin.run();
			} finally {
				// 後処理
				lock.lock();
				try {
					log.debug("task post-process: count={} task={}", activeCount, origin.getClass().getName());
					
					activeCount--;
					notifyIfCompleted();
				} finally {
					lock.unlock();
				}
			}
		}
	}

}
