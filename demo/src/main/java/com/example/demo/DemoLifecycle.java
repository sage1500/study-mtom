package com.example.demo;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class DemoLifecycle extends SimpleGracefulShutdownSmartLifecycleSupport {

	@Value("${demo.core-pool-size:5}")
	private int corePoolSize = 1;

	@Value("${demo.max-pool-size:50}")
	private int maxPoolSize = 5;

	@Value("${demo.schedule-period:5000}")
	private long schedulePeriod = 5000;

	private ThreadPoolTaskExecutor parentExecutor;
	private ThreadPoolTaskExecutor childExecutor;
	private ThreadPoolTaskScheduler scheduler;

	private DemoParentTask parentTask;
	private ScheduledFuture<?> schedulerFuture;

	@PostConstruct
	public void init() {
		// 親Executor
		parentExecutor = new TaskExecutorBuilder() //
				.threadNamePrefix(this.getClass().getSimpleName() + "-parent-") //
				.awaitTermination(false) //
				.corePoolSize(1) //
				.taskDecorator(this) //
				.build();
		parentExecutor.afterPropertiesSet();

		// 子Executor
		childExecutor = new TaskExecutorBuilder() //
				.threadNamePrefix(this.getClass().getSimpleName() + "-child-") //
				.awaitTermination(false) //
				.corePoolSize(corePoolSize) //
				//.maxPoolSize(maxPoolSize) //
				//.queueCapacity(Integer.MAX_VALUE) //
				.taskDecorator(this) //
				.build();
		childExecutor.afterPropertiesSet();

		// スケジューラ
		scheduler = new TaskSchedulerBuilder() //
				.threadNamePrefix(this.getClass().getSimpleName() + "-scheduler-") //
				.awaitTermination(false) //
				.poolSize(1) //
				.build();
		scheduler.afterPropertiesSet();

		// 親タスク
		parentTask = new DemoParentTask(childExecutor);
	}

	@PreDestroy
	public void destroy() {
		if (parentExecutor != null) {
			parentExecutor.shutdown();
		}
		if (childExecutor != null) {
			childExecutor.shutdown();
		}
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}

	@Override
	public void start() {
		super.start();

		// 親タスク登録
		parentExecutor.execute(parentTask);

		// スケジュール登録
		schedulerFuture = scheduler.scheduleAtFixedRate(decorate(new DemoScheduledTask()),
				Duration.ofMillis(schedulePeriod));
	}

	@Override
	public void stop() {
		// 停止通知
		parentTask.stop();
		
		// スケジュール解除
		if (schedulerFuture != null) {
			schedulerFuture.cancel(false);
		}
	}

}
