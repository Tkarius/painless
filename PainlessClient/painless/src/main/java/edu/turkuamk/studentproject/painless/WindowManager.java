package edu.turkuamk.studentproject.painless;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

/**
 * WindowManager class is a higher level controller which controls switching user view between windows
 * as well as handles passing needed information between lower-level controllers.
 * 
 * @author Mira Pohjola
 *
 */
public class WindowManager {
  private static Scene scene;
  private static Stage stage;
  private static final MqttConnection mqtt = new MqttConnection();

  public WindowManager(Scene _scene) {
    scene = _scene;
  }

  public WindowManager() {
    // TODO Auto-generated constructor stub
  }

  public void authenticated(Boolean auth) {
    if (auth) {
      showMainWindow();
      MqttConnection.mqttAuthClose();
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      MqttConnection.mqttConnectBroker();
    }
    else {
      showRegisterWindow();
    }
  }
  
  /**
   * Sets the stage parameter. This method is called from main program to allow WindowManager to have
   * access to main stage in order to resize the stage when changing the scene.
   * @param _stage
   */
  public void setStage(Stage _stage) {
    stage = _stage;
  }
	
  //Handles showing login screen
  public void showLoginScreen() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
      scene.setRoot((Parent) loader.load());
      LoginController controller = loader.<LoginController>getController();
      controller.initManager(this, mqtt);
    } catch (IOException ex) {
      //
    }
  }

  //Handles showing main screen and moves control there after successful login
  private void showMainWindow() {
    try {   	
      FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
      scene.setRoot((Parent) loader.load());
      MainWindowController controller = loader.<MainWindowController>getController();
      stage.sizeToScene();
      controller.initDashboard(this, mqtt);
    } catch (IOException ex) {
      System.out.println("No main window for you. :(" + ex);
    }
  }
	
  //Handles showing register screen
  private void showRegisterWindow() {
    //TODO: Add FXML for register window and add needed logic here.
  }
}
