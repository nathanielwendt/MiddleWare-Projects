package includes;
import java.util.Random;
import java.util.UUID;


public class UUIDGenerator {
	private static int uuidLocalCounter = 0;
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
