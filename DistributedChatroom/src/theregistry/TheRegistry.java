package theregistry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import chatclient.ChatClient;
import chatroomprovider.ChatroomServer;

/**
 * The registry for the system that
 * is used to 
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public interface TheRegistry extends Remote {
	/**
	 * Registers the chatroomserver to the system
	 * @param cs - the chatroomserever instance
	 * @return true if successful
	 * @throws RemoteException
	 */
    public boolean register(ChatroomServer cs) throws RemoteException;
    /**
	 * Registers the chatroomclient to the system
	 * @param cc - the chatroomclient instance
	 * @return true if successful
	 * @throws RemoteException
	 */
    public boolean register(ChatClient cc) throws RemoteException;
    /**
	 * Deregisters the chatroomserver to the system
	 * @param cs - the chatroomserever instance
	 * @return true if successful
	 * @throws RemoteException
	 */
	public boolean deRegister(ChatroomServer cs) throws RemoteException;
	/**
	 * Deregisters the chatroomclient to the system
	 * @param cs - the chatroomclient instance
	 * @return true if successful
	 * @throws RemoteException
	 */
	public boolean deRegister(String cs) throws RemoteException;
	/**
	 * Deregisters the chatroomclient to the system
	 * @param cc - the chatroomclient instance
	 * @return true if successful
	 * @throws RemoteException
	 */
	public boolean deRegister(ChatClient cc) throws RemoteException;
	/**
	 * Gets the info of the entity, given the UUID
	 * @param uuid - the uuid of the entity
	 * @return the info in a list form
	 * @throws RemoteException
	 */
	public List<String> getInfo(String uuid) throws RemoteException;
	//only allows registered chat clients to access the chat rooms
	/**
	 * Gets the list of chatrooms registered in
	 * the system's registry.
	 * 
	 * @param cc - the chat client that is registered
	 * 			to the system
	 * @return - list of servers if client is registered, if not, NULL
	 * @throws RemoteException
	 */
	public List<ChatroomServer> getChatRooms(ChatClient cc) throws RemoteException;
	
	//allows the chat room provider to get a list of chat rooms with correct password
	/**
	 * Gets the list of chatrooms registered in
	 * the system's registry.
	 * 
	 * @param adminPass - the admin password
	 * @return - list of servers if admin password is correct, if not, NULL
	 * @throws RemoteException
	 */
	public List<ChatroomServer> getChatRooms(String adminPass) throws RemoteException;
}
