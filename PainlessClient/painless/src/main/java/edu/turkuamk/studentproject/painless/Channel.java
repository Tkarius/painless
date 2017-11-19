package edu.turkuamk.studentproject.painless;

//TODO: This class is a suggestion on how we want to handle information about each of user's subscribed channels.
//We could save a list of these guys in some kind of 'user' class to be used in populating the list (reading the channels
//from file), saving the list of channels to file and in populating the main window with the channels?
public class Channel {
  private String cName = "";
  private String role = "Client";
  // We'll implement the channel messages as a queue of 50 Strings in a static array.
  private final int CHANNEL_MAX_MESSAGES = 51;
  private String[] messages = new String[CHANNEL_MAX_MESSAGES]; 
  private int msgTail = 0;  // index to add the next message into
  private int msgHead = 0;  // index of the oldest message
	
  // constructor
  public Channel(String channelName, String _role) {
    setcName(channelName);
    setRole(_role);
    // TODO: should read stored messages from a file in constructor
  }
	
  public void addMsg(String msg) {
    int addToIndex = msgTail;
    if (addToIndex == CHANNEL_MAX_MESSAGES - 1) {
      addToIndex = 0;
      msgHead = 1;
    }
	messages[addToIndex] = msg;
	msgTail = addToIndex + 1;
	if (msgsFull()) {
		msgHead++;
	}
  }
  
  private boolean msgsFull() {
	int nextIndex = msgTail + 1;
    if (nextIndex == CHANNEL_MAX_MESSAGES) {
      nextIndex = 0;
    }
    return nextIndex == msgHead;
  }
  
  private boolean msgsEmpty() {
    return msgHead == msgTail;
  }
	
  public String getcName() {
    return cName;
  }
  
  private void setcName(String cName) {
    this.cName = cName;
  }
  
  public String getRole() {
    return role;
  }
  
  private void setRole(String role) {
    this.role = role;
  }

}
