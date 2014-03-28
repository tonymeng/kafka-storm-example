package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tonymeng
 * Date: 3/26/14
 */
public class WordCountBolt extends BaseBasicBolt {
  Map<String, Integer> counts = new HashMap<String, Integer>();

  @Override
  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    try {
      String word = new String((byte[]) tuple.getValue(0), "UTF-8");
      Integer count = counts.get(word);
      if (count == null)
        count = 0;
      count++;
      counts.put(word, count);
      System.out.println(word + ":" + count);
      basicOutputCollector.emit(new Values((word + ":" + count).getBytes(), count));
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("Could not translate tuple: " + tuple.toString(), uee);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("word", "count"));
  }
}
