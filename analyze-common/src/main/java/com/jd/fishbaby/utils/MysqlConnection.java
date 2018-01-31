package com.jd.fishbaby.utils;
/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月23日 下午6:21:34
* $
*/

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.fishbaby.constant.MysqlConstant;
import com.jd.fishbaby.domain.BinlogInfo;
import com.jd.fishbaby.domain.BinlogMasterStatus;
import com.jd.fishbaby.domain.ColumnInfo;

public class MysqlConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlConnection.class);

	private static Connection conn;

	private static String host;
	private static int port;
	private static String user;
	private static String password;

	public static void setConnection(String hostArg, int portArg, String userArg, String passwordArg) {
		try {
			if (conn == null || conn.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");

				host = hostArg;
				port = portArg;
				user = userArg;
				password = passwordArg;

				conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/", user, password);
				LOGGER.info("connected to mysql:{} : {}", user, password);
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static Connection getConnection(String url , String userArg , String passwordArg) {
		Connection rst = null;
		try {
			if (rst == null || rst.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				user = userArg;
				password = passwordArg;

				rst = DriverManager.getConnection(url, user, password);
				LOGGER.info("connected to mysql:{} : {}", user, password);
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return rst;
	}

	public static Connection getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				setConnection(host, port, user, password);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return conn;
	}

	public static Map<String, List<ColumnInfo>> getColumns() {
		Map<String, List<ColumnInfo>> cols = new HashMap<String, List<ColumnInfo>>();
		Connection conn = getConnection();

		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rs = metaData.getCatalogs();
			String tableType[] = { "TABLE" };
			while (rs.next()) {
				String dataBaseName = rs.getString("TABLE_CAT");
				ResultSet result = metaData.getTables(dataBaseName, null, null, tableType);
				while (result.next()) {
					String tableName = result.getString("TABLE_NAME");
					//System.out.println(result.getInt("TABLE_ID"));
					String key = dataBaseName + "." + tableName;
					ResultSet colSet = metaData.getColumns(dataBaseName, null, tableName, null);
					cols.put(key, new ArrayList<ColumnInfo>());
					while (colSet.next()) {
						ColumnInfo columnInfo = new ColumnInfo(colSet.getString("COLUMN_NAME"),
								colSet.getString("TYPE_NAME"));
						cols.get(key).add(columnInfo);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return cols;
	}
	
	public static List<String> getColumnTypes(String host, Integer port, String databaseName, String user,
			String password, String tableName) {
		List<String> rst = new ArrayList<String>();
		java.sql.PreparedStatement pStemt = null;
		String url = String.format(MysqlConstant.CONN_URL, host, port, databaseName);
		Connection conn = getConnection(url, user, password);
		String tableSql = "SELECT * FROM " + tableName;
		try {
			pStemt = conn.prepareStatement(tableSql);
			ResultSetMetaData rsmt = pStemt.getMetaData();
			for (int i = 0; i < rsmt.getColumnCount(); i++) {
				rst.add(rsmt.getColumnTypeName(i + 1));
			}
		} catch (Exception e) {
			LOGGER.error("getColumnTypes failure", e);
		} finally {
			if (pStemt != null) {
				try {
					pStemt.close();
					closeConnection(conn);
				} catch (SQLException e) {
					LOGGER.error("getColumnTypes close pstem and connection failure", e);
				}
			}
		}
		return rst;
	}
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }
	public static List<BinlogInfo> getBinlogInfo() {
		List<BinlogInfo> binlogList = new ArrayList<BinlogInfo>();
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery("show binary logs");
			while (resultSet.next()) {
				BinlogInfo binlogInfo = new BinlogInfo(resultSet.getString("Log_name"), resultSet.getLong("File_size"));
				binlogList.add(binlogInfo);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return binlogList;
	}

	public static BinlogMasterStatus getBinlogMasterStatus() {
		BinlogMasterStatus binlogMasterStatus = new BinlogMasterStatus();

		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery("show master status");
			while (resultSet.next()) {
				binlogMasterStatus.setBinlogName(resultSet.getString("File"));
				binlogMasterStatus.setPosition(resultSet.getLong("Position"));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return binlogMasterStatus;
	}

	public static int getServerId() {
		int serverId = 6789;
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery("show variables like 'server_id'");
			while (resultSet.next()) {
				serverId = resultSet.getInt("Value");
			}
		} catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        } finally{
            try {
                if(resultSet != null)
                    resultSet.close();
                if(statement != null)
                    statement.close();
                if(conn != null)
                    conn.close();
            } catch (SQLException e) {
            	LOGGER.error(e.getMessage(),e);
            }
        }
		return serverId;
	}
}
