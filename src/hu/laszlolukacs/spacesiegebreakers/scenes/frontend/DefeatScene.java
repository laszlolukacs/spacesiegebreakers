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

/**
 * The static {@link Scene} which is shown after a crushing defeat.
 */
public class DefeatScene extends BaseMicroEditionScene implements Scene {

	public static final String TAG = "DefeatScene";
	
	private static final long INPUT_DEBOUNCE_TIME = 166; // ms

	private Image defeatHeader;
	private Image backgroundImage;
	private GameTimer inputTimer = new GameTimer(INPUT_DEBOUNCE_TIME);
	private int score = 0;

	public DefeatScene() {
		super();
	}

	public void init() {
		try {
			this.defeatHeader = Image.createImage("/legacy/ui_defeat.png");
			this.backgroundImage = Image.createImage("/legacy/splash_gameover.png");
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
	
	public void setScore(final int score) {
		this.score = score;
	}

	private void drawStaticScene() {
		g.drawImage(backgroundImage, screen.getCenterX(), screen.getCenterY(),
				Graphics.VCENTER | Graphics.HCENTER);
		g.drawImage(defeatHeader, screen.getCenterX(), 24,
				Graphics.TOP | Graphics.HCENTER);

		textRenderer.drawNormalText("You have been defeated!",
				screen.getCenterX(), screen.getCenterY(),
				TextAnchor.TOP | TextAnchor.HCENTER);
		textRenderer.drawHeaderText("Total score: " + score,
				screen.getCenterX(), screen.getCenterY() + 16,
				TextAnchor.TOP | TextAnchor.HCENTER);
		textRenderer.drawHeaderText("Press 'FIRE' to begin",
				screen.getCenterX(), 
				screen.getHeight() - 48,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);

		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();
		if ((keyStates & FIRE_PRESSED) != 0) {
			// returns to main menu
			Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.MAIN_MENU);
			Game.setScene(nextScene);
		}
		
		inputTimer.setEnabled(keyStates != 0);
		inputTimer.reset();
	}
}
