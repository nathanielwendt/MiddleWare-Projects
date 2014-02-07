package chatclient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Used to join chatrooms and chat in 
 * the partcular chatrooms joined.
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public interface ChatClient extends Remote {
	/**
	 * Shows the message in the given GUI or in the console
	 * @param message - the message that needs to be showed
	 * @throws RemoteException if there is a problem
	 */
	public void showMessage(String message) throws RemoteException;
	/**
	 * Gets the UUID of the client
	 * @return the UUID
	 * @throws RemoteException
	 */
	public String getUUID() throws RemoteException;
	/**
	 * Gets the info about the client
	 * in the form of a list of Strings.
	 * @return - the info about the client
	 * @throws RemoteException
	 */
	public List<String> getInfo() throws RemoteException;
	/**
	 * Gets the name of the client
	 * @return - the name of the client
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;
	/**
	 * Gets the location of the client.
	 * @return - the location of the client
	 * @throws RemoteException
	 */
	public String getLocation() throws RemoteException;
}
