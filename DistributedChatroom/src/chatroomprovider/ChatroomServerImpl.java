package chatroomprovider;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import chatclient.ChatClient;
import entity.Entity;

public class ChatroomServerImpl extends Entity implements ChatroomServer {
	List<ChatClient> clients = new ArrayList<ChatClient>(); 
	private static final long serialVersionUID = -5921032464343099513L;
	
	public ChatroomServerImpl(String name, String location) throws RemoteException {
		super(name,location);
		//this.setUuid(String.valueOf(System.currentTimeMillis()));
	}
	
	public String getUUID() throws RemoteException{
		return this.getUuid();
	}
	
	public List<String> getInfo() throws RemoteException{
		List<String> newList = new ArrayList<String>();
		newList.add(this.getName());
		newList.add(this.getLocation());
		newList.add(String.valueOf(this.getStatusCode()));
		return newList;
	}
	
	public synchronized boolean hasJoined(ChatClient cc) throws RemoteException{
		for(ChatClient c : this.clients){
			if(c.getName().equals(cc.getName())){
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean join(ChatClient cc) throws RemoteException{
		return this.clients.add(cc);
	}

	public synchronized boolean leave(ChatClient cc) throws RemoteException{
		return this.clients.remove(cc);
	}

	public void talk(String message) throws RemoteException{
		List<ChatClient> copy;
		synchronized(this) {
		    copy = new ArrayList<ChatClient>(this.clients);
		}
		for(ChatClient c : copy) {
		    try {
				c.showMessage(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
