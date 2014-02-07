package chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import theregistry.TheRegistry;
import chatroomprovider.ChatroomServer;
import entity.Entity;
import gui.ChatClientGUI;
/**
 * Used to join chatrooms and chat in 
 * the partcular chatrooms joined.
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public class ChatClientImpl extends Entity implements ChatClient {
	private static final long serialVersionUID = 1680013954088361634L;
	private ChatClientGUI guiInstance;

	public ChatClientImpl(String name, String location,ChatClientGUI guiInstance) throws RemoteException{
		super(name,location);
		this.guiInstance = guiInstance;
		//this.setUuid(String.valueOf(System.currentTimeMillis()));
	}

	public static void main(String[] args){
		TheRegistry reg = null;
		System.setSecurityManager(new RMISecurityManager());
		try {
			reg = (TheRegistry) Naming.lookup("rmi://localhost/TheRegistry");
		} catch (Exception e) {
			//MalformedURLException | RemoteException | NotBoundException e
			e.printStackTrace();
		}

		try {
			//GUI prompt the user for a name and location, on clicking register button...
			//Get the String values for the following line from GUI
			ChatClientImpl me = new ChatClientImpl("natedawg", "bellingham,WA",null);
			reg.register(me);

			//GUI button will ask for chat rooms, then run this code
			List<ChatroomServer> temp = reg.getChatRooms(me);			
			if(temp == null){
				System.out.println("NULL Return, no permission to get chat roooms");
			}

			//This is an example of how to print out the chatrooms from the above command
			for(ChatroomServer csc : temp){
				System.out.println(csc.getInfo());
			}

			//GUI will allow to select which of available chatrooms to join
			temp.get(0).join(me);
			InputStreamReader streamReader = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			String chatText = "";

			System.out.println("Enter some text to process -> ");

			//Example code for polling on input, to be replaced by GUI
			while(true){
				try {
					chatText = bufferedReader.readLine();
					if(chatText.equals("exit"))
						temp.get(0).leave(me);
					else
						temp.get(0).talk(chatText);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	    	
		} catch (RemoteException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public String getUUID() throws RemoteException {
		return this.getUuid();
	}

	@Override
	public List<String> getInfo() throws RemoteException {
		List<String> newList = new ArrayList<String>();
		newList.add(this.getName());
		newList.add(this.getLocation());
		newList.add(String.valueOf(this.getStatusCode()));
		return newList;
	}

	@Override
	public void showMessage(String message) throws RemoteException {
		if(guiInstance == null){
			System.out.println(message);
		}else{
			guiInstance.writeToAllChatPane(message);
		}
	}



}