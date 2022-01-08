/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.ui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.Drawable;
import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.utils.Direction2D;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;
import hu.laszlolukacs.spacesiegebreakers.utils.ScreenSize;
import hu.laszlolukacs.spacesiegebreakers.utils.TextAnchor;
import hu.laszlolukacs.spacesiegebreakers.utils.TextRenderer;

public class Hud implements Drawable, Updateable, GameControlButtonSelector {

	public static final String TAG = Hud.class.getName();

	private static final int HUD_ICON_SIZE_PX = 16;

	private final Graphics g;
	private final TextRenderer textRenderer;
	private final ScreenSize screen;
	
	// an independent fullscreen LayerManager for the HUD
	private final LayerManager layerManager;

	private final TowerDefenseGameState gamestate;

	private Image hudTopBackground;
	private Image hudBottomBackground;
	private Image ui_HUDicons;
	private Image ui_HUDcontrols;

	private final Hashtable gameControlButtons = new Hashtable();
	private GameControlButton activeControlButton;

	private boolean areGameControlButtonsMoving = false;
	private GameTimer gameControlButtonsTimer = new GameTimer(GameControlButton.GAME_CONTROL_BUTTON_MOVE_PERIOD_TIME);
	private int gameControlButtonsMovementDirection = Direction2D.LEFT;
	private int gameControlButtonsTargetPosition = 0;
	private int gameControlButtonsActualPosition = 0;

	public Hud(final Graphics g, 
			final ScreenSize screen,
			final TextRenderer textRenderer,
			final TowerDefenseGameState gamestate) {
		this.g = g;
		this.screen = screen;
		this.textRenderer = textRenderer;
		this.layerManager = new LayerManager();
		this.gamestate = gamestate;
	}

	public void init() {
		loadImages();
		createSprites();
		activateGameControl(GameControlButton.BUILD_TURRET);
	}

	private void loadImages() {
		try {
			hudTopBackground = Image.createImage("/legacy/ui_header.png");
			hudBottomBackground = Image.createImage("/legacy/ui_tray.png");
			ui_HUDicons = Image.createImage("/legacy/ui_icons.png");
			ui_HUDcontrols = Image.createImage("/legacy/ui_controls.png");
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	private void createSprites() {
		createHudIcon(2, 2);
		createHudIcon(screen.getWidth() - 36 - 56, 0);
		createHudIcon(screen.getWidth() - 36, 1);
		createGameControlButton(GameControlButton.BUILD_TURRET);
		createGameControlButton(GameControlButton.NEW_WAVE);
		createGameControlButton(GameControlButton.QUIT);
	}

	private void createHudIcon(final int xCoord, final int frameIndex) {
		final int hudIconYCoord = 2;
		Sprite hudIcon = new Sprite(ui_HUDicons, HUD_ICON_SIZE_PX, HUD_ICON_SIZE_PX);
		hudIcon.setFrame(frameIndex);
		hudIcon.setPosition(xCoord, hudIconYCoord);
		layerManager.append(hudIcon);
	}

	private void createGameControlButton(final int gameControlButtonIndex) {
		Integer index = new Integer(gameControlButtonIndex);
		GameControlButton button = GameControlButton.build(index, ui_HUDcontrols, screen);
		gameControlButtons.put(index, button);
		layerManager.append(button.getSprite());
	}
	
	public int getCurrentButtonIndex() {
		if (activeControlButton != null) {
			return activeControlButton.getIndex();
		}

		return 0;
	}

	public void selectPreviousControlButton() {
		if (GameControlButton.MIN_INDEX < activeControlButton.getIndex()) {
			activateGameControl(activeControlButton.getIndex() - 1);
			gameControlButtonsTargetPosition += 32;
			gameControlButtonsMovementDirection = Direction2D.LEFT;
			areGameControlButtonsMoving = true;
			gameControlButtonsTimer.setEnabled(true);
		}
	}

	public void selectNextControlButton() {
		if (activeControlButton.getIndex() < GameControlButton.MAX_INDEX) {
			activateGameControl(activeControlButton.getIndex() + 1);
			gameControlButtonsTargetPosition -= 32;
			gameControlButtonsMovementDirection = Direction2D.RIGHT;
			areGameControlButtonsMoving = true;
			gameControlButtonsTimer.setEnabled(true);
		}
	}

	private void activateGameControl(final int gameControlIndex) {
		if (gameControlIndex < GameControlButton.MIN_INDEX
				|| gameControlIndex > GameControlButton.MAX_INDEX) {
			throw new IllegalArgumentException(
					"gameControlIndex must have a valid value from GameControls");
		}

		if (activeControlButton != null) {
			activeControlButton.setActive(false);
		}
		
		Integer index = new Integer(gameControlIndex);
		activeControlButton = (GameControlButton) gameControlButtons.get(index);
		activeControlButton.setActive(true);
	}

	public void update(final long delta) {
		if (areGameControlButtonsMoving) {
			updateGameControls(delta);
		}
	}

	private void updateGameControls(final long delta) {
		gameControlButtonsTimer.update(delta);
		if (gameControlButtonsTimer.isThresholdReached()
				&& gameControlButtonsActualPosition != gameControlButtonsTargetPosition) {
			int diffX = (gameControlButtonsMovementDirection == Direction2D.LEFT)
					? GameControlButton.GAME_CONTROL_BUTTON_VELOCITY
					: (GameControlButton.GAME_CONTROL_BUTTON_VELOCITY * -1);
			Enumeration e = gameControlButtons.elements();
			while (e.hasMoreElements()) {
				GameControlButton button = (GameControlButton) e.nextElement();
				Sprite sprite = button.getSprite();
				sprite.setPosition(sprite.getX() + diffX, sprite.getY());
			}

			gameControlButtonsActualPosition += diffX;
			if (gameControlButtonsActualPosition == gameControlButtonsTargetPosition) {
				areGameControlButtonsMoving = false;
				gameControlButtonsTimer.setEnabled(false);
				gameControlButtonsTimer.reset();
			}
		}
	}

	public void render(final long delta) {
		renderHudBackground();
		layerManager.paint(g, 0, 0);
		renderHudText(gamestate);
		renderUiText();
		renderMessageText("");
	}

	private void renderHudBackground() {
		g.drawImage(hudTopBackground, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawImage(hudBottomBackground, 0, screen.getHeight(),
				Graphics.BOTTOM | Graphics.LEFT);
	}

	private void renderHudText(final TowerDefenseGameState gamestate) {
		textRenderer.drawHeaderText(String.valueOf(gamestate.playerCredits),
				screen.getWidth() - 38, 0, TextAnchor.TOP | TextAnchor.RIGHT);
		textRenderer.drawHeaderText(String.valueOf(gamestate.playerLives),
				screen.getWidth() - 2, 0, TextAnchor.TOP | TextAnchor.RIGHT);
		textRenderer.drawHeaderText(String.valueOf(gamestate.playerScore), 24,
				0, TextAnchor.TOP | TextAnchor.LEFT);
	}

	private void renderUiText() {
		if (activeControlButton != null) {
			textRenderer.drawNormalText(activeControlButton.getDescription(), 
					screen.getCenterX(),
					screen.getHeight() - 42,
					TextAnchor.BOTTOM | TextAnchor.HCENTER);
		}
	}

	private void renderMessageText(final String message) {
		textRenderer.drawNormalText(message, screen.getCenterX(),
				screen.getHeight() - 66,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
	}
}
