/** Header
 * 
 */
package edu.turkuamk.studentproject.painless;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Painless.");
        login();
        MqttConnection mqtt = new MqttConnection();
        mqtt.mqttOpen();
        mqtt.mqttClose(); 
        System.out.println("Some pain will last.");
    }

    public static class Credentials {
    	private static String userID;
    	private static String password;
    	
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
    	final Scanner userInput = new Scanner(System.in);
    	Credentials.setUser(userInput.next());
    	System.out.println("Password: ");
    	Credentials.setPass(userInput.next());
    	userInput.close();
    }
}
