/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;

import hu.laszlolukacs.spacesiegebreakers.gamelogic.MazeImpl;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.ui.Hud;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class GameScene extends BaseMicroEditionScene implements Scene {
	public static final String TAG = "GameScene";

	private final Graphics g;

	private Image backgroundImage;
	private Hud hud;
	private MazeImpl maze;
	private LayerManager layerManagerGameTheater;

	public GameScene(final Display display) {
		super();
		this.g = super.getGraphics();
		this.hud = new Hud(getGraphics(), textRenderer, screen, new TowerDefenseGameState());
		this.layerManagerGameTheater = new LayerManager();
	}

	public void init() {
		Log.i(TAG, "Loading GameScene resources... ");
		try {
			this.backgroundImage = Image.createImage("/legacy/abs_bkg.png");
			this.hud.init();
			Image terrainTiles = Image.createImage("/legacy/map_tiles.png");
			this.maze = new MazeImpl(terrainTiles);
			layerManagerGameTheater.append(maze.getDrawableLayer());
			if (Log.getEnabled()) {
				Log.i(TAG, "Loading complete.");
			}
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update(final long delta) {
		this.getInput();
	}
	
	private void getInput() {
		int keyStates = super.getKeyStates();
	}

	public void render(final long delta) {
		drawBackground();
		drawHud(delta);
		renderGameFrame();
		drawUi();

		super.flushGraphics();
	}

	// draws the starscape background
	private void drawBackground() {
		g.drawImage(backgroundImage, screen.getCenterX(), screen.getCenterY(),
				Graphics.VCENTER | Graphics.HCENTER);
	}

	// draws the static HUD
	private void drawHud(final long delta) {
		this.hud.render(delta);
	}

	// draws the game scene
	private void renderGameFrame() {
		layerManagerGameTheater.paint(g, screen.getGameAreaX(), screen.getGameAreaY());
	}

	// draws the dynamic UI controls
	private void drawUi() {
	}
}
