package chatserver;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import chatclient.ChatClient;
import entity.Entity;
import theregistry.TheRegistryImpl;

public class ChatServerImpl extends Entity implements ChatServer {
	List<ChatClient> clients = new ArrayList<ChatClient>(); //remember to synchronize methods on this data structure - many users
	private static final long serialVersionUID = 1680013954088361634L; //added for serializable class safety

	public ChatServerImpl(String name, String location) throws RemoteException{
		super(name,location);
	}

	@Override
	public String getUUID() throws RemoteException {
		return this.getUUID();
	}
	
	public List<String> getInfo() throws RemoteException {
		List<String> newList = new ArrayList<String>();
		newList.add(this.getName());
		newList.add(this.getLocation());
		newList.add(String.valueOf(this.getStatusCode()));
		return newList;
	}

	@Override
	public synchronized boolean join(ChatClient cc) throws RemoteException {
		return this.clients.add(cc);
	}

	@Override
	public synchronized boolean leave(ChatClient cc) throws RemoteException {
		return this.clients.remove(cc);
	}

	@Override
	public void talk(String message) throws RemoteException {
		List<ChatClient> copy;
		synchronized(this) {
		    copy = new ArrayList<ChatClient>(this.clients);
		}
		for(ChatClient c : copy) {
		    c.showMessage(message);
		}
	}
	
}
