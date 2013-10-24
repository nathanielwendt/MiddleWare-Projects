//@file Init.java
//@author Nathaniel Wendt, Raga Srinivasan
//@ Initialization file for setting up what ports to connect and for establishing the masterport.
//@ Also allows for enabling some thread sleeping options to improve performance (when simulating on one machine)
//@ and debugging information through VERBOSE;

package setup;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class Init {
	public static String NETWORKNAME = "";
	static{
		try {
			NETWORKNAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	public static int MASTERPORT = 4000;
	public static int[] BROKERINDEX = {4001,4002,4003,4004};
	
	public static boolean VERBOSE = true; //set to false to diable all debug messages
	public static boolean ENABLE_THREAD_SLEEP = true; //enable thread sleep in all while(true) loops to decrease load on computer.
	public static long THREAD_SLEEP_INTERVAL = 1000; // the interval for which the threads must be paused for.
	public static Gson gsonConverter = new Gson(); //the gson object for all conversions
}
