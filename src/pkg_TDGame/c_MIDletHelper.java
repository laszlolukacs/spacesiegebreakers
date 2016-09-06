package pkg_TDGame;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class c_MIDletHelper extends MIDlet {
	
	private Display dgDisp;
	private c_TDGameHelper instance;
	
	public c_MIDletHelper(){
		dgDisp = Display.getDisplay(this);
	}
	
	protected void startApp() {
		try {
			if(instance == null) {
				System.out.println("INFORMATION: MIDlet startup...");
				instance = new c_TDGameHelper(this, dgDisp); }
			else System.out.println("INFORMATION: MIDlet restored");
			instance.start();
		}
		catch (Exception ex) {
			System.err.println("WARNING: The following exception occurred: " + ex.toString());
			ex.printStackTrace();
		}
		
		dgDisp.setCurrent(instance);
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		System.out.println("INFORMATION: MIDlet shutdown");
		if(instance != null)
			instance.stop();
	}

	protected void pauseApp() {
		
	}
}
