package entity;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * Used to represent an entity in the whole
 * system. ex - chatroom
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public class Entity extends UnicastRemoteObject {
	private static final long serialVersionUID = -3420042203470495360L; 
	/**
	 * UUid of the entity
	 */
	private final String uuid;
	/**
	 * name of the entity
	 */
	private final String name;
	/**
	 * Location of the entity
	 */
	private final String location;
	/**
	 * Status code of the entity. (currently not in use, may be put to use in the future).
	 */
	private int statusCode;
	
	public Entity(String name, String location) throws RemoteException {
		this.uuid = UUIDGenerator.getNextUUID();
		this.name = name;
		this.location = location;
		this.statusCode = 0;
	}
	
	/**
	 * Gets the status code of the entity.
	 * @return status code
	 */
	public int getStatusCode(){
		return this.statusCode;
	}
	
	/**
	 * Gets the location of the entity
	 * @return the location
	 */
	public String getLocation(){
		return this.location;
	}
	
	/**
	 * Gets the name of the entity
	 * @return the name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Gets the UUID of the entity
	 * @return uuid of the entity
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the status code of the entity
	 * @param status - the status code that needs to be set
	 */
	public void setStatusCode(int status){
		this.statusCode = status;
	}
}
