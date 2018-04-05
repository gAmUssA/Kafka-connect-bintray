package io.gamov.connect.bintray;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;


public class BintraySourceConnectorConfig extends AbstractConfig {

  public static final String BINTRAY_API_URL = "bintray.api.url";
  private static final String BINTRAY_API_URL_DOC = "Url to bintray api server. almost always "
                                                    + "https://api.bintray.com/";

  public static final String BINTRAY_API_KEY = "bintray.api.key";
  private static final String BINTRAY_API_KEY_DOC = "User's API key. Can be found Bintray->Edit "
                                                    + "Profile->API Key";

  public static final String BINTRAY_USER = "bintray.user";
  public static final String BINTRAY_USER_DOC = "Bintray User";

  public static final String BINTRAY_ORG = "bintray.org";
  public static final String BINTRAY_ORG_DOC = "Bintray organization to subscribe to";

  public static final String BINTRAY_KAFKA_TOPIC = "bintray.kafka.topic";
  public static final String BINTRAY_KAFKA_TOPIC_DOC = "Kafka topic to write the bintray firehose"
                                                       + " event";

  public BintraySourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);
  }

  public BintraySourceConnectorConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
        .define(BINTRAY_API_URL, Type.STRING, Importance.HIGH, BINTRAY_API_URL_DOC)
        .define(BINTRAY_API_KEY, Type.STRING, Importance.HIGH, BINTRAY_API_KEY_DOC)
        .define(BINTRAY_USER, Type.STRING, Importance.HIGH, BINTRAY_USER_DOC)
        .define(BINTRAY_ORG, Type.STRING, Importance.HIGH, BINTRAY_ORG_DOC)
        .define(BINTRAY_KAFKA_TOPIC, Type.STRING, Importance.HIGH, BINTRAY_KAFKA_TOPIC_DOC)
        ;
  }

  public String getBintrayApiKey() {
    return this.getString(BINTRAY_API_KEY);
  }

  public String getBintrayUser() {
    return this.getString(BINTRAY_USER);
  }

  public String getBintrayOrg() {
    return this.getString(BINTRAY_ORG);
  }

  public String getBintrayApiUrl() {
    return this.getString(BINTRAY_API_URL);
  }

  public String getStreamingApiUrl() {
    return this.getBintrayApiUrl() + "/stream";
  }

  public String getBintrayKafkaTopic(){
    return this.getString(BINTRAY_KAFKA_TOPIC);
  }
}
