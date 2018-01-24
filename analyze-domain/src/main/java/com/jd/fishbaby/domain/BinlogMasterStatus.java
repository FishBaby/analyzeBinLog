package com.jd.fishbaby.domain;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:44:40
* $
*/
public class BinlogMasterStatus {
	private String binlogName;
	private long position;
	public BinlogMasterStatus(String binlogName, long position) {
		super();
		this.binlogName = binlogName;
		this.position = position;
	}
	public BinlogMasterStatus() {
	}
	public String getBinlogName() {
		return binlogName;
	}
	public void setBinlogName(String binlogName) {
		this.binlogName = binlogName;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
}
