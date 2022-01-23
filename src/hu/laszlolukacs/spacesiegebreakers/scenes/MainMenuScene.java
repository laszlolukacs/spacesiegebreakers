/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.ui.MenuButton;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;
import hu.laszlolukacs.spacesiegebreakers.utils.TextAnchor;
import hu.laszlolukacs.spacesiegebreakers.utils.Version;

/**
 * The {@link Scene} which displays the main menu.
 */
public class MainMenuScene extends BaseMicroEditionScene implements Scene {

	public static final String TAG = "MainMenuScene";

	private static final long INPUT_DEBOUNCE_TIME = 166; // ms
	private static final int UI_CONTROL_SIZE_PX = 32;

	private static final String START_GAME_BUTTON_LABEL = "Begin the defense";
	private static final String QUIT_BUTTON_LABEL = "Abandon ship";

	private final LayerManager layerManager;

	private Image mainMenuBackground;
	private Image mainMenuHeader;

	private MenuButton[] buttons = new MenuButton[2];
	private MenuButton selectedButton = null;
	
	private GameTimer inputTimer = new GameTimer(INPUT_DEBOUNCE_TIME);

	public MainMenuScene() {
		super();
		layerManager = new LayerManager();
	}

	public void init() {
		Log.i(TAG, "Loading main menu resources... ");
		try {
			mainMenuBackground = Image.createImage("/legacy/splash_menu.png");
			mainMenuHeader = Image.createImage("/legacy/ui_main.png");
			Image buttonFramesImage = Image.createImage("/legacy/ui_controls.png");
			buttons[0] = createButton(MenuButton.START_GAME, buttonFramesImage);
			buttons[1] = createButton(MenuButton.QUIT, buttonFramesImage);
			selectButton(MenuButton.START_GAME);
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}
	
	private MenuButton createButton(final int buttonIndex, final Image buttonFramesImage) {
		Sprite buttonSprite = new Sprite(buttonFramesImage, UI_CONTROL_SIZE_PX,	UI_CONTROL_SIZE_PX);
		int x = screen.getCenterX() - 16;
		String label;
		switch (buttonIndex) {
		case MenuButton.START_GAME:
			buttonSprite.setPosition(x, screen.getCenterY() - 18);
			buttonSprite.setFrame(2);
			label = START_GAME_BUTTON_LABEL;
			break;
		case MenuButton.QUIT:
			buttonSprite.setPosition(x, screen.getCenterY() + 18);
			buttonSprite.setFrame(4);
			label = QUIT_BUTTON_LABEL;
			break;
		default:
			label = "";
			break;
		}

		buttonSprite.setVisible(true);
		layerManager.append(buttonSprite);
		return new MenuButton(buttonIndex, buttonSprite, label);
	}

	public void update(final long delta) {
		inputTimer.update(delta);
		if (inputTimer.isThresholdReached()) {
			getInput();
		}
	}

	public void render(final long delta) {
		g.drawImage(mainMenuBackground, 
				screen.getCenterX(),
				screen.getCenterY(), 
				Graphics.VCENTER | Graphics.HCENTER);
		g.drawImage(mainMenuHeader, 
				screen.getCenterX(), 
				24,
				Graphics.TOP | Graphics.HCENTER);

		if (selectedButton != null) {
			textRenderer.drawNormalText(selectedButton.getDescription(),
					screen.getCenterX(), 
					screen.getCenterY() - 48,
					TextAnchor.TOP | TextAnchor.HCENTER);
		}

		textRenderer.drawThinText(
				"Created by Laszlo Lukacs, " + Version.CURRENT_VERSION_YEAR,
				screen.getCenterX(), 
				screen.getHeight() - 12,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
		textRenderer.drawThinText(Version.CURRENT_VERSION_STRING,
				screen.getWidth(), 
				screen.getHeight(),
				TextAnchor.BOTTOM | TextAnchor.RIGHT);

		layerManager.paint(g, 0, 0);
		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();
		
		if ((keyStates & UP_PRESSED) != 0) {
			selectPreviousButton();
		} else if ((keyStates & DOWN_PRESSED) != 0) {
			selectNextButton();
		}
		
		if((keyStates & FIRE_PRESSED) != 0) {
			onButtonSelected();
		}
		
		inputTimer.setEnabled(keyStates != 0);
		inputTimer.reset();
	}


	public void selectPreviousButton() {
		if (MenuButton.MIN_INDEX < selectedButton.getIndex()) {
			selectButton(selectedButton.getIndex() - 1);
		}
	}

	public void selectNextButton() {
		if (selectedButton.getIndex() < MenuButton.MAX_INDEX) {
			selectButton(selectedButton.getIndex() + 1);
		}
	}

	private void selectButton(final int buttonIndex) {
		if (buttonIndex < MenuButton.MIN_INDEX
				|| buttonIndex > MenuButton.MAX_INDEX) {
			throw new IllegalArgumentException(
					"buttonIndex must have a valid value from MenuButton");
		}

		if (selectedButton != null) {
			selectedButton.setActive(false);
		}

		selectedButton = buttons[buttonIndex - 1];
		selectedButton.setActive(true);
	}
	
	private void onButtonSelected() {
		switch (selectedButton.getIndex()) {
		case MenuButton.START_GAME:
			Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.GAME_SCENE);
			Game.setScene(nextScene);
			break;
		case MenuButton.QUIT:
			Game.quit();
			break;
		default:
			return;
		}
	}
}
