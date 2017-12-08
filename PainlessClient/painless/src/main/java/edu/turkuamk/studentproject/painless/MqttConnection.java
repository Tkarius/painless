/** 
 * MqttConnection -class is used to handle connecting to MQTT broker
 * and communicating with it. Including both sending and receiving
 * messages.
 *  
 * @author Tommi Tuomola
 * @author Mira Pohjola
 */
package edu.turkuamk.studentproject.painless;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javafx.application.Platform;

/**
 * MqttConnection class offers public methods for handling the connection to
 * MQTT broker. The class grants public interface for opening and closing the
 * broker connection as well as methods to subscribe to channels and to publish
 * messages on those channels. We also listen to all the messages sent on the
 * subscribed channels.
 * 
 * @author Tommi Tuomola
 * @author Mira Pohjola
 */
public class MqttConnection {
  private static MqttClient mqttClient;
  private static final String mqttDeviceId = "ClientDevice_1";
  private static final String mqttBrokerAddr = "ssl://127.0.0.1";
  private static MqttConnectOptions mqttConnectOptions;
  private static final String mqttCaFilePath = "/home/painless/Project/PainlessClient/painless/tls/ca.crt";
  private static final String mqttClientCrtFilePath = "/home/painless/Project/PainlessClient/painless/tls/painlessTestClient.client.crt";
  private static final String mqttClientKeyFilePath = "/home/painless/Project/PainlessClient/painless/tls/cert.key";
  private static final String clientChannelListFile = "/home/painless/Project/PainlessClient/painless/channels.txt";
  
  private static WindowManager windowManager;
  
  private static final String AuthRequestCH = "painless/sys/auth/request/" + mqttDeviceId;
  private static final String AuthResponseCH = "painless/sys/auth/response/" + mqttDeviceId;
  private static String SubRequestCH = "painless/sys/subscribe/request/";
  private static String SubResponseCH = "painless/sys/subscribe/response/";

  public MqttConnection() {
    // read channellist from text file here and add all the subscribed channels.
    // Credentials.getChannelList().add(new PainlessChannel("testi/t1", "Client"));
    BufferedReader channelReader = null;
    try {
      channelReader = new BufferedReader(new FileReader(clientChannelListFile));
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    String channel;
    ArrayList<PainlessChannel> painlessChannelList = new ArrayList<PainlessChannel>();
    try {
      while ((channel = channelReader.readLine()) != null) {
        PainlessChannel newChannel = new PainlessChannel(channel, "Client");
        painlessChannelList.add(newChannel);
        }
      channelReader.close();
      Credentials.setChannelList(painlessChannelList);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
  }

  /**
   * Sends a message to a given channel on MQTT-broker at QoS 2.
   * 
   * @param channel
   * @param msgToSend
   */
  public void sendMessage(String channel, String msgToSend) {
    msgToSend = Credentials.getUser() + " " + msgToSend;
    MqttMessage message = new MqttMessage();
    message.setPayload(msgToSend.getBytes());
    message.setQos(2);
    System.out.println("Sending: Channel: " + channel + " Msg: " + message.toString());
    try {
      mqttClient.publish(channel, message);
    } catch (MqttException | NullPointerException exc) {
      if (exc.toString().contains("java.io.FileNotFoundException")) {
        System.out.println("Debug: MQTT persistence exception: " + exc);
      } else {
        System.out.println("Debug: MQTT exception while sending msg: " + exc);
        exc.printStackTrace();
      }
    }
  }// sendMessage

  /**
   * Connects to MQTT-broker with the given user credentials. Uses RSA-keys given
   * in files mqttCaFilePath, mqttClientCrtFilePath and mqttClientKeyFilePath.
   * Subscribes to all channels in the channelList member variable.
   */
  public static void mqttConnectBroker() {
    System.out.println("Initiating MQTT broker connection.");
    for (int i = 0; i < 5; i++) {
      try {
        mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
        mqttClient.setCallback(new PainlessMqttCallback());
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setPassword(Credentials.getPass().toCharArray());
        mqttConnectOptions.setUserName(Credentials.getUser());
        mqttConnectOptions.setSocketFactory(SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath,
            mqttClientKeyFilePath, Credentials.getPass()));
        mqttConnectOptions.setCleanSession(true);
        mqttClient.setTimeToWait(5000);
        if (!mqttClient.isConnected()) {
          mqttClient.connect(mqttConnectOptions);
        }
        System.out.println("Subscribing to channels.");
        for (PainlessChannel channel : Credentials.getChannelList()) {
          mqttClient.subscribe(channel.getcName());
        }
        //Subscribing to needed system channels as well
        mqttClient.subscribe(SubResponseCH + Credentials.getUser());
        return;
      } catch (MqttException exc) {
        System.out.println("Debug: Exception occured while connecting to broker: " + exc);
      }
    }
  }

  public void mqttAuthorize(WindowManager _windowManager) {
    windowManager = _windowManager;
    try {
      mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
      mqttClient.setCallback(new PainlessMqttAuthCallback());
      mqttConnectOptions = new MqttConnectOptions();
      mqttConnectOptions.setPassword("AuthPass".toCharArray());
      mqttConnectOptions.setUserName("AuthCheck");
      mqttConnectOptions.setSocketFactory(SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath,
          mqttClientKeyFilePath,"AuthPass"));
      mqttConnectOptions.setCleanSession(true);
      mqttClient.setTimeToWait(5000);
      if (!mqttClient.isConnected()) {
    	System.out.println("connecting to auth server");
        mqttClient.connect(mqttConnectOptions);
      }
      mqttClient.subscribe("painless/sys/auth/response/" + mqttDeviceId);
      MqttMessage message = new MqttMessage();
      message.setPayload((Credentials.getUser() + "@" + Credentials.getPass()).getBytes());
      message.setQos(0);
      System.out.println("Sending Auth...");
      mqttClient.publish("painless/sys/auth/request/" + mqttDeviceId, message);
    } catch (MqttException exc) {
      System.out.println("Debug: Exception occured while connecting to broker: " + exc);
    }
  }

