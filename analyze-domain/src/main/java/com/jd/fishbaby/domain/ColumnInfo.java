package com.jd.fishbaby.domain;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:18:35
* $
*/
public class ColumnInfo {
	private String name;
	private String type;
	public ColumnInfo(String name, String type) {
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
