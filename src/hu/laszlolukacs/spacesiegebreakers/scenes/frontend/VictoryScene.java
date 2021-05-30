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

public class VictoryScene extends BaseMicroEditionScene implements Scene {

	public static final String TAG = "VictoryScene";

	private final Graphics g;

	private Image victoryHeader;
	private Image backgroundImage;

	public VictoryScene() {
		this.g = super.getGraphics();
	}

	public void init() {
		try {
			this.victoryHeader = Image.createImage("/legacy/ui_victory.png");
			this.backgroundImage = Image
					.createImage("/legacy/splash_gameover.png");
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
		final int score = 0;

		g.drawImage(backgroundImage, screen.getCenterX(), screen.getCenterY(),
				Graphics.VCENTER | Graphics.HCENTER);
		g.drawImage(victoryHeader, screen.getCenterX(), 24,
				Graphics.TOP | Graphics.HCENTER);

		textRenderer.drawNormalText("You have successfully defended the ship!",
				screen.getCenterX(), screen.getCenterY(),
				TextAnchor.TOP | TextAnchor.HCENTER);
		textRenderer.drawHeaderText("Total score: " + score,
				screen.getCenterX(), screen.getCenterY() + 16,
				TextAnchor.TOP | TextAnchor.HCENTER);

		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();
		if ((keyStates & FIRE_PRESSED) != 0
				&& (System.currentTimeMillis() - lastTimeButtonPressed > 300)) {
			lastTimeButtonPressed = System.currentTimeMillis();

			// returns to main menu
			Scene nextScene = SceneFactory
					.createSceneByKey(SceneFactory.MAIN_MENU);
			Game.setScene(nextScene);
		}
	}
}
