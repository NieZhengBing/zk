package com.nzb.zk.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImproveLock implements Lock {
	private static Logger logger = LoggerFactory.getLogger(ImproveLock.class);

	private static final String ZOOKEEPER_IP_PORT = "192.168.159.152:2181";

	private static final String LOCK_PATH = "/LOCK";

	private ZkClient client = new ZkClient(ZOOKEEPER_IP_PORT, 10000, 10000, new SerializableSerializer());

	private CountDownLatch cdl;

	private String beforePath;

	private String currentPath;

	public ImproveLock() {
		if (!this.client.exists(LOCK_PATH)) {
			this.client.createPersistent(LOCK_PATH);
		}
	}

	@Override
	public void lock() {
		if (!tryLock()) {
			waitForLock();
			lock();
		} else {
			logger.info(Thread.currentThread().getName() + " get dustribute lock");
		}
	}

	private void waitForLock() {
		IZkDataListener listener = new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				logger.info(Thread.currentThread().getName() + ": DataDelete event!");
				if (cdl != null) {
					cdl.countDown();
				}
			}
		};

		this.client.subscribeDataChanges(beforePath, listener);
		if (this.client.exists(beforePath)) {
			cdl = new CountDownLatch(1);
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.client.unsubscribeDataChanges(beforePath, listener);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		if (currentPath == null || currentPath.length() <= 0) {
			currentPath = this.client.createEphemeralSequential(LOCK_PATH + "/", "lock");
			System.out.println("-------------------" + currentPath);
		}

		List<String> childrens = this.client.getChildren(LOCK_PATH);
		Collections.sort(childrens);
		if (currentPath.equals(LOCK_PATH + "/" + childrens.get(0))) {
			return true;
		} else {
			int wz = Collections.binarySearch(childrens, currentPath.substring(6));
			beforePath = LOCK_PATH + "/" + childrens.get(wz - 1);
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		client.delete(LOCK_PATH);
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
