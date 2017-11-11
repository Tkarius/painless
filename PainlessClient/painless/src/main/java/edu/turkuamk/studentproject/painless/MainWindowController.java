package edu.turkuamk.studentproject.painless;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * MainWindowController class is a controller for mainWindow.fxml. Handles MQTT connectivity and main window
 * functionality.
 * 
 * @author Mira Pohjola
 * 
 */

public class MainWindowController {
	@FXML private Text user;
	private static final MqttConnection mqtt = new MqttConnection();

	/**
	 * Initializes the user dashboard with user related information, such as username and
	 * the list of channels user has subscribed to.
	 * 
	 * @author Mira Pohjola
	 */
	public void initDashboard(WindowManager windowManager) {
	  //TODO: Basically almost everything. 
	  //Right now we show username, open MQTT connection, send one message and shut the connection. Wohoo!
	  user.setText(Credentials.getUser());
	  mqtt.mqttOpen();
      mqtt.sendMessage("testi/t1", "Hello wurld!");
      //menu();
      mqtt.mqttClose();
	}

}
