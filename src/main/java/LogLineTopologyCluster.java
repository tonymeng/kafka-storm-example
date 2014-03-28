import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import consumer.bolt.FileDumpBolt;
import consumer.bolt.WordCountBolt;
import nl.minvenj.nfi.storm.kafka.KafkaSpout;

/**
 * User: tonymeng
 * Date: 3/27/14
 */
public class LogLineTopologyCluster {

  public static void main(String[]args) throws Exception {
    if (args == null || args.length != 2) {
      throw new IllegalArgumentException("Expected: <zookeeper:port> <topic>");
    }
    String zkConnect = args[0];
    String topic = args[1];

    Config config = new Config();
    config.setNumWorkers(1);

    config.put("kafka.spout.topic", topic);
    config.put("kafka.spout.consumer.group", "test-consumer-group");
    config.put("kafka.zookeeper.connect", zkConnect);
    config.put("kafka.consumer.timeout.ms", 4000);

    KafkaSpout spout = new KafkaSpout();
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("kafkaspout", spout);
    builder.setBolt("countbolt", new WordCountBolt()).shuffleGrouping("kafkaspout");
    builder.setBolt("filebolt", new FileDumpBolt("/tmp/filedump")).shuffleGrouping("kafkaspout");
    builder.setBolt("filecountbolt", new FileDumpBolt("/tmp/filedumpcount")).shuffleGrouping("countbolt");

    StormSubmitter.submitTopology("loglinetopology", config, builder.createTopology());
  }
}
