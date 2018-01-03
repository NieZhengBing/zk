package com.nzb.zk.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributeLock implements Lock {

	private static Logger logger = LoggerFactory.getLogger(DistributeLock.class);
	private static final String ZK_IP_PORT = "192.168.159.152:2181";
	private static final String LOCK_NAME = "/lock";

	private ZkClient client = new ZkClient(ZK_IP_PORT);

	private CountDownLatch cdl = null;

	@Override
	public void lock() {
		if (tryLock()) {
			return;
		}

		waitForLock();
		lock();
	}

	private void waitForLock() {
		IZkDataListener listener = new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				logger.info("get data delete event");
				if (cdl != null) {
					cdl.countDown();
				}
			}
		};

		client.subscribeDataChanges(LOCK_NAME, listener);
		if (client.exists(LOCK_NAME)) {
			try {
				cdl = new CountDownLatch(1);
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		client.unsubscribeDataChanges(LOCK_NAME, listener);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		try {
			client.createPersistent(LOCK_NAME);
			return true;
		} catch (ZkNodeExistsException e) {
			return false;
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		client.delete(LOCK_NAME);
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
