package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.tuple.Tuple;

/**
 * User: tonymeng
 * Date: 3/31/14
 */
public class WordCountDumpBolt extends FileDumpBolt {

  public WordCountDumpBolt(String path) {
    super(path);
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    try {
      String word = new String((byte[]) tuple.getValueByField("Word"), "UTF-8");
      Integer count = tuple.getIntegerByField("Count");
      getBw().write(count + ":" + word + "\n");
      getBw().flush();
    } catch (Exception e) {
      throw new RuntimeException("Could not write tuple: " + tuple, e);
    }
  }
}
