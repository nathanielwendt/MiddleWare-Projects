package chatroomprovider;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.List;

import setup.Init;
import theregistry.TheRegistry;

/**
 * Used to register and deregister chatrooms.
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
public class ChatRoomProvider {
	/**
	 * The provider object for singleton pattern
	 */
	private static ChatRoomProvider provider = null;
	/**
	 * The registry looked up from the actual RMI registry
	 */
	private static TheRegistry reg = null;

	public static void main(String args[]) throws Exception {
		ChatRoomProvider.getInstance().registerChatRoom("puppies","Atlanta,Georgia");
		ChatRoomProvider.getInstance().registerChatRoom("kittens","Austin,Texas");
		List<ChatroomServer> servers = reg.getChatRooms("SUDO");
		for(ChatroomServer cs : servers){
			System.out.println(cs.getInfo());
		}
		System.out.println("Added test rooms to the chat room provider!");
	}

	/**
	 * The singleton access to the chatroom
	 * provider object.
	 * @return the singleton object
	 */
	public static ChatRoomProvider getInstance(){
		if(provider == null){
			System.setSecurityManager(new RMISecurityManager());
			try {
				reg = (TheRegistry) Naming.lookup(Init.REGISTRY_LOOKUP_STRING);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
			provider = new ChatRoomProvider(reg);
		}
		return provider;
	}
	
	
	private ChatRoomProvider(TheRegistry register){
		reg = register;
	}

	/**
	 * Used to regsiter a new chatroom. Does not
	 * register if the chat room exists already.
	 * 
	 * @param name - name of the chatroom
	 * @param location -  location of the chatroom
	 */
	public void registerChatRoom(String name, String location){
		try {
			ChatroomServerImpl cs = new ChatroomServerImpl(name,location);
			if(reg.register(cs)){
			}else{
				System.err.println("Can't register a chatroom which has already been registered.");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if(Init.VERBOSE) {
			System.out.println("Registered chatroom " + name + " at " + location );
		}
	}

	/**
	 * Used to remove the chatroom off the
	 * registered chatrooms list
	 * 
	 * @param uuid - uuid of the chatroom
	 * @return returns true if the chatroom is 
	 * 			deregistered.
	 */
	public boolean deRegisterChatRoom(String uuid){
		try {
			return reg.deRegister(uuid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
}
