package edu.turkuamk.studentproject.painless;

import java.io.IOException;
import java.util.logging.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;

/**
 * WindowManager class is a higher level controller which controls switching user view between windows
 * as well as handles passing needed information between lower-level controllers.
 * 
 * @author Mira Pohjola
 *
 */
public class WindowManager {
  private Scene scene;

  public WindowManager(Scene scene) {
    this.scene = scene;
  }

  public void authenticated(Boolean auth) {
    if (auth) {
      showMainWindow();
    }
    else {
      showRegisterWindow();
    }
  }

  //Handles showing login screen
  public void showLoginScreen() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
      scene.setRoot((Parent) loader.load());
      LoginController controller = loader.<LoginController>getController();
      controller.initManager(this);
    } catch (IOException ex) {
      //
    }
  }

  //Handles showing main screen and moves control there after succesfull login
  private void showMainWindow() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
      scene.setRoot((Parent) loader.load());
      MainWindowController controller = loader.<MainWindowController>getController();
      controller.initDashboard(this);
    } catch (IOException ex) {
      System.out.println("No main window for you. :(" + ex);
    }
  }
	
  //Handles showing register screen
  private void showRegisterWindow() {
    //TODO: Add FXML for register window and add needed logic here.
  }
}
