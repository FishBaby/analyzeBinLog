package org.analyze.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.OpenReplicator;
import com.jd.fishbaby.analyze.core.impl.EsServiceInterfaceImpl;
import com.jd.fishbaby.domain.BaseObject;
import com.jd.fishbaby.domain.BinlogMasterStatus;
import com.jd.fishbaby.enums.EventTypeEnum;
import com.jd.fishbaby.event.CDCEvent;
import com.jd.fishbaby.linstener.InstanceListener;
import com.jd.fishbaby.utils.CDCEventManager;
import com.jd.fishbaby.utils.EsClientBuilder;
import com.jd.fishbaby.utils.MysqlConnection;

public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static final String host = "65.49.218.201";
	private static final int port = 3306;
	private static final String user = "root";
	private static final String password = "root";

	public static void main(String[] args) {
		OpenReplicator or = new OpenReplicator();
		or.setUser(user);
		or.setPassword(password);
		or.setHost(host);
		or.setPort(port);
		MysqlConnection.setConnection(host, port, user, password);

		BinlogMasterStatus bms = MysqlConnection.getBinlogMasterStatus();
		or.setBinlogFileName(bms.getBinlogName());
		or.setBinlogPosition(4);

		or.setBinlogEventListener(new InstanceListener());

		try {
			or.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		Thread thread = new Thread(new PrintCDCEvent());
		thread.start();
	}
	
	public static class PrintCDCEvent implements Runnable{
		
        @SuppressWarnings("unused")
		@Override
        public void run() {
        	Map<String, Integer> ipAddress =  new HashMap<String, Integer>();
        	ipAddress.put("localhost", 9300);
        	EsClientBuilder esClientBuilder = new EsClientBuilder();
        	esClientBuilder.setClusterName("elasticsearch");
        	esClientBuilder.setIpAddress(ipAddress);
        	Client client = esClientBuilder.init();
        	EsServiceInterfaceImpl esService = new EsServiceInterfaceImpl();
        	esService.setClient(client);
            while(true){
                if(CDCEventManager.queue.isEmpty() == false) {
                    CDCEvent ce = CDCEventManager.queue.pollFirst();
                    if(ce.getDataBaseName().equals("mysql")) {
                    	continue;
                    }
                    try {
						BaseObject baseObject = new BaseObject();
						 if(ce.getEventType() == EventTypeEnum.EVENT_INSERT.value()) {
							baseObject.setFieldAndValue(ce.getBefore());
						} else {
							baseObject.setFieldAndValue(ce.getAfter());
						}
						baseObject.setDatabaseName(ce.getDataBaseName());
						baseObject.setEventOccurTime(new Date(ce.getTimeStamp()));
						baseObject.setEventType(EventTypeEnum.of(ce.getEventType()));
						baseObject.setTableName(ce.getTablesName());
						if(!esService.add(baseObject)) {
							CDCEventManager.queue.add(ce);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                else{
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

		/*private QueryBuilder getQueryBuilder(CDCEvent ce) {
			return QueryBuilders
					.queryStringQuery(ce.getDataBaseName())
					.field("df_news");
		}   */    
    }
}
