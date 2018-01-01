package com.nzb.zk.election;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ZKTomcatValve extends ValveBase {

	private static CuratorFramework client;

	private final static String zkPath = "/Tomcat/ActiveLock";

	private static TreeCache cache;

	@Override
	public void invoke(Request arg0, Response arg1) throws IOException, ServletException {
		client = CuratorFrameworkFactory.builder().connectString("192.168.159.152:2181").connectionTimeoutMs(1000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		try {
			createZKNode(zkPath);
		} catch (Exception e) {
			try {
				addZKNodeListener(zkPath);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	private void createZKNode(String path) throws Exception {
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
		System.out.println("========create success=============");
	}

	private void addZKNodeListener(String path) throws Exception {
		cache = new TreeCache(client, path);
		cache.start();
		cache.getListenable().addListener(new TreeCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				if (event.getData() != null && event.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
					System.out.println("==========master shutdown, create new node==========");
					createZKNode(path);
				}
			}

		});
	}

}
