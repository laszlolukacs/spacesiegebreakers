/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.game.GameCanvas;

import hu.laszlolukacs.spacesiegebreakers.scenes.Scene;
import hu.laszlolukacs.spacesiegebreakers.scenes.SceneFactory;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

/**
 * Contains the core main game loop which is intended to be executed by a
 * thread.
 * 
 * @author laszlolukacs
 */
public class Game extends GameCanvas implements Runnable {
	
	public static final String TAG = "Game";

	// specifies the target speed in ~ms / frame, e.g. 33 ms / frame = 30 fps
	private static final long CYCLE_TIME_THRESHOLD = 33; // ms / frame = 30 fps

	private volatile boolean isRunning = false;

	public static Scene currentScene;
	public static Display display;
	public static SpaceSiegeBreakersMIDlet midlet;

	public Game(SpaceSiegeBreakersMIDlet midlet, Display display) {
		super(true);
		Game.midlet = midlet;
		Game.display = display;
	}

	/**
	 * Executes the game's core main loop.
	 */
	public void run() {
		Log.i(TAG, "Entering the main game loop.");
		long cycleCompleteTime = System.currentTimeMillis();
		
		// main loop of the game
		while (isRunning) {
			final long cycleStartTime = System.currentTimeMillis();
			long delta = System.currentTimeMillis() - cycleCompleteTime;
			currentScene.update(delta);
			delta = System.currentTimeMillis() - cycleCompleteTime;
			currentScene.render(delta);

			// makes the thread sleep if the game cycle finishes on time
			cycleCompleteTime = System.currentTimeMillis();
			long cycleTime = cycleCompleteTime - cycleStartTime;
			if (cycleTime < CYCLE_TIME_THRESHOLD) {
				synchronized (this) {
					try {
						Thread.sleep(CYCLE_TIME_THRESHOLD - cycleTime);
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
		setScene(SceneFactory.createSceneByKey(SceneFactory.SPLASH_SCREEN));
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
}
