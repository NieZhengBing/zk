package com.nzb.zk.client;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class GetChildrenDemo {

	public static void main(String[] args) throws InterruptedException {
		String path = "/zk-client";
		ZkClient client = new ZkClient("192.168.159.152:2181", 5000);
		
		client.subscribeChildChanges(path, new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println(parentPath + " node changed: " + currentChilds);
			}
		});
		
		client.createPersistent(path);
		Thread.sleep(1000);
		System.out.println(client.getChildren(path));
		client.createPersistent(path + "/c1");
		Thread.sleep(1000);
		client.delete(path + "/c1");
		Thread.sleep(1000);
		client.delete(path);
		Thread.sleep(Integer.MAX_VALUE);
	}

}
