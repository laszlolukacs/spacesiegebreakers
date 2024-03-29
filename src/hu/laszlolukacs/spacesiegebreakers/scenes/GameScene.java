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
import hu.laszlolukacs.spacesiegebreakers.drawables.MazeLayerFactory;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteAdapterFactory;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseEventHandler;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGame;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.TowerDefenseGameState;
import hu.laszlolukacs.spacesiegebreakers.gamelogic.Turret;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.DefeatScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.VictoryScene;
import hu.laszlolukacs.spacesiegebreakers.ui.GameControlButton;
import hu.laszlolukacs.spacesiegebreakers.ui.Hud;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

/**
 * The {@link Scene} which handles the actual game.
 */
public class GameScene extends BaseMicroEditionScene implements Scene, TowerDefenseEventHandler {
	public static final String TAG = "GameScene";

	private static final long INPUT_DEBOUNCE_TIME = 66; // ms => 2 frames
	private static final long FIRE_BUTTON_DEBOUNCE_TIME = 166; // ms => ~5 frames

	private final Display dgDisplay;
	private final TowerDefenseGameState gameState = new TowerDefenseGameState();
	private final TowerDefenseGame game = new TowerDefenseGame(gameState, this);

	private Image backgroundImage;
	private Image terrainTilesImage;
	private Image minionSprites;
	private Image minionExplosionFrames;
	private Image turretSprite;
	private Image turretPlaceholderImage;

	private Hud hud;
	private LayerManager layerManager;
	private Sprite turretPlaceholderSprite;

	private GameTimer inputTimer = new GameTimer(INPUT_DEBOUNCE_TIME);
	private GameTimer fireButtonTimer = new GameTimer(FIRE_BUTTON_DEBOUNCE_TIME);
	private boolean isBuildMode = false;

	public GameScene(final Display display) {
		super();
		this.dgDisplay = display;
		this.layerManager = new LayerManager();
		this.hud = new Hud(super.getGraphics(), screen, textRenderer, gameState);
	}

	public void init() {
		Log.i(TAG, "Loading GameScene resources... ");
		try {
			loadImages();
			this.hud.init();
			SpriteAdapterFactory.setLayerManager(layerManager);
			SpriteAdapterFactory.setMinionImage(minionSprites);
			SpriteAdapterFactory.setExplosionImage(minionExplosionFrames);
			SpriteAdapterFactory.setTurretImage(turretSprite);
			createTurretPlaceholderSprite();
			SpriteAdapterFactory.setMazeLayer(MazeLayerFactory.createTiledLayer(this.terrainTilesImage));
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
			this.turretSprite = Image.createImage("/legacy/turret.png");
			this.turretPlaceholderImage = Image.createImage("/legacy/placeholder.png");
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
		layerManager.append(turretPlaceholderSprite);
	}

	public void update(final long delta) {
		getInput(delta);
		game.update(delta);
		hud.update(delta);
	}

	private void getInput(final long delta) {
		inputTimer.update(delta);
		fireButtonTimer.update(delta);
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
			}

			if (fireButtonTimer.isThresholdReached()) {
				if ((keyStates & FIRE_PRESSED) != 0) {
					onFirePressed();
				}

				fireButtonTimer.setEnabled(keyStates != 0);
				fireButtonTimer.reset();
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
		hud.setMessage("Pick a location");
	}

	private void buildTurret() {
		game.buildTurret(turretPlaceholderSprite.getX(), turretPlaceholderSprite.getY());
		isBuildMode = false;
		turretPlaceholderSprite.setVisible(isBuildMode);
	}

	public void render(final long delta) {
		drawBackground();
		renderGameFrame();
		drawHud(delta);

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

	public void onDefeat() {
		Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.DEFEAT_SCREEN);
		((DefeatScene) nextScene).setScore(gameState.playerScore);
		Game.setScene(nextScene);
	}

	public void onVictory() {
		Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.VICTORY_SCREEN);
		((VictoryScene) nextScene).setScore(gameState.playerScore);
		Game.setScene(nextScene);
	}

	public void onCollision() {
		dgDisplay.vibrate(333);
	}

	public void onLowLives() {
		hud.setMessage("WARNING! Defeat imminent");
	}

	public void onWaveComplete() {
		hud.setMessage("Wave completed!");
	}

	private void quitGame() {
		Scene nextScene = SceneFactory.createSceneByKey(SceneFactory.MAIN_MENU);
		Game.setScene(nextScene);
	}

	public void onTurretPlacementFailed(int reason) {
		switch(reason) {
		case Turret.BUILD_FAIL_REASON_NOT_ENOUGH_RESOURCES:
			hud.setMessage("Not enough credits.");
			break;
		case Turret.BUILD_FAIL_REASON_INVALID_TERRAIN:
			hud.setMessage("You must build on the platform.");
			break;
		case Turret.BUILD_FAIL_REASON_TURRET_COLLISION:
		default:
			hud.setMessage("You can't place the turret there.");
		}
	}
}
