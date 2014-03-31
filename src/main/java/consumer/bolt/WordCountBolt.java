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
 * Basic word count, given a tuple, it will keep a count in memory of how many of that tuple it has seen.
 * It will emit a value of <word, count>.
 *
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
      counts.put(word, ++count);
      basicOutputCollector.emit(new Values(tuple.getValue(0), count));
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("Word", "Count"));
  }
}
