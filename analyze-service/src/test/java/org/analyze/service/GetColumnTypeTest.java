package org.analyze.service;

import com.jd.fishbaby.utils.MysqlConnection;

/**
* Created with Eclipse.
* @author yuminghui3
* @version 创建时间：2018年1月31日 下午1:57:27
* $
*/
public class GetColumnTypeTest {
	private static final String host = "65.49.218.201";
	private static final int port = 3306;
	private static final String user = "root";
	private static final String password = "root";
	public static void main(String[] args) {
		System.err.println(MysqlConnection.getColumnTypes(host, port, "df_news", user, password, "userinfo"));
	}
}
