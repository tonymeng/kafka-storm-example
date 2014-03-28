import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import consumer.bolt.FileDumpBolt;
import consumer.bolt.WordCountBolt;
import nl.minvenj.nfi.storm.kafka.KafkaSpout;

/**
 * User: tonymeng
 * Date: 3/25/14
 */
public class LogLineTopology {

  public static void main(String[]args) throws Exception {
    Config config = new Config();
    config.setDebug(true);

    config.put("kafka.spout.topic", "logs");
    config.put("kafka.spout.consumer.group", "test-consumer-group");
    config.put("kafka.zookeeper.connect", "localhost:2181");
    config.put("kafka.consumer.timeout.ms", 4000);

    KafkaSpout spout = new KafkaSpout();
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("kafkaspout", spout);
    builder.setBolt("countbolt", new WordCountBolt()).shuffleGrouping("kafkaspout");
    builder.setBolt("filebolt", new FileDumpBolt("/tmp/filedump")).shuffleGrouping("kafkaspout");
    builder.setBolt("filecountbolt", new FileDumpBolt("/tmp/filedumpcount")).shuffleGrouping("countbolt");

    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("myjobname", config, builder.createTopology());
  }
}
