package chatroomprovider;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.List;

import chatclient.ChatClient;

/**
 * Used to listen to the messages from 
 * chat clients and publish them to the 
 * chatroom this server is managing.
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public interface ChatroomServer extends Remote {
	/**
	 * Gets the UUID of the chatroom 
	 * server
	 * @return the UUID
	 * @throws RemoteException
	 */
	public String getUUID() throws RemoteException;	
	/**
	 * Gets the Info of the chatroom server
	 * in the form of a list
	 * @return - the info of the chatroom server
	 * @throws RemoteException
	 */
	public List<String> getInfo() throws RemoteException;	
	/**
	 * Registers the given client to the chatroom
	 * so that all the messages in the room are visible
	 * to the client.
	 * @param cc - the chat client instance
	 * @return - true if add is successful
	 * @throws RemoteException
	 */
	public boolean join(ChatClient cc) throws RemoteException;
	/**
	 * Deregisters the given client from the chatroom
	 * so that the client is not notified of any new 
	 * messages.
	 * @param cc - the chat client that needs to be removed 
	 * from the list.
	 * @return true is deregister is successful
	 * @throws RemoteException
	 */
	public boolean leave(ChatClient cc) throws RemoteException;
	/**
	 * Publishes the message to the subscribed chat
	 * clients
	 * @param message - the message to be published
	 * @throws RemoteException
	 */
	public void talk(String message) throws RemoteException;
	/**
	 * Checks if the given client has joined the chatroom
	 * @param cc - the chatclient to be checked for
	 * @return true if chatclient has joined the chatroom
	 * @throws RemoteException
	 */
	public boolean hasJoined(ChatClient cc) throws RemoteException;
	/**
	 * Gets the name of the chatroom
	 * @return - the name of the chatroom
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;
	/**
	 * Gets the location of the chatroom
	 * @return the location of the chatroom
	 * @throws RemoteException
	 */
	public String getLocation() throws RemoteException;
}
