/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.drawables.MazeLayer;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteAdapterFactory;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseEventHandler;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGame;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.ui.GameControlButton;
import hu.laszlolukacs.spacesiegebreakers.ui.Hud;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class GameScene extends BaseMicroEditionScene
		implements Scene, TowerDefenseEventHandler {
	public static final String TAG = "GameScene";

	private static final long INPUT_DEBOUNCE_TIME = 66; // ms => 2 frames

	private final Display dgDisplay;
	private final Graphics g;

	private final TowerDefenseGameState gameState = new TowerDefenseGameState();
	private final TowerDefenseGame game = new TowerDefenseGame(gameState, this);

	private Image backgroundImage;
	private Image terrainTilesImage;
	private Image minionSprites;
	private Image minionExplosionFrames;
	private Image turretPlaceholderImage;

	private Hud hud;
	private MazeLayer maze;
	private LayerManager layerManager;
	private Sprite turretPlaceholderSprite;

	private GameTimer inputTimer = new GameTimer(INPUT_DEBOUNCE_TIME);
	private boolean isBuildMode = false;

	public GameScene(final Display display) {
		super();
		this.dgDisplay = display;
		this.g = super.getGraphics();
		this.layerManager = new LayerManager();
		this.hud = new Hud(super.getGraphics(), screen, textRenderer,
				gameState);
	}

	public void init() {
		Log.i(TAG, "Loading GameScene resources... ");
		try {
			loadImages();
			this.hud.init();
			this.maze = new MazeLayer(this.terrainTilesImage);
			SpriteAdapterFactory.setLayerManager(layerManager);
			SpriteAdapterFactory.setMinionImage(minionSprites);
			SpriteAdapterFactory.setExplosionImage(minionExplosionFrames);
			createTurretPlaceholderSprite();
			layerManager.append(maze.getDrawableLayer());
			if (Log.getEnabled()) {
				Log.i(TAG, "Loading complete.");
			}
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
			this.turretPlaceholderImage = Image
					.createImage("/legacy/placeholder.png");
			if (Log.getEnabled()) {
				Log.i(TAG, "Image resources have been loaded.");
			}
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the image resource, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	private void createTurretPlaceholderSprite() {
		turretPlaceholderSprite = new Sprite(turretPlaceholderImage, 10, 10);
		turretPlaceholderSprite.setVisible(false);
		// turretPlaceholderSprite.defineReferencePixel(5, 5);
		layerManager.append(turretPlaceholderSprite);
	}

	public void update(final long delta) {
		getInput(delta);
		game.update(delta);
		hud.update(delta);
	}

	private void getInput(final long delta) {
		inputTimer.update(delta);
		if (inputTimer.isThresholdReached()) {
			int keyStates = super.getKeyStates();

			if ((keyStates & LEFT_PRESSED) != 0) {
				onLeftPressed();
			} else if ((keyStates & UP_PRESSED) != 0) {
				onUpPressed();
			} else if ((keyStates & RIGHT_PRESSED) != 0) {
				onRightPressed();
			} else if ((keyStates & DOWN_PRESSED) != 0) {
				onDownPressed();
			} else if ((keyStates & FIRE_PRESSED) != 0) {
				onFirePressed();
			}

			inputTimer.setEnabled(keyStates != 0);
			inputTimer.reset();
		}
	}

	private void onLeftPressed() {
		if (!isBuildMode) {
			hud.selectPreviousControlButton();
		} else if (turretPlaceholderSprite.getX() > 0) {
			moveTurretPlaceholderSprite(-5, 0);
		}
	}

	private void onUpPressed() {
		if (isBuildMode && turretPlaceholderSprite.getY() > 0) {
			moveTurretPlaceholderSprite(0, -5);
		}
	}

	private void onRightPressed() {
		if (!isBuildMode) {
			hud.selectNextControlButton();
		} else if (turretPlaceholderSprite.getX() < (160 - 10)) {
			moveTurretPlaceholderSprite(5, 0);
		}
	}

	private void onDownPressed() {
		if (isBuildMode && turretPlaceholderSprite.getY() < (170 - 10)) {
			moveTurretPlaceholderSprite(0, 5);
		}
	}

	private void moveTurretPlaceholderSprite(int deltaX, int deltaY) {
		turretPlaceholderSprite.setPosition(
				turretPlaceholderSprite.getX() + deltaX,
				turretPlaceholderSprite.getY() + deltaY);
	}

	private void onFirePressed() {
		if (!isBuildMode) {
			switch (hud.getCurrentButtonIndex()) {
			case GameControlButton.BUILD_TURRET:
				enterBuildMode();
				break;
			case GameControlButton.NEW_WAVE:
				game.startNewWave();
				break;
			case GameControlButton.QUIT:
				quitGame();
				break;
			default:
				break;
			}
		} else {
			buildTurret();
		}
	}

	private void enterBuildMode() {
		isBuildMode = true;
		turretPlaceholderSprite.setVisible(isBuildMode);
	}

	private void buildTurret() {
		isBuildMode = false;
		turretPlaceholderSprite.setVisible(isBuildMode);
		// TODO: build a turret
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

	private void quitGame() {
		Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.MAIN_MENU);
		Game.setScene(nextScene);
	}
}
