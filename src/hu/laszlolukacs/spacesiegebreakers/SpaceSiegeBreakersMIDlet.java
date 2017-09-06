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
 * MID Profile application.
 * 
 * @author laszlolukacs
 */
public class SpaceSiegeBreakersMIDlet extends MIDlet {
	public static final String TAG = "SpaceSiegeBreakersMIDlet";

	private Display display;
	private Game game;

	/**
	 * Initializes a new instance of the `SpaceSiegeBreakersMIDlet` class.
	 */
	public SpaceSiegeBreakersMIDlet() {
		Log.i(TAG,
				"Space Siege Breakers for J2ME, created by Laszlo Lukacs 2010, 2017");
		this.display = Display.getDisplay(this);
	}

	/**
	 * Signals the MIDlet that it has entered the Active state.
	 * 
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		try {
			if (this.game == null) {
				Log.i(TAG, "MIDlet starting up...");
				this.game = new Game(this, this.display);
			} else {
				Log.i(TAG, "MIDlet restored.");
			}

			this.game.start();
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
		// TODO Auto-generated method stub
		Log.i(TAG, "Application paused!");

	}

	/**
	 * Signals the MIDlet to terminate and enter the Destroyed state.
	 * 
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
		Log.i(TAG, "MIDlet shutting down...");
		if (this.game != null) {
			this.game.stop();
		}
	}

	/*
	 * Cleans up and notifies that the MIDlet has been destroyed.
	 */
	public void exit() {
		try {
			this.destroyApp(false);
		} catch (MIDletStateChangeException e) {
			Log.e(TAG, "Failed to destroy MIDlet, reason: " + e.getMessage());
			e.printStackTrace();
		}

		this.notifyDestroyed();
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
