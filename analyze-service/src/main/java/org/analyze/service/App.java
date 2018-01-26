package org.analyze.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.or.OpenReplicator;
import com.google.code.or.common.util.MySQLConstants;
import com.jd.fishbaby.domain.BinlogMasterStatus;
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
        	Client client = new EsClientBuilder().init();
        	BulkRequestBuilder builder = client.prepareBulk();
        	ObjectMapper mapper = new ObjectMapper();
        	byte[] jsonObj;
        	
            while(true){
                if(CDCEventManager.queue.isEmpty() == false) {
                    CDCEvent ce = CDCEventManager.queue.pollFirst();
                    //Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                    //String prettyStr1 = gson.toJson(ce);
                    //System.out.println(ce.toString());
                    logger.info(ce.toString());
                    try {
						jsonObj = mapper.writeValueAsBytes(ce);
						builder.add(client.prepareIndex(ce.getDataBaseName(), ce.getTablesName()).
									setSource(jsonObj).setId(ce.getEventId() + ""));
						BulkResponse response = builder.get();
				        logger.info(response.getItems().toString());
				        SearchRequestBuilder sb = client.prepareSearch(ce.getDataBaseName()).setTypes(ce.getTablesName())
				        			.setSearchType(SearchType.QUERY_THEN_FETCH);
				       // SearchResponse sResponse = sb.setQuery(getQueryBuilder(ce)).execute().actionGet();
				        //GetResponse response2 =  client.prepareGet().get();
				        
				        SearchResponse response2 = sb.setSearchType(SearchType.QUERY_THEN_FETCH)
				        						.setQuery(QueryBuilders.termQuery("eventId", "2")).execute().actionGet();
				        logger.info(response2.getHits().toString());
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

		private QueryBuilder getQueryBuilder(CDCEvent ce) {
			return QueryBuilders
					.queryStringQuery(ce.getDataBaseName())
					.field("df_news");
		}       
    }
}
