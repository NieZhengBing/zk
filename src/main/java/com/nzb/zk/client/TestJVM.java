package com.nzb.zk.client;

public class TestJVM {

	public static int k = 0;
	public static TestJVM t1 = new TestJVM("t1");
	public static TestJVM t2 = new TestJVM("t2");
	public static int i = print("i");
	public static int n = 99;
	public int j = print("j");
	
//	static block
	static {
		print("static");
	}
	
	public TestJVM(String str) {
		System.out.println("ddddd" + (++k) + ":" + str +" i=" + i + " n=" + n);
		++i;
		++n;
	}
	
//	constructor block
	{
		print("constructor");
	}

	private static int print(String str) {
		System.out.println("mmmmmm" + (++k) + ":" + str +" i=" + i + " n=" + n);
		++n;
		return ++i;
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		TestJVM t = new TestJVM("init");
		
	}

}
