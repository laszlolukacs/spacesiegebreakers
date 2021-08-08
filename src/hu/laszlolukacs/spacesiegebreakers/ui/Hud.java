package hu.laszlolukacs.spacesiegebreakers.ui;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import hu.laszlolukacs.spacesiegebreakers.Drawable;
import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;
import hu.laszlolukacs.spacesiegebreakers.utils.ScreenSize;
import hu.laszlolukacs.spacesiegebreakers.utils.TextAnchor;
import hu.laszlolukacs.spacesiegebreakers.utils.TextRenderer;

public class Hud implements Drawable, Updateable {

	public static final String TAG = Hud.class.getName();

	private final Graphics g;
	private final TextRenderer textRenderer;
	private final ScreenSize screen;

	private final TowerDefenseGameState gamestate;

	private Image hudTopBackground;
	private Image hudBottomBackground;
	private Image ui_HUDicons;
	private Image ui_HUDcontrols;

	public Hud(final Graphics g, final TextRenderer textRenderer,
			final ScreenSize screen, final TowerDefenseGameState gamestate) {
		this.g = g;
		this.textRenderer = textRenderer;
		this.screen = screen;
		this.gamestate = gamestate;
	}

	public void init() {
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

	public void update(final long delta) {
		// TODO Auto-generated method stub
	}

	public void render(final long delta) {
		renderHudBackground();
		renderHudText(gamestate);
	}

	private void renderHudBackground() {
		g.drawImage(hudTopBackground, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawImage(hudBottomBackground, 0, screen.getHeight(), Graphics.BOTTOM | Graphics.LEFT);
	}

	private void renderHudText(final TowerDefenseGameState gamestate) {
		textRenderer.drawHeaderText(
				String.valueOf(gamestate.playerCredits),
				screen.getWidth() - 38, 
				0, 
				TextAnchor.TOP | TextAnchor.RIGHT);
		textRenderer.drawHeaderText(
				String.valueOf(gamestate.playerLives),
				screen.getWidth() - 2, 
				0, 
				TextAnchor.TOP | TextAnchor.RIGHT);
		textRenderer.drawHeaderText(
				String.valueOf(gamestate.playerScore), 
				24,
				0, 
				TextAnchor.TOP | TextAnchor.LEFT);

		String currentMessage = "";
		textRenderer.drawNormalText(
				currentMessage, 
				screen.getCenterX(),
				screen.getHeight() - 42,
				TextAnchor.BOTTOM | TextAnchor.HCENTER);
	}

}
