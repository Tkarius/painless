package edu.turkuamk.studentproject.painless;


import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
  @FXML private TextField subChannelNameField;
  @FXML private TextField pubMessageField;
  @FXML private Button subButton;
  @FXML private Button pubButton;
  private PainlessChannel activeChannel;

	/**
	 * Initializes the user dashboard with user related information, such as username and
	 * the list of channels user has subscribed to.
	 * 
	 * @author Mira Pohjola
	 */
  public void initDashboard(WindowManager windowManager, MqttConnection mqtt) {
    logoutButton.setOnAction(new EventHandler<ActionEvent>() {
	  @Override public void handle(ActionEvent event) {
        System.out.println("Log out process initiated. Closing all connections.");
		mqtt.mqttClose();
		// save data structures to filesystem?
		
	  }
	});
    
    ArrayList<PainlessChannel> testChannelList = new ArrayList<PainlessChannel>();
    testChannelList.add(new PainlessChannel("painless/parsat/on/parhaita", "Owner"));
    testChannelList.add(new PainlessChannel("testi/t1", "Owner"));
    populateChannelList(Credentials.getChannelList());
    testChannelList.get(0).addMsg(new PainlessMessage("Esteri_1 Olen Viesti"));
    testChannelList.get(0).addMsg(new PainlessMessage("Esteri_2 Olen myös viesti"));
    showChat(testChannelList.get(0));
		
    loggedInAsText.setText("Signed in as: " + Credentials.getUser());
    mqtt.sendMessage("testi/t1", "Hello wurld!");
    //mqtt.mqttClose();
    //mqtt.sendMessage("testi/t1", "Hello wurld!");
    //mqtt.mqttClose();
    
    //Event handler for subsribe button. Takes the text in subscribe text field and gives it as a parameter
    // when calling a subscribe auth from MqttConnection class. That method is not yet implemented.
    subButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String channelName = subChannelNameField.getText();
        mqtt.authSubscribe(channelName);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        populateChannelList(Credentials.getChannelList());
        
      }
      
    });
    //Event handler for publish button. Takes the text in publish text field and the currently active channel and
    // calls mqtt.sendMessage with those as variables.
    pubButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        String messageToSend = pubMessageField.getText();
        String channelToSend = activeChannel.getcName();
        mqtt.sendMessage(channelToSend, messageToSend);
        
      }
      
    });
  } //initDashboard()

  //Current implementation here wants a ArrayList full of channels, but if we decide to handle channels some different way
  //let's change this!
  private void populateChannelList(ArrayList<PainlessChannel> channels) {		
    for (int i = 0; i < channels.size(); i++) {
      final int loopIndex = i;
      Label newChannel = new Label(channels.get(loopIndex).getcName());
      newChannel.setOnMouseClicked(new EventHandler<MouseEvent>() {
      //This event will allow us to change the active chat in chat screen!
      //When we'll have the list of available chats available, we can call the correct message list
      //by the channel.
        @Override
        public void handle(MouseEvent arg0) {
          System.out.println("Changing chats, yay!");
          showChat(channels.get(loopIndex));
          activeChannel = channels.get(loopIndex);
        }				
      });
      channelList.add(newChannel, 0, loopIndex);
      channelList.add(new Label(channels.get(loopIndex).getRole()), 1, loopIndex);
      //TODO: Add event listener etc. to button
      Button manageButton = new Button("Manage");
      channelList.add(manageButton, 2, loopIndex);
    }		
  }//populateChannelList()

  private void showChat(PainlessChannel channel) {
    activeChannel = channel;
    chatBox.getChildren().clear();
    ArrayList<PainlessMessage> messages = channel.showMsgs();
    for (int i = 0; i < messages.size(); i++) {
      chatBox.add(new Label(messages.get(i).toString()), 0, i);
			//chatBox.add(new Label("username"), 0, i); //Username of the sender goes here
			//chatBox.add(new Label("Timestamp"), 1, i); //Timestamp goes here
			//chatBox.add(new Label(messages.get(i)), 2, i); // Message goes here. How do we handle multiline/long messages?
    }
  }; //showchat()
	
}
