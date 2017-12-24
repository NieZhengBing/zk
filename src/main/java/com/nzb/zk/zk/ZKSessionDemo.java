package com.nzb.zk.zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKSessionDemo implements Watcher {

	private static CountDownLatch cdl = new CountDownLatch(1);
	public static void main(String[] args) throws IOException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.159.152:2181", 5000, new ZKSessionDemo());
		cdl.await();

        long sessionId = zk.getSessionId();
        byte[] passwd = zk.getSessionPasswd();
        new ZooKeeper("192.168.159.152:2181", 5000, new ZKSessionDemo(), 11, "test".getBytes());
        new ZooKeeper("192.168.159.152:2181", 5000, new ZKSessionDemo(), sessionId, passwd);
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("Received watched event:" + event);
		if (KeeperState.SyncConnected == event.getState()) {
			cdl.countDown();
		}
	}

}
