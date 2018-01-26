package com.jd.fishbaby.analyze.core.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.OpenReplicator;
import com.jd.fishbaby.analyze.core.AnalyzeCoreInterface;
import com.jd.fishbaby.domain.BinlogMasterStatus;
import com.jd.fishbaby.linstener.InstanceListener;
import com.jd.fishbaby.utils.MysqlConnection;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月26日 下午2:36:30
* $
*/
public class AnalyzeCoreInterfaceImpl implements AnalyzeCoreInterface {
	private static final Logger logger = LoggerFactory.getLogger(AnalyzeCoreInterface.class);
	private static final Integer startPosition = 4;
	private OpenReplicator or = new OpenReplicator();
	@Override
	public void analyzeThreadStart(String host, String user, String password, int port) {
		analyzeThreadStart(host, user, password, port, startPosition);
	}
	@Override
	public void analyzeThreadStart(String host, String user, String password, int port, int startPosition) {
		or.setUser(user);
		or.setPassword(password);
		or.setHost(host);
		or.setPort(port);
		MysqlConnection.setConnection(host, port, user, password);

		BinlogMasterStatus bms = MysqlConnection.getBinlogMasterStatus();
		or.setBinlogFileName(bms.getBinlogName());
		or.setBinlogPosition(startPosition);

		or.setBinlogEventListener(new InstanceListener());

		try {
			or.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	@Override
	public void analyzeThreadStop() {
		try {
			if(or.isRunning()) {
				or.stop(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			}else {
				logger.warn("This analyze thread was stoped!!!");
			}
		} catch (Exception e) {
			logger.error("Analyze thread stop erro!!!", e);
		}
	}
	
	
}
