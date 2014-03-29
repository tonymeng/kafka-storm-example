import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import consumer.bolt.ExceptionBolt;
import consumer.bolt.FileDumpBolt;
import consumer.bolt.MailerBolt;
import consumer.bolt.WordCountBolt;
import nl.minvenj.nfi.storm.kafka.KafkaSpout;

/**
 * User: tonymeng
 * Date: 3/28/14
 */
public class ExceptionTopologyCluster {

  public static void main(String[]args) throws Exception {
    if (args == null || args.length != 5) {
      throw new IllegalArgumentException("Expected: <zookeeper:port> <topic> <gmailuser> <gmailpass> <targetaddress>");
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
    builder.setBolt("exceptionbolt", new ExceptionBolt()).shuffleGrouping("kafkaspout");
    builder.setBolt("exceptionfilebolt", new FileDumpBolt("/tmp/exceptions")).shuffleGrouping("exceptionbolt");
    builder.setBolt("exceptionmailbolt", new MailerBolt(args[2], args[3], args[4], "Exception Thrown")).shuffleGrouping("exceptionbolt");

    StormSubmitter.submitTopology("exceptiontopology", config, builder.createTopology());
  }
}
