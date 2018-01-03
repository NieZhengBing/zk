package com.nzb.zk.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderCodeGenerator {

	private static int i = 0;

	public String getOrderCode() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(now) + ++i;
	}

	public static void main(String[] args) {
		OrderCodeGenerator ong = new OrderCodeGenerator();
		for (int i = 0; i < 10; i++) {
			System.out.println(ong.getOrderCode());
		}
	}

}
