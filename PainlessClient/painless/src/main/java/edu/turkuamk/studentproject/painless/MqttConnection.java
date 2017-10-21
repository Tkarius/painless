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
  public void MqttConnection() {
	  mqttOpen();
  }
  
  public void sendMessage(String msgToSend) {
    MqttMessage message = new MqttMessage();
    message.setPayload(msgToSend.getBytes());
    message.setQos(2); // SIIRRÄ CONFFIIN
    System.out.println("Sending: " + message.toString());
    try {
      mqttClient.publish(("tokenrequest/" + mqttDeviceId), message);
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
    if (!mqttClient.isConnected()) {
        try {
          mqttClient.disconnect();
          // mqttClient.setCallback(null);
        } catch (MqttException ex) {
          System.out.println("Debug: Disconnecting MQTT broker connection failed: " + ex);
        } finally {
          mqttClient.close();
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
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
      if (topic.equals("system/" + mqttDeviceId)) {
        System.out.println("Debug: System message: " + msg.toString());
      } else if (topic.equals("system/" + mqttDeviceId + "/error")) {
        System.out.println("Received error from broker: " + msg.toString());
      }
    } //messageArrived

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	} //deliveryComplete
  } //PainlessMqttCallback
}
