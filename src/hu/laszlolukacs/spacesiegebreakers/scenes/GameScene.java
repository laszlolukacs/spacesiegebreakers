/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.drawables.MazeLayer;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteCache;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseEventHandler;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGame;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefensePlayerActions;
import hu.laszlolukacs.spacesiegebreakers.ui.Hud;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class GameScene extends BaseMicroEditionScene
		implements Scene, TowerDefenseEventHandler {
	public static final String TAG = "GameScene";

	private final Display dgDisplay;
	private final Graphics g;

	private final TowerDefenseGameState gameState = new TowerDefenseGameState();
	private final TowerDefenseGame game = new TowerDefenseGame(gameState, this);

	private Image backgroundImage;
	private Image terrainTilesImage;
	private Image minionSprites;
	private Image minionExplosionFrames;

	private Hud hud;
	private MazeLayer maze;
	private LayerManager layerManager;

	public GameScene(final Display display) {
		super();
		this.dgDisplay = display;
		this.g = super.getGraphics();
		this.hud = new Hud(super.getGraphics(), textRenderer, screen,
				gameState);
		this.layerManager = new LayerManager();
	}

	public void init() {
		Log.i(TAG, "Loading GameScene resources... ");
		try {
			loadImages();
			this.hud.init();
			this.maze = new MazeLayer(this.terrainTilesImage);
			layerManager.append(maze.getDrawableLayer());
			SpriteCache.setLayerManager(layerManager);
			SpriteCache.setImage(minionSprites);
			SpriteCache.setExplosion(minionExplosionFrames);
			if (Log.getEnabled()) {
				Log.i(TAG, "Loading complete.");
			}
			
			game.startNewWave();
		} catch (Exception ex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private void loadImages() {
		Log.i(TAG, "Loading GameScene resources... ");
		try {
			this.backgroundImage = Image.createImage("/legacy/abs_bkg.png");
			this.terrainTilesImage = Image.createImage("/legacy/map_tiles.png");
			this.minionSprites = Image.createImage("/legacy/minions.png");
			this.minionExplosionFrames = Image.createImage("/legacy/fx.png");
			if (Log.getEnabled()) {
				Log.i(TAG, "GameScene image resources loaded.");
			}
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the image resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update(final long delta) {
		this.getInput();
		game.update(delta);
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
		layerManager.paint(g, screen.getGameAreaX(), screen.getGameAreaY());
	}

	// draws the dynamic UI controls
	private void drawUi() {
	}

	public void onDefeat() {
		Scene nextScene = SceneFactory
				.createSceneByKey(SceneFactory.DEFEAT_SCREEN);
		Game.setScene(nextScene);
	}

	public void onVictory() {
		Scene nextScene = SceneFactory
				.createSceneByKey(SceneFactory.VICTORY_SCREEN);
		Game.setScene(nextScene);
	}

	public void onCollision() {
		dgDisplay.vibrate(100);
	}

	public void onLowLives() {
		// TODO: display warning message here
	}

	public void onWaveComplete() {
		// TODO: display message here
	}
}
