package chatserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import chatclient.ChatClient;

public interface ChatServer extends Remote {
	public String getUUID() throws RemoteException;
	public List<String> getInfo() throws RemoteException;
	
	public boolean join(ChatClient cc) throws RemoteException;
	public boolean leave(ChatClient cc) throws RemoteException;
	public void talk(String message) throws RemoteException;
}
