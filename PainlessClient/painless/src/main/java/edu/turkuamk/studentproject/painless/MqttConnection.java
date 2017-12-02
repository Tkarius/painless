/** 
 * MqttConnection -class is used to handle connecting to MQTT broker
 * and communicating with it. Including both sending and receiving
 * messages.
 *  
 * @author Tommi Tuomola
 * @author Mira Pohjola
 */
package edu.turkuamk.studentproject.painless;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MqttConnection class offers public methods for handling
 * the connection to MQTT broker. The class grants public
 * interface for opening and closing the broker connection
 * as well as methods to subscribe to channels and to publish
 * messages on those channels. We also listen to all the messages
 * sent on the subscribed channels.
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
  private static final List<PainlessChannel> painlessChannelList = new ArrayList<PainlessChannel>();

  public MqttConnection() {
	  // read channellist from text file here and add all the subscribed channels.
	  painlessChannelList.add(new PainlessChannel("testi/t1", "Client"));
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
   * Connects to MQTT-broker with the given user credentials. Uses RSA-keys
   * given in files mqttCaFilePath, mqttClientCrtFilePath and mqttClientKeyFilePath.
   * Subscribes to all channels in the channelList member variable.
   */
  public static void mqttConnectBroker() {
	System.out.println("Initiating MQTT broker connection.");
	for (int i=0; i<5; i++) {
	  try {  
        mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
        mqttClient.setCallback(new PainlessMqttCallback());
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setPassword(Credentials.getPass().toCharArray());
        mqttConnectOptions.setUserName(Credentials.getUser());
        mqttConnectOptions.setSocketFactory(
          SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath, mqttClientKeyFilePath, Credentials.getPass()));
        mqttConnectOptions.setCleanSession(true);
        mqttClient.setTimeToWait(5000);
        if (!mqttClient.isConnected()) {
          mqttClient.connect(mqttConnectOptions);
        }
        System.out.println("Subscribing to channels.");
        for (PainlessChannel channel : painlessChannelList) {
          mqttClient.subscribe(channel.getcName());
        }
      } catch (MqttException exc) {
        System.out.println("Debug: Exception occured while connecting to broker: " + exc);
	  }
	}
  }

  public void mqttAuthorize() {
    try {  
	  mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
	  mqttClient.setCallback(new PainlessMqttAuthCallback());
	  mqttConnectOptions = new MqttConnectOptions();
	  mqttConnectOptions.setPassword("AuthPassu".toCharArray());
	  mqttConnectOptions.setUserName("AuthCheck");
	  mqttConnectOptions.setSocketFactory(
	    SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath, mqttClientKeyFilePath, Credentials.getPass()));
	  mqttConnectOptions.setCleanSession(true);
	  mqttClient.setTimeToWait(5000);
	  if (!mqttClient.isConnected()) {
	    mqttClient.connect(mqttConnectOptions);
	  }
      mqttClient.subscribe("/painless/sys/auth");
      MqttMessage message = new MqttMessage();
      message.setPayload("Auth@testiauth".getBytes());
      message.setQos(0);
      mqttClient.publish("/painless/sys/auth", message);
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
    } finally { //ensures that the client is closed only after disconnecting from broker.
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
  private static void mqttAuthClose() {
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
    } //connectionLost
    
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	} //deliveryComplete

    @Override
    public void messageArrived(String channel, MqttMessage msg) throws Exception {
      if (channel.equals("painless/sys/" + mqttDeviceId)) {
        System.out.println("System message: " + msg.toString());
      } else if (channel.equals("painless/sys/" + mqttDeviceId + "/error")) {
        System.out.println("Received error from broker: " + msg.toString());
      }
      else {  // if the message is not a system message, it's a regular channel message.
        // System.out.println("[" + channel + "] " + msg);
        PainlessMessage incChannelMessage = new PainlessMessage(msg.toString());
        painlessChannelList.get(findChannelIndexByName(channel)).addMsg(incChannelMessage);
      }
    } //messageArrived

    private int findChannelIndexByName(String channelToFind) {
      for (PainlessChannel channel : painlessChannelList) {
        if (channel.getcName().equals(channelToFind)) {
        	return painlessChannelList.indexOf(channel);
        }
      }
      System.out.println("Debug: we should never get here, something is very wrong.");
      return -1;
    }
    
  } //PainlessMqttCallback
  
  private static class PainlessMqttAuthCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
      // connection lost to Auth, sounds pretty bad. we should probably terminate.
	  System.out.println("Disconnected from MQTT broker.");
	  cause.printStackTrace();
	} //connectionLost

    @Override
    public void messageArrived(String channel, MqttMessage msg) throws Exception {
      if (channel.equals("painless/sys/auth/response/" + mqttDeviceId)) {
        if (msg.toString().equals("Success")) {
          // Auth successful, connect to MQTT broker with the given credentials.
          mqttAuthClose();
          mqttConnectBroker();
        }
        else {
          // Auth fails, do something!
        }
      }
    } //messageArrived

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    } //deliveryComplete
  } //PainlessMqttCallback
}
