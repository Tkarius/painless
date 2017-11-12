package edu.turkuamk.studentproject.painless;


import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
	@FXML private Text loggedInAsText;
	@FXML private Button logoutButton;
	@FXML private Button settingsButton;
	@FXML private GridPane channelList;
	@FXML private ScrollPane chatScreen;
	@FXML private GridPane chatBox;
	private Channel activeChannel;
	
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
		
		//Following things mostly exist for testing the layout.
		ArrayList<Channel> testChannelList = new ArrayList<Channel>();
		testChannelList.add(new Channel("painless/parsat/on/parhaita", "Owner"));
		populateChannelList(testChannelList);	
		ArrayList<String> testMessageList = new ArrayList<String>();
		testMessageList.add("Olen Viesti");
		testMessageList.add("Olen myös viesti");
		showChat(testMessageList);
		
		loggedInAsText.setText("Signed in as: " + Credentials.getUser());
		mqtt.mqttOpen();
    mqtt.sendMessage("testi/t1", "Hello wurld!");
    //menu();
    mqtt.mqttClose();		
	}
	
	//Current implementation here wants a ArrayList full of channels, but if we decide to handle channels some different way
	//let's change this!
	private void populateChannelList(ArrayList<Channel> channels) {		
		for (int i = 0; i < channels.size(); i++) {
			channelList.add(new Label(channels.get(i).getcName()), 0, i);
			channelList.add(new Label(channels.get(i).getRole()), 1, i);
			//TODO: Add event listener etc. to button
			Button manageButton = new Button("Manage");
			channelList.add(manageButton, 2, i);
		}		
	}
	
	//When we will have a class for messages, I presume we will want this method to take an ArrayList of those instead.
	//This is purely for layout testing.
	private void showChat(ArrayList<String> messages) {
		for (int i = 0; i < messages.size(); i++) {
			chatBox.add(new Label("username"), 0, i);
			chatBox.add(new Label(messages.get(i)), 1, i);
		}

	}

}
