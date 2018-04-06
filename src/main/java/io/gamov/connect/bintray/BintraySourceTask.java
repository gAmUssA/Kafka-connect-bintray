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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import io.gamov.connect.bintray.model.FirehoseEvent;
import io.gamov.connect.bintray.model.FirehoseEventConverter;
import io.gamov.connect.bintray.util.HttpClient;
import io.gamov.connect.bintray.util.HttpsURLConnectionClient;

import static io.gamov.connect.bintray.model.FirehoseEventConverter.*;

public class BintraySourceTask extends SourceTask {

  private static final Logger log = LoggerFactory.getLogger(BintraySourceTask.class);

  private final ConcurrentLinkedDeque<SourceRecord> messageQueue = new ConcurrentLinkedDeque<>();
  private HttpClient httpClient;
  private BintraySourceConnectorConfig connectorConfig;
  private ObjectMapper objectMapper = new ObjectMapper();
  private ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    //Do things here that are required to start your task.
    // This could be open a connection to a database, etc.

    objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    connectorConfig = new BintraySourceConnectorConfig(map);

    executor.submit(new MyHttpStreamTask(connectorConfig.getStreamingApiUrl(),
                                         connectorConfig.getBintrayOrg(),
                                         connectorConfig.getBintrayUser(),
                                         connectorConfig.getBintrayApiKey()));
  }

  private class MyHttpStreamTask implements Runnable {

    private String streamingApiUrl;
    private String bintrayOrg;
    private String bintrayUser;
    private String bintrayApiKey;

    private MyHttpStreamTask(String streamingApiUrl, String bintrayOrg, String bintrayUser,
                             String bintrayApiKey) {
      this.streamingApiUrl = streamingApiUrl;
      this.bintrayOrg = bintrayOrg;
      this.bintrayUser = bintrayUser;
      this.bintrayApiKey = bintrayApiKey;
    }

    @Override
    public void run() {
      httpClient = new HttpsURLConnectionClient();
      try {
        httpClient.openHttpsConnection(streamingApiUrl + "/" + bintrayOrg, bintrayUser,
                                       bintrayApiKey, handler);
      } catch (IOException e) {
        log.error("error: ", e);
      }
    }
  }

  private Consumer<String> handler = s -> {
    try {
      Map<String, ?> sourcePartition = Collections.emptyMap();
      Map<String, ?> sourceOffset = Collections.emptyMap();
      System.out.println(s);
      log.debug("raw event: ", s);
      if (!"".equals(s)) {
        FirehoseEvent event = objectMapper.readValue(s, FirehoseEvent.class);
        System.out.println(event);

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
      }
    } catch (Exception ex) {
      if (log.isErrorEnabled()) {
        log.error("Exception thrown", ex);
      }
    }
  };

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    log.debug("================ Poll =========================");
    List<SourceRecord> records = new ArrayList<>(256);

    while (records.isEmpty()) {
      int size = messageQueue.size();

      for (int i = 0; i < size; i++) {
        SourceRecord record = this.messageQueue.poll();

        if (null == record) {
          break;
        }

        records.add(record);
        log.debug("polled " + size + " records");
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
    executor.shutdown();
    httpClient.closeConnection();
  }
}