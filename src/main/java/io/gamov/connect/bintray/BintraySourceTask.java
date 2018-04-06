package io.gamov.connect.bintray;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.connect.data.Struct;
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

import io.gamov.connect.bintray.model.FirehoseEvent;
import io.gamov.connect.bintray.model.FirehoseEventConverter;
import io.gamov.connect.bintray.util.HttpClient;
import io.gamov.connect.bintray.util.HttpsURLConnectionClient;

import static io.gamov.connect.bintray.model.FirehoseEventConverter.*;

public class BintraySourceTask extends SourceTask {

  static final Logger log = LoggerFactory.getLogger(BintraySourceTask.class);
  final ConcurrentLinkedDeque<SourceRecord> messageQueue = new ConcurrentLinkedDeque<>();

  HttpClient httpClient;
  BintraySourceConnectorConfig connectorConfig;
  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    //TODO: Do things here that are required to start your task. This could be open a connection to a database, etc.
    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
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
    try {
      Map<String, ?> sourcePartition = Collections.emptyMap();
      Map<String, ?> sourceOffset = Collections.emptyMap();
      System.out.println(s);
      log.debug("raw event: ", s);
      FirehoseEvent event = null;
      try {
        if(!"".equals(s)){
          event = objectMapper.readValue(s, FirehoseEvent.class);
          System.out.println(event);
        }
      } catch (IOException e) {
        log.error("Error in parsing json", e);
      }

      Struct keyStruct = new Struct(FIRE_SCHEMA_KEY);
      Struct valueStruct = new Struct(FIREHOSE_EVENT_SCHEMA);

      convertKey(event, keyStruct);
      convert(event, valueStruct);

      SourceRecord
          record =
          new SourceRecord(sourcePartition,
                           sourceOffset,
                           this.connectorConfig.getBintrayKafkaTopic(),
                           FirehoseEventConverter.FIRE_SCHEMA_KEY, keyStruct,
                           FirehoseEventConverter.FIREHOSE_EVENT_SCHEMA, valueStruct);

      this.messageQueue.add(record);
    } catch (Exception ex) {
      if (log.isErrorEnabled()) {
        log.error("Exception thrown", ex);
      }
    }

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