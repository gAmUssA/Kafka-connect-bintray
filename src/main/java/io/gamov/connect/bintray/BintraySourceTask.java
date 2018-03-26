package io.gamov.connect.bintray;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import io.gamov.connect.bintray.util.HttpClient;
import io.gamov.connect.bintray.util.HttpsURLConnectionClient;

public class BintraySourceTask extends SourceTask {

  static final Logger log = LoggerFactory.getLogger(BintraySourceTask.class);
  final ConcurrentLinkedDeque<SourceRecord> messageQueue = new ConcurrentLinkedDeque<>();

  HttpClient httpClient;
  BintraySourceConnectorConfig connectorConfig;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    //TODO: Do things here that are required to start your task. This could be open a connection to a database, etc.
    connectorConfig = new BintraySourceConnectorConfig(map);
    String streamingApiUrl = connectorConfig.getStreamingApiUrl();
    String bintrayOrg = connectorConfig.getBintrayOrg();
    String bintrayUser = connectorConfig.getBintrayUser();
    String bintrayApiKey = connectorConfig.getBintrayApiKey();

    httpClient = new HttpsURLConnectionClient();
    try {
      httpClient.openHttpsConnection(streamingApiUrl + "/" + bintrayOrg, bintrayUser,
                                     bintrayApiKey, handler);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private Consumer<String> handler = s -> {
    Map<String, ?> sourcePartition = Collections.emptyMap();
    Map<String, ?> sourceOffset = Collections.emptyMap();
    System.out.println(s);

    //TODO: SourceRecord
  };

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    List<SourceRecord> records = new ArrayList<>(256);

    while (records.isEmpty()) {
      int size = messageQueue.size();

      for (int i = 0; i < size; i++) {
        SourceRecord record = this.messageQueue.poll();

        if (null == record) {
          break;
        }

        records.add(record);
      }

      if (records.isEmpty()) {
        Thread.sleep(100);
      }
    }

    return records;
  }

  @Override
  public void stop() {
    //TODO: Do whatever is required to stop your task.
    httpClient.closeConnection();
  }
}