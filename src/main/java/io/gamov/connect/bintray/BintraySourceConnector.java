package io.gamov.connect.bintray;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BintraySourceConnector extends SourceConnector {

  private static Logger log = LoggerFactory.getLogger(BintraySourceConnector.class);

  private List<Map<String, String>> configs = new ArrayList<>();

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {
    log.info("[✅] Starting Bintray connector");
    BintraySourceConnectorConfig config = new BintraySourceConnectorConfig(map);

    Map<String, String> taskSettings = new HashMap<>(map);
    this.configs.add(taskSettings);
  }

  @Override
  public Class<? extends Task> taskClass() {
    Class<BintraySourceTask> bintraySourceTaskClass = BintraySourceTask.class;
    log.debug("taskClass: " + bintraySourceTaskClass);
    return bintraySourceTaskClass;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    Preconditions.checkState(maxTasks > 0, "MaxTasks must be greater than 0");

    return this.configs;
  }

  @Override
  public void stop() {
    //TODO: Do things that are necessary to stop your connector.
  }

  @Override
  public ConfigDef config() {
    return BintraySourceConnectorConfig.conf();
  }
}
