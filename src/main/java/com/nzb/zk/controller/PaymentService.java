package com.nzb.zk.controller;

import org.springframework.beans.factory.annotation.Autowired;

public class PaymentService {

	@Autowired
	private PaymentDao dao;

	public Payment getById() {
		return dao.findById();
	}

}
