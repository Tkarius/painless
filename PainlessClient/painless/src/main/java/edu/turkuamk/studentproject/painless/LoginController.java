package edu.turkuamk.studentproject.painless;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

/**
 * Controller for login.fxml. Captures login information, asks for auth from server.
 * Passes the auth information to WindowManager.
 * 
 * @author Mira Pohjola
 * 
 */
public class LoginController {
  @FXML private TextField user;
  @FXML private TextField password;
  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private Text testtarget;

  
  public void initManager(final WindowManager windowManager) {
    loginButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent event) {
      	System.out.println("Login Button pressed.");
      	Credentials.setPass(password.getText());
      	Credentials.setUser(user.getText());
      	System.out.println(Credentials.getUser() + Credentials.getPass());
      	Boolean auth = authorized();
        windowManager.authenticated(auth);
      }

	  private boolean authorized() {
		//Send user credentials over MQTT for verification. Return true if user with that password/username exists
		//And false if doesn't
	    return true;
	  }
    });
  }

  

  
}
