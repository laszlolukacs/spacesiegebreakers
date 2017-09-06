package hu.laszlolukacs.spacesiegebreakers;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import hu.laszlolukacs.spacesiegebreakers.scenes.MainMenuScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.Scene;
import hu.laszlolukacs.spacesiegebreakers.scenes.SplashScreenScene;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

/**
 * Contains the core main game loop which is intended to be executed by a
 * thread.
 * 
 * @author laszlolukacs
 */
public class Game implements Runnable {
	public static final String TAG = "Game";

	private static boolean isRunning = false;
	private static long cycleStartTime;
	private static long cycleCompleteTime;
	private static long framesPerSec;

	public static Scene currentScene;
	public static Display display;
	public static SpaceSiegeBreakersMIDlet midlet;

	/**
	 * Initializes a new instance of the `Game` class.
	 * 
	 * @author laszlolukacs
	 */
	public Game(SpaceSiegeBreakersMIDlet midlet, Display display) {
		Game.midlet = midlet;
		Game.display = display;
	}

	// specifies the target running speed in ~ms / frame
	// e.g. 20 ms / frame = 50 fps
	private static final long CYCLE_TIME_THRESHOLD = 20; // ms / frame

	public void run() {
		Log.i(TAG, "Loading game resources...");
		Game.init();

		Log.i(TAG, "Main game loop is starting...");
		// Graphics g = getGraphics();
		// main loop of the game
		while (Game.isRunning) {
			Game.cycleStartTime = System.currentTimeMillis();
			Game.update(); // updates game state
			Game.render(); // renders a frame

			// makes the thread sleep if the game cycle finishes on time
			Game.cycleCompleteTime = System.currentTimeMillis()
					- Game.cycleStartTime;
			Game.framesPerSec = 1000 / (cycleCompleteTime + 1);
			if (Game.cycleCompleteTime < CYCLE_TIME_THRESHOLD) {
				synchronized (this) {
					try {
						Thread.sleep(
								CYCLE_TIME_THRESHOLD - Game.cycleCompleteTime);
					} catch (InterruptedException e) {
						Log.e(TAG,
								"Game thread has been interrupted, reason: " + e.getMessage());
						e.printStackTrace();
						this.stop();
					}
				}
			} else {
				Log.w(TAG,
						"Game cycle time threshold has been hit! (fps < 50)");
				Thread.yield();
			}
		}

		Log.i(TAG, "Main game loop stopped.");
		this.stop();
	}

	public void start() {
		Game.init();
		Game.isRunning = true;
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	public void stop() {
		Game.isRunning = false;
	}
	
	public static void setScene(Scene scene) {
		scene.init();
		Game.currentScene = scene;
		Game.display.setCurrent((Displayable) Game.currentScene);
	}

	private static void init() {
		Game.setScene(new SplashScreenScene(Game.midlet));
	}

	/**
	 * Allows the game to gather input and update the game state.
	 */
	private static void update() {
		Game.currentScene.update();
	}

	/**
	 * Draws a frame based on the current game state.
	 */
	private static void render() {
		Game.currentScene.render();
	}
}
