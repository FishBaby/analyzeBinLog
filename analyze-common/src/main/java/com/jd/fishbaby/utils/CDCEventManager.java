package com.jd.fishbaby.utils;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.jd.fishbaby.event.CDCEvent;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月24日 上午10:42:25
* $
*/
public class CDCEventManager {
	 public static final ConcurrentLinkedDeque<CDCEvent> queue = new ConcurrentLinkedDeque<CDCEvent>();
}
