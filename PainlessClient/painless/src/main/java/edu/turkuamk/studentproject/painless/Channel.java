package edu.turkuamk.studentproject.painless;

//TODO: This class is a suggestion on how we want to handle information about each of user's subscribed channels.
//We could save a list of these guys in some kind of 'user' class to be used in populating the list (reading the channels
//from file), saving the list of channels to file and in populating the main window with the channels?
public class Channel {
	private String cName = "";
	private String role = "Client";
	
	public Channel(String channelName, String _role) {
		setcName(channelName);
		setRole(_role);
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
