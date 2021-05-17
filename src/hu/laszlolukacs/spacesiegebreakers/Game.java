package hu.laszlolukacs.spacesiegebreakers;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import hu.laszlolukacs.spacesiegebreakers.scenes.Scene;
import hu.laszlolukacs.spacesiegebreakers.scenes.SceneFactory;
import hu.laszlolukacs.spacesiegebreakers.scenes.SceneKeys;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

/**
 * Contains the core main game loop which is intended to be executed by a
 * thread.
 * 
 * @author laszlolukacs
 */
public class Game implements Runnable {
	public static final String TAG = "Game";

	// specifies the target speed in ~ms / frame, e.g. 33 ms / frame = 30 fps
	private static final long CYCLE_TIME_THRESHOLD = 33; // ms / frame = 30 fps

	private static long framesPerSec;
	private boolean isRunning = false;
	private long cycleStartTime;
	private long cycleCompleteTime;

	public static Scene currentScene;
	public static Display display;
	public static SpaceSiegeBreakersMIDlet midlet;

	public Game(SpaceSiegeBreakersMIDlet midlet, Display display) {
		Game.midlet = midlet;
		Game.display = display;
	}

	/**
	 * Executes the game's core main loop.
	 */
	public void run() {
		Log.i(TAG, "Entering the main game loop.");
		// main loop of the game
		while (isRunning) {
			cycleStartTime = System.currentTimeMillis();
			currentScene.update();
			currentScene.render();

			// makes the thread sleep if the game cycle finishes on time
			cycleCompleteTime = System.currentTimeMillis() - cycleStartTime;
			framesPerSec = 1000 / (cycleCompleteTime + 1);
			if (cycleCompleteTime < CYCLE_TIME_THRESHOLD) {
				synchronized (this) {
					try {
						Thread.sleep(CYCLE_TIME_THRESHOLD - cycleCompleteTime);
					} catch (InterruptedException e) {
						Log.e(TAG, "Game thread has been interrupted, reason: "
								+ e.getMessage());
						e.printStackTrace();
						this.stopLoop();
					}
				}
			} else {
				Log.w(TAG,
						"Game cycle time threshold has been hit! (fps < 30)");
				Thread.yield();
			}
		}

		Log.i(TAG, "Main game loop stopped.");
		this.stopLoop();
	}

	public void startLoop() {
		setScene(SceneFactory.createSceneByKey(SceneKeys.SPLASH_SCREEN));
		isRunning = true;
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	public void stopLoop() {
		isRunning = false;
	}

	public static void setScene(Scene scene) {
		scene.init();
		currentScene = scene;
		display.setCurrent((Displayable) currentScene);
	}

	public static void quit() {
		midlet.exit();
	}

	// /**
	// * Allows the game to gather input and update the game state.
	// */
	// private static void update() {
	// currentScene.update();
	// }
	//
	// /**
	// * Draws a frame based on the current game state.
	// */
	// private static void render() {
	// currentScene.render();
	// }
}
