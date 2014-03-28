package consumer.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * User: tonymeng
 * Date: 3/26/14
 */
public class FileDumpBolt extends BaseBasicBolt {
  private final String path;
  private BufferedWriter bw = null;

  public FileDumpBolt(String path) {
    this.path = path;
  }

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    try {
      File dumpFile = new File(path);
      bw = new BufferedWriter(new FileWriter(dumpFile));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    try {
      String word = new String((byte[]) tuple.getValue(0), "UTF-8");
      bw.write(word + "\n");
      bw.flush();
    } catch (Exception e) {
      throw new RuntimeException("Could not process tuple: " + tuple.toString(), e);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    outputFieldsDeclarer.declare(new Fields("word", "count"));
  }
}
