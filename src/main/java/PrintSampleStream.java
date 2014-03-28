import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import consumer.bolt.PrinterBolt;
import consumer.spout.RandomSentenceSpout;

public class PrintSampleStream {
  public static void main(String[] args) {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("spout", new RandomSentenceSpout());
    builder.setBolt("print", new PrinterBolt())
        .shuffleGrouping("spout");

    Config conf = new Config();
    LocalCluster cluster = new LocalCluster();

    cluster.submitTopology("test", conf, builder.createTopology());

    Utils.sleep(10000);
    cluster.shutdown();
  }
}