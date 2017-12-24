package com.nzb.zk.zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZKDemo implements Watcher{
	private static final CountDownLatch cdl = new CountDownLatch(1);
	private static Logger logger = Logger.getLogger(ZKDemo.class);
	
	public static void main(String[] args) throws IOException {
		ZooKeeper zk = new ZooKeeper("192.168.159.152:2181", 10000, new ZKDemo());
		logger.info("zk state: " + zk.getState());
		
		try {
			cdl.await();
		} catch (InterruptedException e) {
			logger.info("zk session established.");
		}
		
		
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("Received watched:" + event);
		if (KeeperState.SyncConnected == event.getState()) {
			cdl.countDown();
		}
	}

}
