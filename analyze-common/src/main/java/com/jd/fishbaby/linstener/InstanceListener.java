package com.jd.fishbaby.linstener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.DeleteRowsEvent;
import com.google.code.or.binlog.impl.event.DeleteRowsEventV2;
import com.google.code.or.binlog.impl.event.QueryEvent;
import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.binlog.impl.event.UpdateRowsEvent;
import com.google.code.or.binlog.impl.event.UpdateRowsEventV2;
import com.google.code.or.binlog.impl.event.WriteRowsEvent;
import com.google.code.or.binlog.impl.event.WriteRowsEventV2;
import com.google.code.or.binlog.impl.event.XidEvent;
import com.google.code.or.common.glossary.Column;
import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import com.google.code.or.common.util.MySQLConstants;
import com.jd.fishbaby.domain.ColumnInfo;
import com.jd.fishbaby.domain.TableInfo;
import com.jd.fishbaby.event.CDCEvent;
import com.jd.fishbaby.utils.CDCEventManager;
import com.jd.fishbaby.utils.TableInfoKeeper;

/**
 * Created with Eclipse.
 * 
 * @author yuminghui3
 * @version 创建时间：2018年1月23日 下午6:09:14 $
 */
public class InstanceListener implements BinlogEventListener {
	public static final Logger logger = LoggerFactory.getLogger(InstanceListener.class);

	@Override
	public void onEvents(BinlogEventV4 be) {
		if (be == null) {
			logger.error("binlog event is null");
			return;
		}
		int eventType = be.getHeader().getEventType();
		switch (eventType) {
		case MySQLConstants.FORMAT_DESCRIPTION_EVENT: {
			logger.trace("FORMAT_DESCRIPTION_EVENT");
			break;
		}
		case MySQLConstants.DELETE_ROWS_EVENT_V2: {
			DeleteRowsEventV2 dre = (DeleteRowsEventV2) be;
			long tableId = dre.getTableId();
			logger.info("DELETE ROW EVENT : tableId : {}", tableId);

			TableInfo tableInfo = TableInfoKeeper.getTableInfo(tableId);
			String databaseName = tableInfo.getDataBaseName();
			String tableName = tableInfo.getTableName();

			List<Row> rows = dre.getRows();
			for (Row row : rows) {
				List<Column> before = row.getColumns();
				Map<String, String> beforeMap = getMapList(before, databaseName, tableName);
				if (beforeMap != null && beforeMap.size() > 0) {
					CDCEvent cdcEvent = new CDCEvent(dre, databaseName, tableName);
					cdcEvent.setIsDdl(false);
					cdcEvent.setSql(null);
					cdcEvent.setBefore(beforeMap);
					CDCEventManager.queue.addLast(cdcEvent);
					logger.info("cdcEvent : {}", cdcEvent);
				}
			}
			break;
		}
		case MySQLConstants.UPDATE_ROWS_EVENT_V2: {
			UpdateRowsEventV2 ure = (UpdateRowsEventV2) be;
			long tableId = ure.getTableId();
			logger.info("UPDATA ROWS EVENT : tableId : {}", tableId);

			TableInfo tableInfo = TableInfoKeeper.getTableInfo(tableId);
			String databaseName = tableInfo.getDataBaseName();
			String tableName = tableInfo.getTableName();

			List<Pair<Row>> rows = ure.getRows();
			for (Pair<Row> pair : rows) {
				List<Column> colsBefore = pair.getBefore().getColumns();
				List<Column> colsAfter = pair.getAfter().getColumns();

				Map<String, String> beforeMap = getMapList(colsBefore, databaseName, tableName);
				Map<String, String> afterMap = getMapList(colsAfter, databaseName, tableName);

				if (beforeMap != null && afterMap != null && beforeMap.size() > 0 && afterMap.size() > 0) {
					CDCEvent cdcEvent = new CDCEvent(ure, databaseName, tableName);
					cdcEvent.setIsDdl(false);
					cdcEvent.setSql(null);
					cdcEvent.setBefore(beforeMap);
					cdcEvent.setAfter(afterMap);
					CDCEventManager.queue.addLast(cdcEvent);
					logger.info("cdcEvent : {} ", cdcEvent);
				}
			}
			break;
		}
		case MySQLConstants.WRITE_ROWS_EVENT_V2: {
			WriteRowsEventV2 wre2 = (WriteRowsEventV2)be;
			long tableId = wre2.getTableId();
			TableInfo tableInfo = TableInfoKeeper.getTableInfo(tableId);
			logger.info("WRITE ROWS EVENT : TableId : {}", tableId);

			String databaseName = tableInfo.getDataBaseName();
			String tableName = tableInfo.getTableName();
			List<Row> rows = wre2.getRows();
			for (Row row : rows) {
				List<Column> after = row.getColumns();
				Map<String, String> afterMap = getMapList(after, databaseName, tableName);
				if (afterMap != null && afterMap.size() > 0) {
					CDCEvent cdcEvent = new CDCEvent(wre2, databaseName, tableName);
					cdcEvent.setIsDdl(false);
					cdcEvent.setSql(null);
					cdcEvent.setAfter(afterMap);
					CDCEventManager.queue.addLast(cdcEvent);
					logger.info("cdcEvent : {} ", cdcEvent);
				}
			}
			break;
		}
		// 每次ROW_EVENT前都伴随一个TABLE_MAP_EVENT事件，保存一些表信息，如tableId,
		// tableName, databaseName, 而ROW_EVENT只有tableId
		case MySQLConstants.TABLE_MAP_EVENT: {
			TableMapEvent tme = (TableMapEvent) be;
			TableInfoKeeper.saveTableMap(tme);
			
			TableInfo tableInfo = createTableInfo(tme);
			logger.info("QUERY_EVENT:databaseName:{},tableName:{}", tableInfo.getDataBaseName(), tableInfo.getTableName());
			logger.info("TABLE_MAP_EVENT : tableId : {}", tme.getTableId());
			break;
		}
		case MySQLConstants.XID_EVENT: {
			XidEvent xe = (XidEvent) be;
			logger.info("XID_EVENT: xid:{}", xe.getXid());
			break;
		}
		default: {
			logger.info("DEFAULT:{}", eventType);
			break;
		}
		}

	}

	private TableInfo createTableInfo(TableMapEvent tme) {
		TableInfo tableInfo = new TableInfo();
		tableInfo.setDataBaseName(tme.getDatabaseName().toString());
		tableInfo.setTableName(tme.getTableName().toString());
		return tableInfo;
	}

	private Map<String, String> getMapList(List<Column> cols, String databaseName, String tableName) {
		Map<String, String> map = new HashMap<String, String>();
		if (cols == null || cols.size() == 0) {
			return null;
		}

		String fullName = databaseName + "." + tableName;
		List<ColumnInfo> columnInfos = TableInfoKeeper.getColumns(fullName);
		if (columnInfos == null) {
			return null;
		}
		if (columnInfos.size() != cols.size()) {
			TableInfoKeeper.refershColumnsMap();
			if (columnInfos.size() != cols.size()) {
				logger.warn("columnInfoList.size is not equal to cols.");
				return null;
			}
		}
		for (int i = 0; i < columnInfos.size(); i++) {
			if (cols.get(i).getValue() == null) {
				map.put(columnInfos.get(i).getName(), "");
			} else {
				map.put(columnInfos.get(i).getName(), cols.get(i).toString());
			}
		}
		return map;
	}
}
