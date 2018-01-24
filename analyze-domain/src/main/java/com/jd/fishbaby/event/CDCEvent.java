package com.jd.fishbaby.event;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.google.code.or.binlog.BinlogEventV4Header;
import com.google.code.or.binlog.impl.event.AbstractBinlogEventV4;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午5:38:18
* $
*/
public class CDCEvent {
	private long eventId = 0;//事件的唯一标识
	private String dataBaseName = null;
	private String tablesName = null;
	private int eventType = 0;//事件类型
	private long timeStamp = 0;//事件发生的时间戳
	private long timeStampReceipt = 0;//open-replicator接收到的时间戳
	private String binLogName = null;
	private long position = 0;
	private long nextPosition = 0;
	private long serverId = 0;
	private Map<String , String> before = null;
	private Map<String , String> after = null;
	private Boolean isDdl = null;
	private String sql = null;
	
	private static AtomicLong uuid = new AtomicLong(0);
	public CDCEvent() {

	}
	
	public CDCEvent(final AbstractBinlogEventV4 are, String databaseName, String tableName){
        this.init(are);
        this.dataBaseName = databaseName;
        this.tablesName = tableName;
    }
	
	private void init(AbstractBinlogEventV4 are) {
		this.eventId = uuid.getAndAdd(1);
		BinlogEventV4Header header = are.getHeader();
		
		this.timeStamp = header.getTimestamp();
		this.eventType = header.getEventType();
		this.serverId = header.getServerId();
		this.timeStampReceipt = header.getTimestampOfReceipt();
		this.position = header.getPosition();
		this.nextPosition = header.getNextPosition();
	}

	@Override
	public String toString() {
		return "CDCEvent [eventId=" + eventId + ", dataBaseName=" + dataBaseName + ", tablesName=" + tablesName
				+ ", eventType=" + eventType + ", timeStamp=" + timeStamp + ", timeStampReceipt=" + timeStampReceipt
				+ ", binLogName=" + binLogName + ", position=" + position + ", nextPosition=" + nextPosition
				+ ", serverId=" + serverId + ", before=" + before + ", after=" + after + ", isDdl=" + isDdl + ", sql="
				+ sql + "]";
	}

	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	public String getDataBaseName() {
		return dataBaseName;
	}
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	public String getTablesName() {
		return tablesName;
	}
	public void setTablesName(String tablesName) {
		this.tablesName = tablesName;
	}
	public int getEventType() {
		return eventType;
	}
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStampReceipt() {
		return timeStampReceipt;
	}
	public void setTimeStampReceipt(long timeStampReceipt) {
		this.timeStampReceipt = timeStampReceipt;
	}
	public String getBinLogName() {
		return binLogName;
	}
	public void setBinLogName(String binLogName) {
		this.binLogName = binLogName;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
	public long getNextPosition() {
		return nextPosition;
	}
	public void setNextPosition(long nextPosition) {
		this.nextPosition = nextPosition;
	}
	public long getServerId() {
		return serverId;
	}
	public void setServerId(long serverId) {
		this.serverId = serverId;
	}
	public Map<String, String> getBefore() {
		return before;
	}
	public void setBefore(Map<String, String> before) {
		this.before = before;
	}
	public Map<String, String> getAfter() {
		return after;
	}
	public void setAfter(Map<String, String> after) {
		this.after = after;
	}
	public Boolean getIsDdl() {
		return isDdl;
	}
	public void setIsDdl(Boolean isDdl) {
		this.isDdl = isDdl;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
}
