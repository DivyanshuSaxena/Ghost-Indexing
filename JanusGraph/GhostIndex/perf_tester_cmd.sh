#!/bin/bash                                                                                                                                                                                                    

# Arguments:
# $1 : Name of the class of query to be run
# $2 : Parameter file to be chosen
# $3 : Dataset to be chosen

/usr/lib/jvm/java-8-openjdk-amd64/bin/java -javaagent:/home/baadalvm/Installers/intellij/lib/idea_rt.jar=44082:/home/baadalvm/Installers/intellij/bin -Dfile.encoding=UTF-8 -classpath /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/charsets.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/icedtea-sound.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jce.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jsse.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/resources.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/baadalvm/Projects/Ghost-Indexing/JanusGraph/GhostIndex/conf/local:/home/baadalvm/Projects/Ghost-Indexing/JanusGraph/GhostIndex/Utilities/target/classes:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-all/0.4.0/janusgraph-all-0.4.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-core/0.4.0/janusgraph-core-0.4.0.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/gremlin-core/3.4.1/gremlin-core-3.4.1.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/gremlin-shaded/3.4.1/gremlin-shaded-3.4.1.jar:/home/baadalvm/.m2/repository/org/yaml/snakeyaml/1.15/snakeyaml-1.15.jar:/home/baadalvm/.m2/repository/org/javatuples/javatuples/1.2/javatuples-1.2.jar:/home/baadalvm/.m2/repository/com/jcabi/jcabi-manifests/1.1/jcabi-manifests-1.1.jar:/home/baadalvm/.m2/repository/com/jcabi/jcabi-log/0.14/jcabi-log-0.14.jar:/home/baadalvm/.m2/repository/com/squareup/javapoet/1.8.0/javapoet-1.8.0.jar:/home/baadalvm/.m2/repository/net/objecthunter/exp4j/0.4.8/exp4j-0.4.8.jar:/home/baadalvm/.m2/repository/org/slf4j/jcl-over-slf4j/1.7.25/jcl-over-slf4j-1.7.25.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/gremlin-groovy/3.4.1/gremlin-groovy-3.4.1.jar:/home/baadalvm/.m2/repository/org/apache/ivy/ivy/2.3.0/ivy-2.3.0.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy/2.5.6/groovy-2.5.6-indy.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-groovysh/2.5.6/groovy-groovysh-2.5.6-indy.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-cli-picocli/2.5.6/groovy-cli-picocli-2.5.6.jar:/home/baadalvm/.m2/repository/info/picocli/picocli/3.9.2/picocli-3.9.2.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-console/2.5.6/groovy-console-2.5.6.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-swing/2.5.6/groovy-swing-2.5.6.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-templates/2.5.6/groovy-templates-2.5.6.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-xml/2.5.6/groovy-xml-2.5.6.jar:/home/baadalvm/.m2/repository/jline/jline/2.14.6/jline-2.14.6.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-json/2.5.6/groovy-json-2.5.6-indy.jar:/home/baadalvm/.m2/repository/org/codehaus/groovy/groovy-jsr223/2.5.6/groovy-jsr223-2.5.6-indy.jar:/home/baadalvm/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar:/home/baadalvm/.m2/repository/com/github/ben-manes/caffeine/caffeine/2.3.1/caffeine-2.3.1.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/tinkergraph-gremlin/3.4.1/tinkergraph-gremlin-3.4.1.jar:/home/baadalvm/.m2/repository/org/glassfish/javax.json/1.0/javax.json-1.0.jar:/home/baadalvm/.m2/repository/io/dropwizard/metrics/metrics-core/3.2.2/metrics-core-3.2.2.jar:/home/baadalvm/.m2/repository/io/dropwizard/metrics/metrics-ganglia/3.2.2/metrics-ganglia-3.2.2.jar:/home/baadalvm/.m2/repository/info/ganglia/gmetric4j/gmetric4j/1.0.7/gmetric4j-1.0.7.jar:/home/baadalvm/.m2/repository/io/dropwizard/metrics/metrics-graphite/3.2.2/metrics-graphite-3.2.2.jar:/home/baadalvm/.m2/repository/org/reflections/reflections/0.9.9-RC1/reflections-0.9.9-RC1.jar:/home/baadalvm/.m2/repository/org/javassist/javassist/3.16.1-GA/javassist-3.16.1-GA.jar:/home/baadalvm/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/home/baadalvm/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:/home/baadalvm/.m2/repository/org/locationtech/spatial4j/spatial4j/0.7/spatial4j-0.7.jar:/home/baadalvm/.m2/repository/org/locationtech/jts/jts-core/1.15.0/jts-core-1.15.0.jar:/home/baadalvm/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/home/baadalvm/.m2/repository/commons-configuration/commons-configuration/1.10/commons-configuration-1.10.jar:/home/baadalvm/.m2/repository/commons-io/commons-io/2.3/commons-io-2.3.jar:/home/baadalvm/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar:/home/baadalvm/.m2/repository/com/carrotsearch/hppc/0.7.1/hppc-0.7.1.jar:/home/baadalvm/.m2/repository/com/github/stephenc/high-scale-lib/high-scale-lib/1.1.4/high-scale-lib-1.1.4.jar:/home/baadalvm/.m2/repository/com/google/code/findbugs/jsr305/3.0.0/jsr305-3.0.0.jar:/home/baadalvm/.m2/repository/org/noggit/noggit/0.6/noggit-0.6.jar:/home/baadalvm/.m2/repository/org/apache/commons/commons-text/1.0/commons-text-1.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-server/0.4.0/janusgraph-server-0.4.0.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/gremlin-server/3.4.1/gremlin-server-3.4.1.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/gremlin-driver/3.4.1/gremlin-driver-3.4.1.jar:/home/baadalvm/.m2/repository/com/codahale/metrics/metrics-core/3.0.2/metrics-core-3.0.2.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-cassandra/0.4.0/janusgraph-cassandra-0.4.0.jar:/home/baadalvm/.m2/repository/io/dropwizard/metrics/metrics-jvm/3.2.2/metrics-jvm-3.2.2.jar:/home/baadalvm/.m2/repository/ch/qos/logback/logback-classic/1.1.3/logback-classic-1.1.3.jar:/home/baadalvm/.m2/repository/ch/qos/logback/logback-core/1.1.3/logback-core-1.1.3.jar:/home/baadalvm/.m2/repository/org/apache/cassandra/cassandra-all/2.2.13/cassandra-all-2.2.13.jar:/home/baadalvm/.m2/repository/com/ning/compress-lzf/0.8.4/compress-lzf-0.8.4.jar:/home/baadalvm/.m2/repository/com/googlecode/concurrentlinkedhashmap/concurrentlinkedhashmap-lru/1.4/concurrentlinkedhashmap-lru-1.4.jar:/home/baadalvm/.m2/repository/org/antlr/antlr/3.5.2/antlr-3.5.2.jar:/home/baadalvm/.m2/repository/org/antlr/ST4/4.0.8/ST4-4.0.8.jar:/home/baadalvm/.m2/repository/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar:/home/baadalvm/.m2/repository/com/boundary/high-scale-lib/1.0.6/high-scale-lib-1.0.6.jar:/home/baadalvm/.m2/repository/com/addthis/metrics/reporter-config3/3.0.0/reporter-config3-3.0.0.jar:/home/baadalvm/.m2/repository/com/addthis/metrics/reporter-config-base/3.0.0/reporter-config-base-3.0.0.jar:/home/baadalvm/.m2/repository/org/hibernate/hibernate-validator/4.3.0.Final/hibernate-validator-4.3.0.Final.jar:/home/baadalvm/.m2/repository/org/jboss/logging/jboss-logging/3.1.0.CR2/jboss-logging-3.1.0.CR2.jar:/home/baadalvm/.m2/repository/com/thinkaurelius/thrift/thrift-server/0.3.7/thrift-server-0.3.7.jar:/home/baadalvm/.m2/repository/com/lmax/disruptor/3.0.1/disruptor-3.0.1.jar:/home/baadalvm/.m2/repository/com/clearspring/analytics/stream/2.5.2/stream-2.5.2.jar:/home/baadalvm/.m2/repository/it/unimi/dsi/fastutil/6.5.7/fastutil-6.5.7.jar:/home/baadalvm/.m2/repository/net/sf/supercsv/super-csv/2.1.0/super-csv-2.1.0.jar:/home/baadalvm/.m2/repository/org/apache/thrift/libthrift/0.9.2/libthrift-0.9.2.jar:/home/baadalvm/.m2/repository/org/apache/cassandra/cassandra-thrift/2.2.13/cassandra-thrift-2.2.13.jar:/home/baadalvm/.m2/repository/com/github/jbellis/jamm/0.3.0/jamm-0.3.0.jar:/home/baadalvm/.m2/repository/com/github/tjake/crc32ex/0.1.1/crc32ex-0.1.1.jar:/home/baadalvm/.m2/repository/io/netty/netty-all/4.0.44.Final/netty-all-4.0.44.Final.jar:/home/baadalvm/.m2/repository/joda-time/joda-time/2.4/joda-time-2.4.jar:/home/baadalvm/.m2/repository/org/fusesource/sigar/1.6.4/sigar-1.6.4.jar:/home/baadalvm/.m2/repository/org/eclipse/jdt/core/compiler/ecj/4.4.2/ecj-4.4.2.jar:/home/baadalvm/.m2/repository/org/caffinitas/ohc/ohc-core/0.3.4/ohc-core-0.3.4.jar:/home/baadalvm/.m2/repository/commons-pool/commons-pool/1.6/commons-pool-1.6.jar:/home/baadalvm/.m2/repository/com/netflix/astyanax/astyanax-core/3.10.2/astyanax-core-3.10.2.jar:/home/baadalvm/.m2/repository/com/eaio/uuid/uuid/3.2/uuid-3.2.jar:/home/baadalvm/.m2/repository/com/netflix/astyanax/astyanax-thrift/3.10.2/astyanax-thrift-3.10.2.jar:/home/baadalvm/.m2/repository/com/netflix/astyanax/astyanax-cassandra/3.10.2/astyanax-cassandra-3.10.2.jar:/home/baadalvm/.m2/repository/com/netflix/astyanax/astyanax-cassandra-all-shaded/3.10.2/astyanax-cassandra-all-shaded-3.10.2.jar:/home/baadalvm/.m2/repository/org/apache/servicemix/bundles/org.apache.servicemix.bundles.commons-csv/1.0-r706900_3/org.apache.servicemix.bundles.commons-csv-1.0-r706900_3.jar:/home/baadalvm/.m2/repository/org/codehaus/jettison/jettison/1.3.8/jettison-1.3.8.jar:/home/baadalvm/.m2/repository/stax/stax-api/1.0.1/stax-api-1.0.1.jar:/home/baadalvm/.m2/repository/com/netflix/astyanax/astyanax-recipes/3.10.2/astyanax-recipes-3.10.2.jar:/home/baadalvm/.m2/repository/org/xerial/snappy/snappy-java/1.0.5-M3/snappy-java-1.0.5-M3.jar:/home/baadalvm/.m2/repository/javax/validation/validation-api/1.1.0.Final/validation-api-1.1.0.Final.jar:/home/baadalvm/.m2/repository/net/jpountz/lz4/lz4/1.3.0/lz4-1.3.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-cql/0.4.0/janusgraph-cql-0.4.0.jar:/home/baadalvm/.m2/repository/com/datastax/cassandra/cassandra-driver-core/3.7.1/cassandra-driver-core-3.7.1.jar:/home/baadalvm/.m2/repository/com/github/jnr/jnr-ffi/2.1.7/jnr-ffi-2.1.7.jar:/home/baadalvm/.m2/repository/com/github/jnr/jffi/1.2.16/jffi-1.2.16.jar:/home/baadalvm/.m2/repository/com/github/jnr/jffi/1.2.16/jffi-1.2.16-native.jar:/home/baadalvm/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:/home/baadalvm/.m2/repository/org/ow2/asm/asm-commons/5.0.3/asm-commons-5.0.3.jar:/home/baadalvm/.m2/repository/org/ow2/asm/asm-analysis/5.0.3/asm-analysis-5.0.3.jar:/home/baadalvm/.m2/repository/org/ow2/asm/asm-tree/5.0.3/asm-tree-5.0.3.jar:/home/baadalvm/.m2/repository/org/ow2/asm/asm-util/5.0.3/asm-util-5.0.3.jar:/home/baadalvm/.m2/repository/com/github/jnr/jnr-x86asm/1.0.2/jnr-x86asm-1.0.2.jar:/home/baadalvm/.m2/repository/com/github/jnr/jnr-posix/3.0.44/jnr-posix-3.0.44.jar:/home/baadalvm/.m2/repository/com/github/jnr/jnr-constants/0.9.9/jnr-constants-0.9.9.jar:/home/baadalvm/.m2/repository/io/netty/netty-handler/4.1.25.Final/netty-handler-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/netty/netty-buffer/4.1.25.Final/netty-buffer-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/netty/netty-common/4.1.25.Final/netty-common-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/netty/netty-transport/4.1.25.Final/netty-transport-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/netty/netty-resolver/4.1.25.Final/netty-resolver-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/netty/netty-codec/4.1.25.Final/netty-codec-4.1.25.Final.jar:/home/baadalvm/.m2/repository/io/vavr/vavr/0.9.0/vavr-0.9.0.jar:/home/baadalvm/.m2/repository/io/vavr/vavr-match/0.9.0/vavr-match-0.9.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-berkeleyje/0.4.0/janusgraph-berkeleyje-0.4.0.jar:/home/baadalvm/.m2/repository/com/sleepycat/je/7.5.11/je-7.5.11.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-hbase/0.4.0/janusgraph-hbase-0.4.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-bigtable/0.4.0/janusgraph-bigtable-0.4.0.jar:/home/baadalvm/.m2/repository/com/google/cloud/bigtable/bigtable-hbase-1.x-shaded/1.11.0/bigtable-hbase-1.x-shaded-1.11.0.jar:/home/baadalvm/.m2/repository/org/checkerframework/checker-compat-qual/2.5.2/checker-compat-qual-2.5.2.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-hadoop/0.4.0/janusgraph-hadoop-0.4.0.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/hadoop-gremlin/3.4.1/hadoop-gremlin-3.4.1.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-client/2.7.2/hadoop-client-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-hdfs/2.7.2/hadoop-hdfs-2.7.2.jar:/home/baadalvm/.m2/repository/xerces/xercesImpl/2.9.1/xercesImpl-2.9.1.jar:/home/baadalvm/.m2/repository/org/fusesource/leveldbjni/leveldbjni-all/1.8/leveldbjni-all-1.8.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-mapreduce-client-app/2.7.2/hadoop-mapreduce-client-app-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-mapreduce-client-common/2.7.2/hadoop-mapreduce-client-common-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-yarn-client/2.7.2/hadoop-yarn-client-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-yarn-server-common/2.7.2/hadoop-yarn-server-common-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-mapreduce-client-shuffle/2.7.2/hadoop-mapreduce-client-shuffle-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-yarn-api/2.7.2/hadoop-yarn-api-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-mapreduce-client-core/2.7.2/hadoop-mapreduce-client-core-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-yarn-common/2.7.2/hadoop-yarn-common-2.7.2.jar:/home/baadalvm/.m2/repository/com/sun/jersey/jersey-client/1.9/jersey-client-1.9.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-mapreduce-client-jobclient/2.7.2/hadoop-mapreduce-client-jobclient-2.7.2.jar:/home/baadalvm/.m2/repository/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar:/home/baadalvm/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/home/baadalvm/.m2/repository/org/apache/tinkerpop/spark-gremlin/3.4.1/spark-gremlin-3.4.1.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-core_2.11/2.4.0/spark-core_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/avro/avro-mapred/1.8.2/avro-mapred-1.8.2-hadoop2.jar:/home/baadalvm/.m2/repository/org/apache/avro/avro-ipc/1.8.2/avro-ipc-1.8.2.jar:/home/baadalvm/.m2/repository/com/twitter/chill_2.11/0.9.3/chill_2.11-0.9.3.jar:/home/baadalvm/.m2/repository/com/esotericsoftware/kryo-shaded/4.0.2/kryo-shaded-4.0.2.jar:/home/baadalvm/.m2/repository/com/esotericsoftware/minlog/1.3.0/minlog-1.3.0.jar:/home/baadalvm/.m2/repository/org/objenesis/objenesis/2.5.1/objenesis-2.5.1.jar:/home/baadalvm/.m2/repository/com/twitter/chill-java/0.9.3/chill-java-0.9.3.jar:/home/baadalvm/.m2/repository/org/apache/xbean/xbean-asm6-shaded/4.8/xbean-asm6-shaded-4.8.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-launcher_2.11/2.4.0/spark-launcher_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-kvstore_2.11/2.4.0/spark-kvstore_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-network-common_2.11/2.4.0/spark-network-common_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-network-shuffle_2.11/2.4.0/spark-network-shuffle_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-unsafe_2.11/2.4.0/spark-unsafe_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/slf4j/jul-to-slf4j/1.7.16/jul-to-slf4j-1.7.16.jar:/home/baadalvm/.m2/repository/org/lz4/lz4-java/1.4.0/lz4-java-1.4.0.jar:/home/baadalvm/.m2/repository/com/github/luben/zstd-jni/1.3.2-2/zstd-jni-1.3.2-2.jar:/home/baadalvm/.m2/repository/org/roaringbitmap/RoaringBitmap/0.5.11/RoaringBitmap-0.5.11.jar:/home/baadalvm/.m2/repository/org/json4s/json4s-jackson_2.11/3.5.3/json4s-jackson_2.11-3.5.3.jar:/home/baadalvm/.m2/repository/org/json4s/json4s-core_2.11/3.5.3/json4s-core_2.11-3.5.3.jar:/home/baadalvm/.m2/repository/org/json4s/json4s-ast_2.11/3.5.3/json4s-ast_2.11-3.5.3.jar:/home/baadalvm/.m2/repository/org/json4s/json4s-scalap_2.11/3.5.3/json4s-scalap_2.11-3.5.3.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/core/jersey-client/2.22.2/jersey-client-2.22.2.jar:/home/baadalvm/.m2/repository/javax/ws/rs/javax.ws.rs-api/2.0.1/javax.ws.rs-api-2.0.1.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/hk2-api/2.4.0-b34/hk2-api-2.4.0-b34.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/hk2-utils/2.4.0-b34/hk2-utils-2.4.0-b34.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/external/aopalliance-repackaged/2.4.0-b34/aopalliance-repackaged-2.4.0-b34.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/external/javax.inject/2.4.0-b34/javax.inject-2.4.0-b34.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/hk2-locator/2.4.0-b34/hk2-locator-2.4.0-b34.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/core/jersey-common/2.22.2/jersey-common-2.22.2.jar:/home/baadalvm/.m2/repository/javax/annotation/javax.annotation-api/1.2/javax.annotation-api-1.2.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/bundles/repackaged/jersey-guava/2.22.2/jersey-guava-2.22.2.jar:/home/baadalvm/.m2/repository/org/glassfish/hk2/osgi-resource-locator/1.0.1/osgi-resource-locator-1.0.1.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/core/jersey-server/2.22.2/jersey-server-2.22.2.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/media/jersey-media-jaxb/2.22.2/jersey-media-jaxb-2.22.2.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/containers/jersey-container-servlet/2.22.2/jersey-container-servlet-2.22.2.jar:/home/baadalvm/.m2/repository/org/glassfish/jersey/containers/jersey-container-servlet-core/2.22.2/jersey-container-servlet-core-2.22.2.jar:/home/baadalvm/.m2/repository/io/dropwizard/metrics/metrics-json/3.1.5/metrics-json-3.1.5.jar:/home/baadalvm/.m2/repository/oro/oro/2.0.8/oro-2.0.8.jar:/home/baadalvm/.m2/repository/net/razorvine/pyrolite/4.13/pyrolite-4.13.jar:/home/baadalvm/.m2/repository/net/sf/py4j/py4j/0.10.7/py4j-0.10.7.jar:/home/baadalvm/.m2/repository/org/apache/spark/spark-tags_2.11/2.4.0/spark-tags_2.11-2.4.0.jar:/home/baadalvm/.m2/repository/org/apache/commons/commons-crypto/1.0.0/commons-crypto-1.0.0.jar:/home/baadalvm/.m2/repository/org/spark-project/spark/unused/1.0.0/unused-1.0.0.jar:/home/baadalvm/.m2/repository/org/scala-lang/scala-library/2.11.8/scala-library-2.11.8.jar:/home/baadalvm/.m2/repository/org/scala-lang/modules/scala-xml_2.11/1.0.5/scala-xml_2.11-1.0.5.jar:/home/baadalvm/.m2/repository/com/thoughtworks/paranamer/paranamer/2.6/paranamer-2.6.jar:/home/baadalvm/.m2/repository/io/netty/netty/3.9.9.Final/netty-3.9.9.Final.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/module/jackson-module-scala_2.11/2.6.6/jackson-module-scala_2.11-2.6.6.jar:/home/baadalvm/.m2/repository/org/scala-lang/scala-reflect/2.11.8/scala-reflect-2.11.8.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/module/jackson-module-paranamer/2.6.6/jackson-module-paranamer-2.6.6.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-n3/2.7.10/sesame-rio-n3-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-model/2.7.10/sesame-model-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-api/2.7.10/sesame-rio-api-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-datatypes/2.7.10/sesame-rio-datatypes-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-languages/2.7.10/sesame-rio-languages-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-ntriples/2.7.10/sesame-rio-ntriples-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-rdfxml/2.7.10/sesame-rio-rdfxml-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-util/2.7.10/sesame-util-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-trig/2.7.10/sesame-rio-trig-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-trix/2.7.10/sesame-rio-trix-2.7.10.jar:/home/baadalvm/.m2/repository/org/openrdf/sesame/sesame-rio-turtle/2.7.10/sesame-rio-turtle-2.7.10.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-common/2.7.7/hadoop-common-2.7.7.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-annotations/2.7.7/hadoop-annotations-2.7.7.jar:/home/baadalvm/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/home/baadalvm/.m2/repository/org/apache/commons/commons-math3/3.1.1/commons-math3-3.1.1.jar:/home/baadalvm/.m2/repository/xmlenc/xmlenc/0.52/xmlenc-0.52.jar:/home/baadalvm/.m2/repository/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar:/home/baadalvm/.m2/repository/commons-net/commons-net/3.1/commons-net-3.1.jar:/home/baadalvm/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:/home/baadalvm/.m2/repository/org/mortbay/jetty/jetty/6.1.26/jetty-6.1.26.jar:/home/baadalvm/.m2/repository/org/mortbay/jetty/jetty-util/6.1.26/jetty-util-6.1.26.jar:/home/baadalvm/.m2/repository/org/mortbay/jetty/jetty-sslengine/6.1.26/jetty-sslengine-6.1.26.jar:/home/baadalvm/.m2/repository/javax/servlet/jsp/jsp-api/2.1/jsp-api-2.1.jar:/home/baadalvm/.m2/repository/com/sun/jersey/jersey-core/1.9/jersey-core-1.9.jar:/home/baadalvm/.m2/repository/com/sun/jersey/jersey-json/1.9/jersey-json-1.9.jar:/home/baadalvm/.m2/repository/com/sun/xml/bind/jaxb-impl/2.2.3-1/jaxb-impl-2.2.3-1.jar:/home/baadalvm/.m2/repository/javax/xml/bind/jaxb-api/2.2.2/jaxb-api-2.2.2.jar:/home/baadalvm/.m2/repository/javax/xml/stream/stax-api/1.0-2/stax-api-1.0-2.jar:/home/baadalvm/.m2/repository/javax/activation/activation/1.1/activation-1.1.jar:/home/baadalvm/.m2/repository/com/sun/jersey/jersey-server/1.9/jersey-server-1.9.jar:/home/baadalvm/.m2/repository/asm/asm/3.1/asm-3.1.jar:/home/baadalvm/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/home/baadalvm/.m2/repository/net/java/dev/jets3t/jets3t/0.9.0/jets3t-0.9.0.jar:/home/baadalvm/.m2/repository/com/jamesmurty/utils/java-xmlbuilder/0.4/java-xmlbuilder-0.4.jar:/home/baadalvm/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/baadalvm/.m2/repository/org/slf4j/slf4j-log4j12/1.7.10/slf4j-log4j12-1.7.10.jar:/home/baadalvm/.m2/repository/org/apache/avro/avro/1.7.4/avro-1.7.4.jar:/home/baadalvm/.m2/repository/com/google/protobuf/protobuf-java/2.5.0/protobuf-java-2.5.0.jar:/home/baadalvm/.m2/repository/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-auth/2.7.7/hadoop-auth-2.7.7.jar:/home/baadalvm/.m2/repository/org/apache/directory/server/apacheds-kerberos-codec/2.0.0-M15/apacheds-kerberos-codec-2.0.0-M15.jar:/home/baadalvm/.m2/repository/org/apache/directory/server/apacheds-i18n/2.0.0-M15/apacheds-i18n-2.0.0-M15.jar:/home/baadalvm/.m2/repository/org/apache/directory/api/api-asn1-api/1.0.0-M20/api-asn1-api-1.0.0-M20.jar:/home/baadalvm/.m2/repository/org/apache/directory/api/api-util/1.0.0-M20/api-util-1.0.0-M20.jar:/home/baadalvm/.m2/repository/org/apache/curator/curator-framework/2.7.1/curator-framework-2.7.1.jar:/home/baadalvm/.m2/repository/com/jcraft/jsch/0.1.54/jsch-0.1.54.jar:/home/baadalvm/.m2/repository/org/apache/curator/curator-client/2.7.1/curator-client-2.7.1.jar:/home/baadalvm/.m2/repository/org/apache/curator/curator-recipes/2.7.1/curator-recipes-2.7.1.jar:/home/baadalvm/.m2/repository/org/apache/htrace/htrace-core/3.1.0-incubating/htrace-core-3.1.0-incubating.jar:/home/baadalvm/.m2/repository/org/apache/zookeeper/zookeeper/3.4.6/zookeeper-3.4.6.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-es/0.4.0/janusgraph-es-0.4.0.jar:/home/baadalvm/.m2/repository/org/elasticsearch/client/elasticsearch-rest-client/6.6.0/elasticsearch-rest-client-6.6.0.jar:/home/baadalvm/.m2/repository/org/apache/httpcomponents/httpasyncclient/4.1.2/httpasyncclient-4.1.2.jar:/home/baadalvm/.m2/repository/org/apache/httpcomponents/httpcore-nio/4.4.5/httpcore-nio-4.4.5.jar:/home/baadalvm/.m2/repository/org/antlr/antlr-runtime/3.2/antlr-runtime-3.2.jar:/home/baadalvm/.m2/repository/org/antlr/stringtemplate/3.2/stringtemplate-3.2.jar:/home/baadalvm/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-solr/0.4.0/janusgraph-solr-0.4.0.jar:/home/baadalvm/.m2/repository/org/apache/solr/solr-solrj/7.0.0/solr-solrj-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/httpcomponents/httpmime/4.4.1/httpmime-4.4.1.jar:/home/baadalvm/.m2/repository/org/codehaus/woodstox/stax2-api/3.1.4/stax2-api-3.1.4.jar:/home/baadalvm/.m2/repository/org/codehaus/woodstox/woodstox-core-asl/4.4.1/woodstox-core-asl-4.4.1.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-analyzers-common/7.0.0/lucene-analyzers-common-7.0.0.jar:/home/baadalvm/.m2/repository/org/janusgraph/janusgraph-lucene/0.4.0/janusgraph-lucene-0.4.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-core/7.0.0/lucene-core-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-queryparser/7.0.0/lucene-queryparser-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-queries/7.0.0/lucene-queries-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-sandbox/7.0.0/lucene-sandbox-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-spatial/7.0.0/lucene-spatial-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-spatial-extras/7.0.0/lucene-spatial-extras-7.0.0.jar:/home/baadalvm/.m2/repository/org/apache/lucene/lucene-spatial3d/7.0.0/lucene-spatial3d-7.0.0.jar:/home/baadalvm/.m2/repository/org/codehaus/jackson/jackson-mapper-asl/1.9.13/jackson-mapper-asl-1.9.13.jar:/home/baadalvm/.m2/repository/org/codehaus/jackson/jackson-core-asl/1.9.13/jackson-core-asl-1.9.13.jar:/home/baadalvm/.m2/repository/org/codehaus/jackson/jackson-xc/1.9.13/jackson-xc-1.9.13.jar:/home/baadalvm/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.9.13/jackson-jaxrs-1.9.13.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.6.6/jackson-core-2.6.6.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.6.6/jackson-databind-2.6.6.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-json-org/2.6.6/jackson-datatype-json-org-2.6.6.jar:/home/baadalvm/.m2/repository/org/apache/geronimo/bundles/json/20090211_1/json-20090211_1.jar:/home/baadalvm/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.6.6/jackson-annotations-2.6.6.jar:/home/baadalvm/.m2/repository/org/apache/hbase/hbase-shaded-client/2.1.5/hbase-shaded-client-2.1.5.jar:/home/baadalvm/.m2/repository/com/github/stephenc/findbugs/findbugs-annotations/1.3.9-1/findbugs-annotations-1.3.9-1.jar:/home/baadalvm/.m2/repository/org/apache/htrace/htrace-core4/4.2.0-incubating/htrace-core4-4.2.0-incubating.jar:/home/baadalvm/.m2/repository/org/apache/yetus/audience-annotations/0.5.0/audience-annotations-0.5.0.jar:/home/baadalvm/.m2/repository/org/apache/hbase/hbase-shaded-mapreduce/2.1.5/hbase-shaded-mapreduce-2.1.5.jar:/home/baadalvm/.m2/repository/org/apache/hadoop/hadoop-distcp/2.7.7/hadoop-distcp-2.7.7.jar:/home/baadalvm/.m2/repository/org/slf4j/slf4j-api/1.7.12/slf4j-api-1.7.12.jar:/home/baadalvm/.m2/repository/com/opencsv/opencsv/3.8/opencsv-3.8.jar:/home/baadalvm/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar:/home/baadalvm/.m2/repository/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.jar:/home/baadalvm/.m2/repository/org/slf4j/slf4j-simple/1.7.21/slf4j-simple-1.7.21.jar:/home/baadalvm/.m2/repository/org/apache/httpcomponents/httpcore/4.4.12/httpcore-4.4.12.jar:/home/baadalvm/.m2/repository/org/apache/httpcomponents/httpclient/4.5.9/httpclient-4.5.9.jar:/home/baadalvm/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/home/baadalvm/.m2/repository/commons-codec/commons-codec/1.11/commons-codec-1.11.jar main.PerformanceTester $1 $2 1 $3 local
