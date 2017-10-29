/** MqttConnection -class is used to handle connecting to MQTT broker
 *  and communicating with it. Including both sending and receiving
 *  messages.
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

//import com.google.gson.JsonObject;

public class MqttConnection {
  private static MqttClient mqttClient;
  private static String mqttDeviceId = "ClientDevice_1";
  private static String mqttBrokerAddr = "localhost";
  private static MqttConnectOptions mqttConnectOptions;
  private static String mqttCaFilePath = "";
  private static String mqttClientCrtFilePath = "";
  private static String mqttClientKeyFilePath = "";
  private static List<String> channelList = new ArrayList<String>();

  public MqttConnection() {
	  // modify to read from file
	  channelList.add("testi/t1");
  }
  
  public void sendMessage(String channel, String msgToSend) {
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
        System.out.println("Debug: Mqtt exception with AWS: " + exc);
        exc.printStackTrace();
      }
    }
  }// sendMessage

  public void mqttOpen() {
    try {
      System.out.println("Initiating mqtt broker connection.");
      mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
      mqttClient.setCallback(new PainlessMqttCallback());
      mqttConnectOptions = new MqttConnectOptions();
      mqttConnectOptions.setPassword(App.Credentials.getPass().toCharArray());
      mqttConnectOptions.setUserName(App.Credentials.getUser());
      mqttConnectOptions.setSocketFactory(
        SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath, mqttClientKeyFilePath, App.Credentials.getPass()));
      mqttConnectOptions.setCleanSession(false);
      mqttClient.setTimeToWait(5000);
      if (!mqttClient.isConnected()) {
        mqttClient.connect(mqttConnectOptions);
      }
      for (String channel : channelList) {
        mqttClient.subscribe(channel);
      }
    } catch (MqttException exc) {
      System.out.println("Debug: Exception occured while connecting to broker: " + exc);
	}
  }

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

  private static class PainlessMqttCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
      System.out.println("Disconnected from MQTT broker.");
      cause.printStackTrace();
    } //connectionLost

    @Override
    public void messageArrived(String channel, MqttMessage msg) throws Exception {
      if (channel.equals("system/" + mqttDeviceId)) {
        System.out.println("Debug: System message: " + msg.toString());
      } else if (channel.equals("system/" + mqttDeviceId + "/error")) {
        System.out.println("Received error from broker: " + msg.toString());
      }
      else {
    	  // handle regular messages here!
    	  System.out.println("[" + channel + "] " + msg);
      }
    } //messageArrived

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	} //deliveryComplete
  } //PainlessMqttCallback
}
