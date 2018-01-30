package com.jd.fishbaby.utils;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月24日 下午4:02:03
* $
*/

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClientBuilder {
	public static final Logger LOGGER = LoggerFactory.getLogger(EsClientBuilder.class);

	private String clusterName;
	private Map<String, Integer> ipAddress;
	private TransportClient client;

	public Client init() {
		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", false).build();
		client = new PreBuiltTransportClient(settings);
		for (String ip : ipAddress.keySet()) {
			try {
				client.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(ip), ipAddress.get(ip)));
			} catch (UnknownHostException e) {
				LOGGER.error("elasticSearch ip error!", e);
			}
		}
		return client;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public TransportClient getClient() {
		return client;
	}

	public Map<String, Integer> getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Map<String, Integer> ipAddress) {
		this.ipAddress = ipAddress;
	}
}
