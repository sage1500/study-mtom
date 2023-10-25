package com.example.demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class SimpleGracefulShutdownPollerTaskSupport<T> implements Runnable {

	private final ReentrantLock stateLock = new ReentrantLock();
	private final Condition stopped = stateLock.newCondition();

	@Getter
	private volatile boolean stopping = false;

	@Setter
	private long idleWaitMillis = 1000L;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (!isStopping()) {
			try {
				// メッセージポーリング
				var message = poll();
				if (message == null) {
					sleep(idleWaitMillis);
					continue;
				}

				// メッセージ実行
				execute(message);
			} catch (InterruptedException e) {
				// 速やかに終了する
				break;
			} catch (RuntimeException e) {
				log.debug("処理実行失敗.", e);
			}
		}
	}

	public void stop() {
		log.debug("stop() called");
		stateLock.lock();
		try {
			stopping = true;
			stopped.signalAll();
		} finally {
			stateLock.unlock();
		}
	}
	
	protected void sleep(long ms) throws InterruptedException {
		stateLock.lock();
		try {
			if (stopping) {
				return;
			}

			stopped.await(ms, TimeUnit.MILLISECONDS);
		} finally {
			stateLock.unlock();
		}
	}

	protected abstract T poll() throws InterruptedException;
	protected abstract void execute(T message) throws InterruptedException;

}
