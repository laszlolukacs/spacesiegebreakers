/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import hu.laszlolukacs.spacesiegebreakers.utils.Log;

/**
 * The methods of this class allow the underlying system application management
 * software to create, start, pause, and destroy the `SpaceSiegeBreakersMIDlet`
 * application.
 */
public class SpaceSiegeBreakersMIDlet extends MIDlet {
	public static final String TAG = "SpaceSiegeBreakersMIDlet";

	private final Display display;
	private Game game;

	public SpaceSiegeBreakersMIDlet() {
		Log.i(TAG,
				"Space Siege Breakers for J2ME, created by Laszlo Lukacs 2010, 2017");

		this.display = Display.getDisplay(this);

		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get amount of free memory within the heap in bytes. This size will
		// increase after garbage collection and decrease as new objects are
		// created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		Log.i(TAG, "Free memory/heap size: " + heapFreeSize + "/" + heapSize
				+ " bytes");
	}

	/**
	 * Signals the MIDlet that it has entered the Active state.
	 * 
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		try {
			if (game == null) {
				Log.i(TAG, "MIDlet starting up...");
				game = new Game(this, display);
			} else {
				Log.i(TAG, "MIDlet restored.");
			}

			game.startLoop();
		} catch (Exception e) {
			Log.e(TAG, "Failed to start MIDlet, reason: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Signals the MIDlet to stop and enter the Paused state.
	 * 
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		Log.i(TAG, "MIDlet paused");
	}

	/**
	 * Signals the MIDlet to terminate and enter the Destroyed state.
	 * 
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		Log.i(TAG, "MIDlet shutting down...");
		if (game != null) {
			game.stopLoop();
		}
	}

	/**
	 * Cleans up and notifies that the MIDlet has been destroyed.
	 */
	public void exit() {
		try {
			destroyApp(false);
		} catch (MIDletStateChangeException e) {
			Log.e(TAG, "Failed to destroy MIDlet, reason: " + e.getMessage());
			e.printStackTrace();
		}

		notifyDestroyed();
	}

	/**
	 * The entry point for the application. Not used by the MIDlet.
	 * 
	 * @param args
	 *            Array of strings containing the arguments that was passed to
	 *            the application.
	 */
	public static void main(String[] args) {
		Log.i(TAG,
				"This application is intended to be launched only as a MIDlet.");
	}
}
