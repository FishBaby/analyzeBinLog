package com.jd.fishbaby.analyze.core;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月26日 下午2:32:16
* $
*/
public interface AnalyzeCoreInterface {
	
	public void analyzeThreadStart(String host, String user, String password, int port);
	public void analyzeThreadStart(String host, String user, String password, int port, int startPosition);
	public void analyzeThreadStop();
	
}
