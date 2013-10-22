import java.util.Random;

import gui.MessageConsole;


public class GUITesterClass{

	public static void main(String[] args) {
		MessageConsole.invokeDebugConsole("buyer " + 54 + " console");
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(System.nanoTime());
		}
	}

}
