package io.gamov.connect.bintray.model;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class FirehoseEventConverter {

  public final static Schema FIREHOSE_EVENT_SCHEMA = SchemaBuilder.string()
      .name("io.gamov.bintray.mode.FirehoseEvent")
      .doc("Bintray firehose event")
      .field("Type", SchemaBuilder.string())
      .field("Path", SchemaBuilder.string())
      ;

  /*private String type;
  private String path;

  // user_agent
  private String userAgent;

  // content_length
  private int contentLengh;

  // ip_address
  private String ipAddress;
  private String subject;
  */private String time;

}
