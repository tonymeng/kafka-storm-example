package consumer.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;

/**
 * This bolt will filter all tuples for exceptions. It will the emit exceptions.
 *
 * User: tonymeng
 * Date: 3/28/14
 */
public class ExceptionBolt extends RegexBolt {

  /*
   * IllegalStateException, FileNotFoundException, etc
   *
   * eg:
   * java.lang.IllegalStateException: Test
   *  at com.....(Test.java:38)
   */
  public static final String EXCEPTION_REGEX = ".*Exception:(.|\\n)*";

  public ExceptionBolt() {
    super(Lists.newArrayList(EXCEPTION_REGEX));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("exception"));
  }
}
