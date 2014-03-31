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
import java.io.IOException;
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

  protected BufferedWriter getBw() {
    return this.bw;
  }

  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    try {
      File dumpFile = new File(path);
      if (!dumpFile.exists()) {
        try {
          dumpFile.createNewFile();
        } catch (IOException ioe) {
          throw new RuntimeException("Could not create file at path: " + path, ioe);
        }
      }
      bw = new BufferedWriter(new FileWriter(dumpFile));
    } catch (IOException ioe) {
      throw new RuntimeException("Could not establish a buffered writer at path: " + path, ioe);
    }
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    try {
      String word = new String((byte[]) tuple.getValue(0), "UTF-8");
      getBw().write(word + "\n");
      getBw().flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

  }
}
