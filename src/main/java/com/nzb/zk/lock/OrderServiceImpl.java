package com.nzb.zk.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderServiceImpl implements Runnable {

	private static OrderCodeGenerator ong = new OrderCodeGenerator();

	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	private static final int NUM = 100;

	private static CountDownLatch cdl = new CountDownLatch(NUM);

	private Lock lock = new ImproveLock();

	public void createOrder() {
		String orderCode = null;

		lock.lock();
		try {
			ong.getOrderCode();
			System.out.println("insert into db use id: " + orderCode);
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}

		// business code

		logger.info("insert into db use id: " + orderCode);
	}

	@Override
	public void run() {
		try {
			cdl.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		createOrder();
	}

	public static void main(String[] args) {
		for (int i = 0; i <= NUM; i++) {
			new Thread(new OrderServiceImpl()).start();
			cdl.countDown();
		}
	}

}
