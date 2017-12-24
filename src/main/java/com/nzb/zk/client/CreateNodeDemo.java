package com.nzb.zk.client;

import org.I0Itec.zkclient.ZkClient;

public class CreateNodeDemo {

	public static void main(String[] args) {
		ZkClient client = new ZkClient("192.168.159.152:2181", 5000);
		String path = "/zk-client/c1";
//		create recursive node
		client.createPersistent(path, true);
	}

}
