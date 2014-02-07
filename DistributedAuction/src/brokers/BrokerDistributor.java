//@file BrokerDistributor.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Handles the deployment of brokers to the MasterBroker

package brokers;
import setup.Init;

public class BrokerDistributor {
	private static int[] brokerIndex = Init.BROKERINDEX;
	private static int numBrokers = Init.BROKERINDEX.length;
	private static int deployedBrokerCount = 1;
	private static int deployedClientCount = 0;
	
	// Gets the BrokerPort that is next available from the setup.Init java port list
	//@returns the integer value representing the nextbrokerport
	//@returns -1 if there are no more available brokers
	public static synchronized int getNextBrokerPort(){
		if(deployedBrokerCount < numBrokers)
			return brokerIndex[(deployedBrokerCount++) - 1];
		else
			return -1;
	}
	
	// Returns a broker at a given index position
	//@params index - position in broker index to find the broker
	//@returns the integer value of the brokerIndex
	//@returns -1 if the index is out of the range of brokers
	public static synchronized int getBrokerAtIndex(int index){
		if(index < numBrokers)
			return Init.BROKERINDEX[index];
		else
			return -1;
	}
	
	// Increments the count of the number of clients
	public static synchronized void incrementClientCount(){
		++deployedClientCount;
	}
	
	// Updates the associated broker and client counts
	//@params brokerCount - new value of the number of brokers
	//@params clientCount - new value of the number of clients
	public static synchronized void updateCounts(int brokerCount, int clientCount){
		deployedBrokerCount = brokerCount;
		deployedClientCount = clientCount;
	}
	
	// Gets the number of deployed brokers
	//returns the integer value representing the number of deployed brokers
	public static synchronized int getDeployedBrokerCount(){
		return deployedBrokerCount;
	}
	
	// Gets the number of deployed clients
	//returns the integer value representing the number of deployed clients
	public static synchronized int getDeployedClientCount(){
		return deployedClientCount;
	}
	
	// Default Constructor
	public BrokerDistributor(){}
	
}
