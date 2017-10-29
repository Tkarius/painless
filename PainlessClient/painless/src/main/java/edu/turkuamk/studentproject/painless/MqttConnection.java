/** MqttConnection -class is used to handle connecting to MQTT broker
 *  and communicating with it. Including both sending and receiving
 *  messages.
 */
package edu.turkuamk.studentproject.painless;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

//import com.google.gson.JsonObject;

public class MqttConnection {
  private static MqttClient mqttClient;
  private static String mqttDeviceId = "";
  private static String mqttBrokerAddr = "";
  private static MqttConnectOptions mqttConnectOptions;
  private static String mqttCaFilePath = "";
  private static String mqttClientCrtFilePath = "";
  private static String mqttClientKeyFilePath = "";
  private static String mqttPW = "";
  private static String mqttUserID = "";

  // do we really want to connect in constructor? guess it's ok for testing at least.
  // we'll probably want to do this after login when we actually have the credentials
  // and can form the connect options.
  public void MqttConnection() {
	  mqttOpen();
  }
  
  public void sendMessage(String channel, String msgToSend) {
    MqttMessage message = new MqttMessage();
    message.setPayload(msgToSend.getBytes());
    message.setQos(2);
    System.out.println("Sending: Channel: " + channel + " Msg: " + message.toString());
    try {
      mqttClient.publish(channel, message);
    } catch (MqttException | NullPointerException me) {
      if (me.toString().contains("java.io.FileNotFoundException")) {
        System.out.println("Debug: MQTT persistence exception: " + me);
        // me.printStackTrace();
      } else {
        System.out.println("Debug: Mqtt exception with AWS: " + me);
        me.printStackTrace();
      }
    }
  }// sendMessage

  private void mqttOpen() {
    try {
      System.out.println("Initiating mqtt broker connection.");
      mqttClient = new MqttClient(mqttBrokerAddr, mqttDeviceId);
      mqttClient.setCallback(new PainlessMqttCallback());
      mqttConnectOptions = new MqttConnectOptions();
      mqttConnectOptions.setPassword(mqttPW.toCharArray());
      mqttConnectOptions.setUserName(mqttUserID);
      mqttConnectOptions.setSocketFactory(
      SslUtil.getSocketFactory(mqttCaFilePath, mqttClientCrtFilePath, mqttClientKeyFilePath, mqttPW));
      mqttConnectOptions.setCleanSession(false);
      mqttClient.setTimeToWait(5000);
      if (!mqttClient.isConnected()) {
        mqttClient.connect(mqttConnectOptions);
      }
    } catch (MqttException exc) {

	}
  }

  public void mqttClose() throws MqttException {
    System.out.println("Shutting down MQTT broker connection.");
    try {
      mqttClient.disconnect();
    } catch (MqttException ex) {
      System.out.println("Debug: Disconnecting MQTT broker connection failed: " + ex);
    } finally { //ensures that the client is closed only after disconnection.
      mqttClient.close();
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
