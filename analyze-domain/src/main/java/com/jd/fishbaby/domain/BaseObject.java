package com.jd.fishbaby.domain;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月29日 下午2:14:44
* $
*/

import java.util.Date;
import java.util.Map;

import com.jd.fishbaby.enums.EventTypeEnum;

public class BaseObject {
	private String databaseName = null;
	private String tableName = null;
	private EventTypeEnum eventType;
	private Date eventOccurTime = null;
	private Map<String, String> fieldAndValue = null;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public EventTypeEnum getEventType() {
		return eventType;
	}

	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public Date getEventOccurTime() {
		return eventOccurTime;
	}

	public void setEventOccurTime(Date eventOccurTime) {
		this.eventOccurTime = eventOccurTime;
	}

	public Map<String, String> getFieldAndValue() {
		return fieldAndValue;
	}

	public void setFieldAndValue(Map<String, String> fieldAndValue) {
		this.fieldAndValue = fieldAndValue;
	}
}
