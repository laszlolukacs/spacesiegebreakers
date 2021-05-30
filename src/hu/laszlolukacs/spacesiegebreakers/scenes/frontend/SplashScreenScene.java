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
import hu.laszlolukacs.spacesiegebreakers.utils.Log;
import hu.laszlolukacs.spacesiegebreakers.utils.TextAnchor;

public class SplashScreenScene extends BaseMicroEditionScene implements Scene {

	public static final String TAG = "SplashScreenScene";

	private final Graphics g;

	private Image logoImage;
	private Image backgroundImage;

	public SplashScreenScene() {
		this.g = super.getGraphics();
	}

	public void init() {
		try {
			this.logoImage = Image.createImage("/legacy/splash_logo.png");
			this.backgroundImage = Image
					.createImage("/legacy/splash_startup.png");
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
		getInput();
	}

	public void render(final long delta) {
	}

	private void drawStaticScene() {
		g.drawImage(backgroundImage, screen.getCenterX(), screen.getCenterY(),
				Graphics.VCENTER | Graphics.HCENTER);
		g.drawImage(logoImage, screen.getCenterX(), 24,
				Graphics.TOP | Graphics.HCENTER);
		textRenderer.drawHeaderText("Press 'FIRE' to begin",
				screen.getCenterX(), screen.getHeight() - 48,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
		textRenderer.drawThinText("Created by Laszlo Lukacs, 2010",
				screen.getCenterX(), screen.getHeight() - 12,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();
		if ((keyStates & FIRE_PRESSED) != 0
				&& (System.currentTimeMillis() - lastTimeButtonPressed > 300)) {
			lastTimeButtonPressed = System.currentTimeMillis();
			Scene nextScene = SceneFactory
					.createSceneByKey(SceneFactory.MAIN_MENU);
			Game.setScene(nextScene);
		}
	}
}
