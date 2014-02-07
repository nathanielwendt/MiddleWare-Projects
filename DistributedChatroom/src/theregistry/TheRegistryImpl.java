package theregistry;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import chatclient.ChatClient;
import chatroomprovider.ChatroomServer;


public class TheRegistryImpl extends UnicastRemoteObject implements TheRegistry {
	private static final long serialVersionUID = -5062793525610290572L;
	Map<String,ChatroomServer> chatroomServers = new HashMap<String,ChatroomServer>();
	Map<String,ChatClient> chatClients = new HashMap<String,ChatClient>();
	
	public TheRegistryImpl() throws RemoteException {}
	
    public static void main(String[] args) throws Exception {
    	System.setSecurityManager(new RMISecurityManager());
    	Naming.rebind("TheRegistry", new TheRegistryImpl());
    	System.out.println("Started running the registry!");
    }
    
    
    @Override
    public synchronized boolean register(ChatroomServer cs) throws RemoteException {
    	if(this.chatroomServers.get(cs.getUUID()) == null){
    		this.chatroomServers.put(cs.getUUID(), cs);
    		return true;
    	} else
    		return false;
    }
    
    @Override
    public synchronized boolean register(ChatClient cc) throws RemoteException{
    	if(this.chatClients.get(cc.getUUID()) == null){
    		this.chatClients.put(cc.getUUID(), cc);
    		return true;
    	} else
    		return false;
    }
    
    public synchronized boolean deRegister(ChatroomServer cs) throws RemoteException {
    	if( this.chatroomServers.get(cs.getUUID()) != null ){
    		this.chatroomServers.remove(cs.getUUID());
    		return true;
    	} else
    		return false;
    }
    
    public synchronized boolean deRegister(String uuid) throws RemoteException {
    	if( this.chatroomServers.get(uuid) != null ){
    		this.chatroomServers.remove(uuid);
    		return true;
    	} else
    		return false;
    }
    
    public boolean deRegister(ChatClient cc) throws RemoteException {
    	if( this.chatClients.get(cc.getUUID()) != null ){
    		this.chatClients.remove(cc.getUUID());
    		return true;
    	} else
    		return false;
    }

	@Override
	public List<String> getInfo(String uuid) throws RemoteException {
		if(this.chatroomServers.get(uuid) != null){
			return this.chatroomServers.get(uuid).getInfo();
		} else if (this.chatClients.get(uuid) != null){
			return this.chatClients.get(uuid).getInfo();
		} else
			return null;
	}

	@Override
	public List<ChatroomServer> getChatRooms(ChatClient cc) throws RemoteException {
		if( this.chatClients.get(cc.getUUID()) != null ){ //make sure the user is registered
			List<ChatroomServer> rooms = new ArrayList<ChatroomServer>();
			for(ChatroomServer cs : this.chatroomServers.values()){
				rooms.add(cs);
			}
			return rooms;
		} else
			return null;
	}
	
	@Override
	
	//allows the chat room provider to get a list of chat rooms with correct password
	//here for testing as of now. Will be removed in the final version.
	public List<ChatroomServer> getChatRooms(String adminPass) throws RemoteException {
		if( adminPass.equals("SUDO") ){ //make sure the user is registered
			List<ChatroomServer> rooms = new ArrayList<ChatroomServer>();
			for(ChatroomServer cs : this.chatroomServers.values()){
				rooms.add(cs);
			}
			return rooms;
		} else
			return null;
	}
}
