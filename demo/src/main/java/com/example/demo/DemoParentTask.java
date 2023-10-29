package com.example.demo;

import org.springframework.core.task.TaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DemoParentTask extends SimpleGracefulShutdownPollerTaskSupport<Integer> {

	private final TaskExecutor executor;
	private final DemoMapper mapper;

	@Override
	protected Integer poll() throws InterruptedException {
		// ダミー実装
		dummy++;
		return (dummy % 2 == 0) ? dummy : null;
	}

	private int dummy = 0;

	@Override
	protected void execute(Integer message) throws InterruptedException {
		// 子タスクで実行
		executor.execute(new DemoChildTask(message, mapper));
	}
}
