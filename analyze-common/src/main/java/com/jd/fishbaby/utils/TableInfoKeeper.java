package com.jd.fishbaby.utils;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:16:30
* $
*/

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.common.util.MySQLConstants;
import com.jd.fishbaby.domain.ColumnInfo;
import com.jd.fishbaby.domain.TableInfo;

public class TableInfoKeeper {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableInfoKeeper.class);
	
	private static Map<Long, TableInfo> tabledIdMap = new ConcurrentHashMap<Long, TableInfo>();
	private static Map<String, List<ColumnInfo>> columnsMap = new ConcurrentHashMap<String, List<ColumnInfo>>();
	
	static {
		columnsMap = MysqlConnection.getColumns();
	}
	
	public static void saveTableMap(TableMapEvent tme) {
		long tableId = tme.getTableId();
		tabledIdMap.remove(tableId);
		
		TableInfo table = new TableInfo();
		table.setDataBaseName(tme.getDatabaseName().toString());
		table.setTableName(tme.getTableName().toString());
		table.setFullName(tme.getDatabaseName() + "." + tme.getTableName());
		
		tabledIdMap.put(tableId, table);
	}
	
	public static synchronized void refershColumnsMap() {
		Map<String, List<ColumnInfo>> map = MysqlConnection.getColumns();
		if (map.size() > 0) {
			columnsMap = map;
		}else {
			LOGGER.error("refresh columns map error.");
		}
	}
	
	public static TableInfo getTableInfo(long tableId){
        return tabledIdMap.get(tableId);
    }

    public static List<ColumnInfo> getColumns(String fullName){
        return columnsMap.get(fullName);
    }
}
