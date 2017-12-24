package com.nzb.zk.curator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CreateNodeAsyncDemo {
	static CountDownLatch cdl = new CountDownLatch(2);
	static ExecutorService es = Executors.newFixedThreadPool(2);
	
	public static void main(String[] args) throws Exception {
		String path = "/zk-client";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.159.152:2181")
		.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			@Override
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("event code: " + event.getResultCode() + ", type:" + event.getType());
			}
		}, es).forPath(path, "test".getBytes());
		
		client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			
			@Override
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("event code: " + event.getResultCode() + ", type: " + event.getType());
				cdl.countDown();
			}
		}, es).forPath(path, "test".getBytes());
		
		cdl.await();
        es.shutdown();
	}

}
