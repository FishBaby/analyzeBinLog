package com.jd.fishbaby.domain;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:38:00
* $
*/
public class BinlogInfo {
	private String binlogName;
	private Long fileSize;
	
	
	public BinlogInfo(String binlogName, Long fileSize) {
		this.binlogName = binlogName;
		this.fileSize = fileSize;
	}
	public String getBinlogName() {
		return binlogName;
	}
	public void setBinlogName(String binlogName) {
		this.binlogName = binlogName;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
}
