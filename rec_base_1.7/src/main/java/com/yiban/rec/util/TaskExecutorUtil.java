package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutorUtil<T> {

	public interface TaskCallable<T>{
		T task(int index);
	}
	
	public static<T> List<T> execute(int count,TaskCallable<T> taskCallable){
		List<T> dataList = new ArrayList<>(count);
		final ExecutorService exec = Executors.newFixedThreadPool(count);
		List<Future<T>> taskList = new ArrayList<Future<T>>(count);
		final CountDownLatch countDownLatch = new CountDownLatch(count);
		for(int i=0;i<count;i++){
			int index = i;
			Future<T> task = exec.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					T t = null;
					try{
						if(taskCallable!=null){
							t = taskCallable.task(index);
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally {
						//计数器减1
						countDownLatch.countDown();
					}
					return t;
				}
			});
			taskList.add(task);
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0;i<count;i++){
			try {
				T data = taskList.get(i).get();
				dataList.add(data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		//关闭线程池  
        exec.shutdown();
		return dataList;
	}
}
