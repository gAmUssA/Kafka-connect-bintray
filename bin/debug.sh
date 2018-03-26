
#!/usr/bin/env bash

: ${SUSPEND:='n'}

set -e

mvn clean package
export KAFKA_JMX_OPTS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=${SUSPEND},address=5005"
export CLASSPATH="$(find target/kafka-connect-bintray-0.1-SNAPSHOT-package/share/java -type f -name '*.jar' | tr '\n' ':')"

connect-standalone config/connect-avro-docker.properties config/BintraySourceConnector.properties
