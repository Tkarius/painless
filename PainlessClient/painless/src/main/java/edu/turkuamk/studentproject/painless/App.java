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
  //private static final MqttConnection mqtt = new MqttConnection();
  //private static final Scanner reader = new Scanner(System.in);
  public static Credentials Credentials;
	
  public static void main(String[] args) {
    System.out.println("Painless.");
    Application.launch(args);
    //login();
    
    //mqtt.mqttOpen();
    //mqtt.sendMessage("testi/t1", "Hello wurld!");
    //menu();
    //mqtt.mqttClose();
    //reader.close();
    System.out.println("Some pain will last.");
  }
  
  @Override
  public void start(Stage stage) {
    Scene scene = new Scene(new StackPane());
    WindowManager windowManager = new WindowManager(scene);
    windowManager.showLoginScreen();
    stage.setTitle("Painless");
    stage.setScene(scene);
    stage.show();
  }

  /*
  private static void login() {
    System.out.println("Username: ");
    Credentials.setUser(reader.nextLine());
    System.out.println("Password: ");
    Credentials.setPass(reader.nextLine());
  }
  // convenience method for testing without GUI
  private static void menu() {
    System.out.println("1) Send message to current channel");
    System.out.println("2) Quit");
    System.out.println("Select: ");
    String menuChoice = reader.nextLine();
    if (menuChoice.equals("1")) {
      System.out.println("Message to send: ");
      String msgToSend = reader.nextLine();
      mqtt.sendMessage("testi/t1", msgToSend);
    }
    else if (menuChoice.equals("2")) {
      return;  
    }
	menu();
  }*/
}
