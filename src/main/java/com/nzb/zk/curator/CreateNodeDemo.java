package com.nzb.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CreateNodeDemo {
	
	public static void main(String[] args) throws Exception {
		String path = "/zk-client/c1";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.159.152:2181").sessionTimeoutMs(5000)
		.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
        client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "test".getBytes());
	}

}
