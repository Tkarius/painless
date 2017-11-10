/** 
 * App.java is the main driver for Painless -client.
 * The class controls the program flow and works
 * as entry point for the service.
 * 
 * @author Tommi Tuomola
 * @author Mira Pohjola 
 */
package edu.turkuamk.studentproject.painless;

import java.util.Scanner;

/**
 * The class works as driver for Painless client.
 * The private methods login() and menu() control
 * the program flow and request the user credentials.
 * 
 * @author Tommi Tuomola
 */
public class App {
  private static final MqttConnection mqtt = new MqttConnection();
  private static final Scanner reader = new Scanner(System.in);
	
  public static void main(String[] args) {
    System.out.println("Painless.");
    login();
    
    mqtt.mqttOpen();
    mqtt.sendMessage("testi/t1", "Hello wurld!");
    menu();
    mqtt.mqttClose();
    reader.close();
    System.out.println("Some pain will last.");
  }

  /**
   * Credentials class is a simple data structure for storing, setting
   * and viewing user credentials. We need these to establish connection
   * to MQTT-broker.
   * 
   * @author Tommi Tuomola
   */
  public static class Credentials {
    private static String userID = "";
    private static String password = "";
    
    public static void setUser(String user) {
      // possible validation here
      userID = user;
    }
    public static void setPass(String pass) {
      // possible validation here. pw method!
      password = pass;
    }
    public static String getUser() {
      return userID;
    }
    public static String getPass() {
      return password;
    }
  }
    
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
  }
}
