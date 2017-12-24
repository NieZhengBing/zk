package com.nzb.zk.config;

import java.util.List;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.zaxxer.hikari.HikariDataSource;

public class ZKCentralConfigurer {
	private CuratorFramework zkClient;
	private TreeCache treeCache;

	private String zkServers;
	private String zkPath;
	private int sessionTimeout;
	private Properties props;

	public ZKCentralConfigurer(String zkServers, String zkPath, int sessionTimeout) {
		this.zkServers = zkServers;
		this.zkPath = zkPath;
		this.sessionTimeout = sessionTimeout;
		this.props = new Properties();

		initZKClient();
		getConfigData();
		addZKListener();
	}

	private void addZKListener() {
		TreeCacheListener listener = new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				if (event.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
					getConfigData();
					WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
					HikariDataSource dataSource = (HikariDataSource) ctx.getBean("dataSource");
					System.out.println("==============" + props.getProperty("url"));
					dataSource.setJdbcUrl(props.getProperty("url"));
				}
			}
		};

		treeCache = new TreeCache(zkClient, zkPath);
		try {
			treeCache.start();
			treeCache.getListenable().addListener(listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getConfigData() {
		try {
			List<String> list = zkClient.getChildren().forPath(zkServers);
			for (String key : list) {
				String value = new String(zkClient.getData().forPath(zkPath + "/" + key));
				if (value != null && value.length() > 0) {
					props.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initZKClient() {
		zkClient = CuratorFrameworkFactory.builder().connectString(zkServers).sessionTimeoutMs(sessionTimeout)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		zkClient.start();
	}

	public Properties getProps() {
		return props;
	}

	public void setZkServers(String zkServers) {
		this.zkServers = zkServers;
	}

	public void setZkPath(String zkPath) {
		this.zkPath = zkPath;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}
