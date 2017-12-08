package edu.turkuamk.studentproject.painless;

import java.util.ArrayList;

/**
 * Credentials class is a simple data structure for storing, setting
 * and viewing user credentials. We need these to establish connection
 * to MQTT-broker.
 * 
 * @author Tommi Tuomola
 */
public class Credentials {
  private static String userID = "";
  private static String password = "";
  private static ArrayList <PainlessChannel> channelList = new ArrayList <PainlessChannel>();
  
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
  public static ArrayList <PainlessChannel> getChannelList() {
    return channelList;
  }
  public static void setChannelList(ArrayList <PainlessChannel> channelList) {
    Credentials.channelList = channelList;
  }
}
