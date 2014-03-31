package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: tonymeng
 * Date: 3/31/14
 */
public class RegexBolt extends BaseBasicBolt {

  private final List<Pattern> patterns;

  public RegexBolt(List<String> regexes) {
    List<Pattern> patterns = Lists.newArrayListWithExpectedSize(regexes.size());
    for (String regex : regexes) {
      patterns.add(Pattern.compile(regex));
    }
    this.patterns = Collections.unmodifiableList(patterns);
  }

  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    try {
      String text = new String((byte[]) input.getValue(0), "UTF-8");
      for (Pattern p : patterns) {
        if (p.matcher(text).matches()) {
          collector.emit(Lists.<Object>newArrayList(input.getValue(0)));
          return; // done after 1st match
        }
      }
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

  }
}
