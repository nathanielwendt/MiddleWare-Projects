package entities;
import setup.Init;

public class BrokerDistributor {
	private static int[] brokerIndex = Init.BROKERINDEX;
	private static int numBrokers = Init.BROKERINDEX.length;
	private static int deployedBrokerCount = 1;
	private static int deployedClientCount = 0;
	
	public static synchronized int getNextBrokerPort(){
		if(deployedBrokerCount < numBrokers)
			return brokerIndex[(deployedBrokerCount++) - 1];
		else
			return -1;
	}
	
	public static synchronized int getBrokerAtIndex(int index){
		if(index < numBrokers)
			return Init.BROKERINDEX[index];
		else
			return -1;
	}
	
	public static synchronized void incrementClientCount(){
		++deployedClientCount;
	}
	
	public static synchronized int getNextClientNumber(){
		return ++deployedClientCount;
	}
	
	public static synchronized void updateCounts(int brokerCount, int clientCount){
		deployedBrokerCount = brokerCount;
		deployedClientCount = clientCount;
	}
	
	public static synchronized int getNumBrokers(){
		return numBrokers;
	}
	
	public static synchronized int getDeployedBrokerCount(){
		return deployedBrokerCount;
	}
	
	public static synchronized int getDeployedClientCount(){
		return deployedClientCount;
	}
	
	public BrokerDistributor(){}
	
}
