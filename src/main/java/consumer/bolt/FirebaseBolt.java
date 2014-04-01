package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

/**
 * Firebase Bolt takes in a tuple of size 2. It expects the first value to be a delimited string
 * which will represent the path to store the JSON at. It expects the second value to be the JSON.
 *
 * User: tonymeng
 * Date: 3/31/14
 */
public class FirebaseBolt extends BaseBasicBolt {

  private final String namespace;
  private final String delimiter;

  public FirebaseBolt(String namespace, String delimiter) {
    this.namespace = namespace;
    this.delimiter = delimiter;
  }

  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    try {
      String delimiterPath = new String((byte[]) input.getValue(0), "UTF-8");
      String json = new String((byte[]) input.getValue(1), "UTF-8");
      StringBuffer path = new StringBuffer("https://" + this.namespace + ".firebaseio.com");
      for (String child : delimiterPath.split(delimiter)) {
        path.append("/").append(child);
      }
      URI uri = new URI(path.toString() + ".json");
      HttpPut put = new HttpPut(uri);
      put.setEntity(new StringEntity(json));
      HttpClient client = new DefaultHttpClient();
      HttpResponse r = client.execute(put);
      collector.emit(new Values(r));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("response"));
  }
}
