package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;

/**
 * This bolt will filter all tuples for exceptions. It will the emit exceptions.
 *
 * User: tonymeng
 * Date: 3/28/14
 */
public class ExceptionBolt extends BaseBasicBolt {
  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    try {
      String text = new String((byte[]) input.getValue(0), "UTF-8");
      if (text.startsWith("Exception in thread")) {
        collector.emit(Lists.<Object>newArrayList(input.getValue(0)));
      }
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("exception"));
  }
}
