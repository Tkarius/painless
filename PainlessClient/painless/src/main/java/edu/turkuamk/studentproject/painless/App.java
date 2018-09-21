/** 
 * App.java is the main driver for Painless -client.
 * The class controls the program flow and works
 * as entry point for the service.
 * 
 * @author Tommi Tuomola
 * @author Mira Pohjola 
 */
package edu.turkuamk.studentproject.painless;

//import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The class works as driver for Painless client.
 * The private methods login() and menu() control
 * the program flow and request the user credentials.
 * 
 * @author Tommi Tuomola
 */
public class App extends Application {
  public static Credentials Credentials;
	
  public static void main(String[] args) {
    System.out.println("Painless.");
    Application.launch(args);
    System.out.println("Some pain will last.");
  }
  
  @Override
  public void start(Stage stage) {
    Scene scene = new Scene(new StackPane());
    WindowManager windowManager = new WindowManager(scene);
    windowManager.setStage(stage);
    windowManager.showLoginScreen();
    stage.setTitle("Painless");
    stage.setScene(scene);
    stage.show();
  }
}
