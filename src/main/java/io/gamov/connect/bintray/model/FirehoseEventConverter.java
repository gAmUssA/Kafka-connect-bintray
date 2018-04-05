package io.gamov.connect.bintray.model;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

public class FirehoseEventConverter {

  public final static Schema FIREHOSE_EVENT_SCHEMA =
      SchemaBuilder.string()
          .name("io.gamov.bintray.model.FirehoseEvent")
          .doc("Bintray firehose event")
          .field("Type", SchemaBuilder.string().build())
          .field("Path", SchemaBuilder.string().build())
          .field("UserAgent", SchemaBuilder.string().build())
          .field("ContentLength", SchemaBuilder.int64().build())
          .field("IpAddress", SchemaBuilder.string().build())
          .field("Subject", SchemaBuilder.string().build())
          .field("Time", SchemaBuilder.string().build())
          .build();

  public final static Schema FIRE_SCHEMA_KEY =
      SchemaBuilder.struct()
          .name("io.gamov.bintray.model.FirehoseEvent.IpAddress")
          .doc("Key for a firehose event.")
          .field("IpAddress", Schema.STRING_SCHEMA)
          .build();

  public static void convertKey(FirehoseEvent event, Struct struct) {
    struct.put("IpAddress", event.getIpAddress());
  }

  public static void convert(FirehoseEvent event, Struct struct) {
    struct
        .put("Type", event.getType())
        .put("Path", event.getPath())
        .put("UserAgent", event.getUserAgent())
        .put("ContentLength", event.getContentLength())
        .put("IpAddress", event.getIpAddress())
        .put("Subject", event.getSubject())
        .put("Time", event.getTime())
    ;
  }

}
