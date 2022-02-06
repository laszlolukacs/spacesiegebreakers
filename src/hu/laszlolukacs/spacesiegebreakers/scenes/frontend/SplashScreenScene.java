/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes.frontend;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.scenes.BaseMicroEditionScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.Scene;
import hu.laszlolukacs.spacesiegebreakers.scenes.SceneFactory;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;
import hu.laszlolukacs.spacesiegebreakers.utils.TextAnchor;
import hu.laszlolukacs.spacesiegebreakers.utils.Version;

/**
 * The static {@link Scene} shown at application startup.
 */
public class SplashScreenScene extends BaseMicroEditionScene implements Scene {

	public static final String TAG = "SplashScreenScene";
	
	private static final long INPUT_DEBOUNCE_TIME = 166; // ms

	private Image logoImage;
	private Image backgroundImage;
	private GameTimer inputTimer = new GameTimer(INPUT_DEBOUNCE_TIME);

	public SplashScreenScene() {
		super();
	}

	public void init() {
		try {
			this.logoImage = Image.createImage("/legacy/splash_logo.png");
			this.backgroundImage = Image.createImage("/legacy/splash_startup.png");
			drawStaticScene();
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update(final long delta) {
		inputTimer.update(delta);
		if (inputTimer.isThresholdReached()) {
			getInput();
		}
	}

	public void render(final long delta) {
		// active rendering will not be performed, see drawStaticScene()
	}

	private void drawStaticScene() {
		g.drawImage(backgroundImage, screen.getCenterX(), screen.getCenterY(),
				Graphics.VCENTER | Graphics.HCENTER);
		g.drawImage(logoImage, 
				screen.getCenterX(), 
				24,
				Graphics.TOP | Graphics.HCENTER);
		textRenderer.drawHeaderText("Press 'FIRE' to begin",
				screen.getCenterX(), 
				screen.getHeight() - 48,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
		textRenderer.drawThinText("Created by Laszlo Lukacs, " + Version.INITIAL_YEAR,
				screen.getCenterX(), 
				screen.getHeight() - 12,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
		textRenderer.drawThinText(Version.CURRENT_VERSION_STRING,
				screen.getWidth(),
				screen.getHeight(),
				TextAnchor.BOTTOM | TextAnchor.RIGHT);
		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();
		if ((keyStates & FIRE_PRESSED) != 0) {
			Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.MAIN_MENU);
			Game.setScene(nextScene);
		}
		
		inputTimer.setEnabled(keyStates != 0);
		inputTimer.reset();
	}
}