  /**
   * Disconnects the MQTT broker connection and closes the client.
   */
  public void mqttClose() {
    System.out.println("Shutting down MQTT broker connection.");
    try {
      mqttClient.disconnect();
    } catch (MqttException exc) {
      System.out.println("Debug: Disconnecting MQTT broker connection failed: " + exc);
    } finally { // ensures that the client is closed only after disconnecting from broker.
      try {
        mqttClient.close();
      } catch (MqttException exc) {
        System.out.println("Debug: Closing MQTT client failed: " + exc);
      }
    }
  }// mqttClose()

  /**
   * Disconnect silently after Auth has been resolved.
   */
  public static void mqttAuthClose() {
    try {
      mqttClient.disconnect();
    } catch (MqttException exc) {
      System.out.println("AUTH: Disconnecting MQTT client failed: " + exc);
    } finally {
      try {
        mqttClient.close();
      } catch (MqttException exc) {
        System.out.println("AUTH: Closing MQTT client failed: " + exc);
      }
    }
  }

  /**
   * This private class handles the received messages from MQTT-broker.
   * 
   * @author Tommi Tuomola
   * @author Mira Pohjola
   */
  private static class PainlessMqttCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
      System.out.println("Disconnected from MQTT broker.");
      cause.printStackTrace();
    } // connectionLost

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    } // deliveryComplete

    @Override
    public void messageArrived(String channel, MqttMessage msg) throws Exception {
      System.out.println("Kato, saatii viesti kanavalle: " + channel);
      if (channel.equals("painless/sys/" + mqttDeviceId)) {
        System.out.println("System message: " + msg.toString());
      } else if (channel.equals("painless/sys/" + mqttDeviceId + "/error")) {
        System.out.println("Received error from broker: " + msg.toString());
      } else if (channel.equals(SubResponseCH + Credentials.getUser())) {
        System.out.println("Got subscribe callback message from server!");
        String[] msgArray = msg.toString().split("@");
        if (msgArray[0].equals("Success")) {
          BufferedWriter output = new BufferedWriter(new FileWriter(clientChannelListFile, true));
          output.write(msgArray[1]);
          output.newLine();
          output.close();
          //ADD TO SUBSCRIBED CHANNELS FILE
          //Since we don't really have functionality for channel owners yet, let's just make everyone a client
          PainlessChannel newChannel = new PainlessChannel(msgArray[1], "Client");
          Credentials.getChannelList().add(newChannel);
          mqttClient.subscribe(msgArray[1]);
          
        }
        
      } else { // if the message is not a system message, it's a regular channel message.
        PainlessMessage incChannelMessage = new PainlessMessage(msg.toString());
        Credentials.getChannelList().get(findChannelIndexByName(channel)).addMsg(incChannelMessage);
      }
    } // messageArrived

    private int findChannelIndexByName(String channelToFind) {
      for (PainlessChannel channel : Credentials.getChannelList()) {
        if (channel.getcName().equals(channelToFind)) {
          return Credentials.getChannelList().indexOf(channel);
        }
      }
      System.out.println("Debug: we should never get here, something is very wrong.");
      return -1;
    }

  } // PainlessMqttCallback

  private static class PainlessMqttAuthCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
      // connection lost to Auth, sounds pretty bad. we should probably terminate.
      System.out.println("Disconnected from MQTT broker.");
      cause.printStackTrace();
    } // connectionLost

    @Override
    public void messageArrived(String channel, MqttMessage msg) throws Exception {
      System.out.println("Saatii viesti: " + msg.toString());
      if (channel.equals("painless/sys/auth/response/" + mqttDeviceId)) {
        if (msg.toString().equals("Success")) {
          // Auth successful, connect to MQTT broker with the given credentials.
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              windowManager.authenticated(true);
            }
          });
        }
      } else {
        // Auth fails, do something!
      }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
      // TODO Auto-generated method stub

    }
  } // messageArrived
  
  public void authSubscribe(String channelName) {
    String subChannel = SubRequestCH + Credentials.getUser();
    String subMessage = channelName;
    System.out.println(subMessage);
    sendMessage(subChannel, subMessage);
  }
}
