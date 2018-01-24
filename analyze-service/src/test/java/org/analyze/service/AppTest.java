package org.analyze.service;

import com.google.code.or.OpenReplicator;
import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest {
    static final OpenReplicator or = new OpenReplicator();
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);
    public static void main(String args[]) throws  Exception {
		//
		final OpenReplicator or = new OpenReplicator();
		or.setUser("root");
		or.setPassword("root");
		or.setHost("65.49.218.201");
		or.setPort(3306);
		or.setServerId(6789);
		or.setBinlogPosition(4);
		or.setBinlogFileName("mysql-bin.000003");
		or.setBinlogEventListener(new BinlogEventListener() {
		    public void onEvents(BinlogEventV4 event) {
		    	LOGGER.info("{}", event);
		    }
		});
		or.start();

		//
		LOGGER.info("press 'q' to stop");
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for(String line = br.readLine(); line != null; line = br.readLine()) {
		    if(line.equals("q")) {
		        or.stop(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		        break;
		    }
		}
	}
}
