package consumer.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;



/**
 * This bolt will take each tuple it receives and will mail it (via GMail) to an intended receiver.
 *
 * User: tonymeng
 * Date: 3/28/14
 */
public class MailerBolt extends BaseBasicBolt {
  private final String username;
  private final String password;
  private final String toAddress;
  private final String subject;

  public MailerBolt(String username, String password, String toAddress, String subject) {
    this.username = username;
    this.password = password;
    this.toAddress = toAddress;
    this.subject = subject;
  }

  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    try {
      String text = new String((byte[]) input.getValue(0), "UTF-8");

      Send(username, password, toAddress, null, subject, text);
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    // Nothing to declare, this will simply mail a notification
  }

  public static void Send(
      final String username, final String password,
      String recipientEmail, String ccEmail,
      String title, String message) throws MessagingException {
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

    // Get a Properties object
    Properties props = System.getProperties();
    props.setProperty("mail.smtps.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "465");
    props.setProperty("mail.smtp.socketFactory.port", "465");
    props.setProperty("mail.smtps.auth", "true");

    /*
    If set to false, the QUIT command is sent and the connection is immediately closed. If set
    to true (the default), causes the transport to wait for the response to the QUIT command.

    ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
            http://forum.java.sun.com/thread.jspa?threadID=5205249
            smtpsend.java - demo program from javamail
    */
    props.put("mail.smtps.quitwait", "false");

    Session session = Session.getInstance(props, null);

    // -- Create a new message --
    final MimeMessage msg = new MimeMessage(session);

    // -- Set the FROM and TO fields --
    msg.setFrom(new InternetAddress(username + "@gmail.com"));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

    if (ccEmail != null && ccEmail.length() > 0) {
      msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
    }

    msg.setSubject(title);
    msg.setText(message, "utf-8");
    msg.setSentDate(new Date());

    SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

    t.connect("smtp.gmail.com", username, password);
    t.sendMessage(msg, msg.getAllRecipients());
    t.close();
  }
}
