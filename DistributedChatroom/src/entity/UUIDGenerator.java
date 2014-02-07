/**
 * Used to generate UUIDs for various
 * entities.
 * 
 * @author Raghavendra Srinivasan, Nathaniel Wendt
 */
package entity;
import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {
	/**
	 * The local counter used to ensure that UUID
	 * generated on local host is actually unique.s
	 */
	private static int uuidLocalCounter = 0;
	/**
	 * Random generator instance used to 
	 * generate random numbers
	 */
	private static final Random rand = new Random();
	
	public static String getNextUUID(){
		StringBuilder builder = new StringBuilder();
		builder.append(UUID.randomUUID());
		builder.append(System.nanoTime());
		builder.append(uuidLocalCounter++);
		builder.append(Math.abs(rand.nextInt()));
		return builder.toString();
	}
	
}
