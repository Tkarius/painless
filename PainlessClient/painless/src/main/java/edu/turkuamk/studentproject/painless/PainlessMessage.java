package edu.turkuamk.studentproject.painless;

import java.time.LocalTime;

public class PainlessMessage {
  private String msgContent = "";
  private String msgAuthor = "";
  private LocalTime timeStamp = LocalTime.now();
  
  public PainlessMessage(String message) {
	  String[] authorAndMessage = message.split(" ", 2);
	  msgAuthor = authorAndMessage[0];
	  msgContent = authorAndMessage[1];
  }
  
  @Override
  public String toString() {
    return "[" + timeStamp.toString() + "]" + "[" + msgAuthor + "] " + msgContent; 
  }
  
}
