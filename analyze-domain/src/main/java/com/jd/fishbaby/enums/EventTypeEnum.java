package com.jd.fishbaby.enums;

/**
 * Created with Eclipse.
 * 
 * @author yuminghui3
 * @version 创建时间：2018年1月29日 下午2:18:02 $
 */
public enum EventTypeEnum {
	EVENT_DELETE(32, "Delete"), EVENT_UPDATE(31, "Update"), EVENT_INSERT(30, "Insert");

	private Integer value;
	private String eventName;
	
	EventTypeEnum(Integer value, String eventName) {
		this.value = value;
		this.eventName = eventName;
	}
	
	public Integer value() {
		return value;
	}
	
	public String toString() {
		return eventName;
	}
	public static EventTypeEnum of(final Integer value) {
		if(null == value){
			return null;
		}
		for (EventTypeEnum ot : EventTypeEnum.values()) {
			if(ot.value().equals(value)){
				return ot;
			}
		}
		return null;
	}
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
}
