/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import java.util.Enumeration;
import java.util.Vector;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class TowerDefenseGame implements Updateable, TowerDefensePlayerActions {

	public static final String TAG = "TowerDefenseGame";

	static final long MINION_SPAWN_RATE = 500; // milliseconds

	private final TowerDefenseEventHandler eventHandler;
	private TowerDefenseGameState gameState;

	public TowerDefenseGame(final TowerDefenseGameState gameState,
			final TowerDefenseEventHandler eventHandler) {
		if (gameState == null) {
			throw new NullPointerException("gameState cannot be null");
		}

		this.gameState = gameState;
		this.eventHandler = eventHandler;
	}

	public void update(final long delta) {
		if (canTick()) {
			gameState.elapsedTime += delta;
			gameState.minionSpawnTimer.update(delta);
			if (canSpawnMinion()) {
				spawnMinion();
			}

			updateMinions(delta);
			if (!isPlayerAlive()) {
				// Game Over
				onDefeat();
			} else if (gameState.hasMinionCollided
					&& gameState.playerLives <= 5) {
				// Defeat is imminent
				onLowLives();
			}

			fireTurrets(delta);
			handleMinionHits();
			if (!isAnyMinionAlive()) {
				onWaveComplete();
			}
		}
	}

	public void startNewWave() {
		if (!gameState.isWave) {
			if (gameState.startTime == 0) {
				gameState.startTime = System.currentTimeMillis();
			}

			cleanUpDeadMinions();
			gameState.numberOfMinions = 0;
			gameState.numberOfAliveMinions = calculateNumberOfAliveMinions();
			gameState.currWaveNumber++;
			gameState.currWaveMinionHealth = (15 * gameState.currWaveNumber);
			gameState.currWaveMinionReward = 1;
			gameState.isWave = true;
		}
	}

	public boolean buildTurret(final int x, final int y) {
		if (canBuildTurretAt(x, y)) {
			Turret turret = new Turret(x, y);
			if (gameState.playerCredits >= turret.getCost()) {
				gameState.turrets.put(calculateTurretKey(x, y), turret);
				gameState.playerCredits -= turret.getCost();
				gameState.playerScore += turret.getCost();
				turret.build();
				Log.i(TAG, "Turret placed @ " + x + ";" + y);
				return true;
			} else {
				Log.i(TAG, "Unable to place turret --- Not enough credits; required: " + turret.getCost() + ", available: " + gameState.playerCredits);
			}
		}

		return false;
	}

	private boolean canTick() {
		return !gameState.isGameover 
				&& !gameState.isVictory
				&& gameState.isWave;
	}

	private void cleanUpDeadMinions() {
		Vector minions = gameState.minions;
		boolean[] deadMinionIndices = new boolean[minions.size()];

		for (int i = 0; i < minions.size(); i++) {
			Minion minion = (Minion) minions.elementAt(i);
			deadMinionIndices[i] = !minion.isAlive();
			if (!minion.isAlive()) {
				minion.close();
			}
		}

		for (int i = (minions.size() - 1); i >= 0; i--) {
			if (deadMinionIndices[i]) {
				minions.removeElementAt(i);
			}
		}
	}

	private void spawnMinion() {
		Log.i(TAG,
				"Spawning minion (Level " + gameState.currWaveNumber + ", "
						+ gameState.currWaveMinionHealth + " hp, "
						+ (gameState.numberOfMinions + 1) + "/"
						+ TowerDefenseGameState.MAX_NUMBER_OF_MINIONS + ")");
		Minion minion = Minion.spawn(gameState.currWaveNumber, gameState.currWaveMinionHealth);
		gameState.minions.addElement(minion);
		gameState.numberOfMinions++;
		gameState.numberOfAliveMinions++;
		gameState.minionSpawnTimer.reset();
		boolean spawnTimerEnabled = gameState.numberOfMinions != TowerDefenseGameState.MAX_NUMBER_OF_MINIONS;
		gameState.minionSpawnTimer.setEnabled(spawnTimerEnabled);
	}

	private boolean canSpawnMinion() {
		return (gameState.numberOfMinions < TowerDefenseGameState.MAX_NUMBER_OF_MINIONS
				&& gameState.minionSpawnTimer.isThresholdReached());
	}

	private void updateMinions(final long delta) {
		Enumeration e = gameState.minions.elements();
		while (e.hasMoreElements()) {
			Minion minion = (Minion) e.nextElement();
			minion.update(delta);
			if (hasMinionCollided(minion)) {
				onMinionCollision();
			}
		}
	}

	private boolean hasMinionCollided(final Minion minion) {
		if (!minion.hasCollided() && minion.getX() == 80
				&& minion.getY() == -20) {
			minion.setCollided();
			return true;
		}

		return false;
	}

	private void onMinionCollision() {
		gameState.playerLives--;
		gameState.hasMinionCollided = true;
		Log.i(TAG,
				"Minion collided! Remaining lives: " + gameState.playerLives);
		if (eventHandler != null) {
			eventHandler.onCollision();
		}
	}

	private boolean isPlayerAlive() {
		return gameState.playerLives > 0;
	}

	private void onDefeat() {
		gameState.isWave = false;
		gameState.isGameover = true;
		Log.i(TAG, "*********");
		Log.i(TAG, "GAME OVER, wave no. " + gameState.currWaveNumber
				+ ", scored " + gameState.playerScore + " points");
		Log.i(TAG, "*********");
		if (eventHandler != null) {
			eventHandler.onDefeat();
		}
	}

	private void onLowLives() {
		Log.i(TAG,
				"Defeat imminent, remaining lives: " + gameState.playerLives);
		if (eventHandler != null) {
			eventHandler.onLowLives();
		}

		gameState.hasMinionCollided = false;
	}

	private void fireTurrets(final long delta) {
		Enumeration e = gameState.turrets.elements();
		while (e.hasMoreElements()) {
			Turret turret = (Turret) e.nextElement();
			turret.update(delta);
			turret.attack(gameState.minions);
		}
	}

	private void handleMinionHits() {
		int numberOfAliveMinions = calculateNumberOfAliveMinions();
		if (numberOfAliveMinions != gameState.numberOfAliveMinions) {
			for (int i = 0; i < (gameState.numberOfAliveMinions - numberOfAliveMinions); i++) {
				onMinionDeath();
			}

			gameState.numberOfAliveMinions = numberOfAliveMinions;
		}
	}

	private int calculateNumberOfAliveMinions() {
		int result = 0;
		Enumeration e = gameState.minions.elements();
		while (e.hasMoreElements()) {
			Minion minion = (Minion) e.nextElement();
			if (minion.isAlive()) {
				result++;
			}
		}

		return result;
	}

	private void onMinionDeath() {
		gameState.playerCredits += gameState.currWaveMinionReward;
		gameState.playerScore += gameState.currWaveMinionHealth;
	}

	private boolean isAnyMinionAlive() {
		return gameState.numberOfAliveMinions > 0;
	}

	private void onWaveComplete() {
		gameState.isWave = false;
		Log.i(TAG, "Wave no. " + gameState.currWaveNumber + " completed");
		if (gameState.currWaveNumber == 10) {
			gameState.isVictory = true;
			if (eventHandler != null) {
				eventHandler.onVictory();
			}
		} else {
			if (eventHandler != null) {
				eventHandler.onWaveComplete();
			}
		}
		
		cleanUpDeadMinions();
	}

	private boolean canBuildTurretAt(final int x, final int y) {
		boolean isLocationSuitable = isTurretLocationSuitableAt(x, y);
		if (!isLocationSuitable) {
			Log.i(TAG, "Unable to place turret --- Terrain is unsuitable @ " + x
					+ ";" + y);
		}

		boolean isLocationFree = isTurretLocationFreeAt(x, y);
		if (!isLocationFree) {
			Log.i(TAG, "Unable to place turret --- Destination is blocked @ "
					+ x + ";" + y);
		}

		return isLocationSuitable && isLocationFree;
	}

	private boolean isTurretLocationSuitableAt(final int x, final int y) {
		int x10Remainder = x % 10;
		int y10Remainder = y % 10;
		if ((x10Remainder != 0 && x10Remainder != 5)
				|| (y10Remainder != 0 && y10Remainder != 5)) {
			throw new IllegalArgumentException(
					"input coordinates must be a multiple of 5");
		}

		if (x10Remainder == 0 && y10Remainder == 0) {
			return gameState.maze.getHeightAt(x, y) == 1;
		} else if (x10Remainder == 0) {
			return gameState.maze.getHeightAt(x, y - 5) == 1
					&& gameState.maze.getHeightAt(x, y + 5) == 1;
		} else if (y10Remainder == 0) {
			return gameState.maze.getHeightAt(x - 5, y) == 1
					&& gameState.maze.getHeightAt(x + 5, y) == 1;
		} else {
			return gameState.maze.getHeightAt(x - 5, y - 5) == 1
					&& gameState.maze.getHeightAt(x - 5, y + 5) == 1
					&& gameState.maze.getHeightAt(x + 5, y - 5) == 1
					&& gameState.maze.getHeightAt(x + 5, y + 5) == 1;
		}
	}

	private boolean isTurretLocationFreeAt(final int x, final int y) {
		return !gameState.turrets.containsKey(calculateTurretKey(x, y))
				&& !gameState.turrets.containsKey(calculateTurretKey(x, y - 5))
				&& !gameState.turrets.containsKey(calculateTurretKey(x, y + 5))
				&& !gameState.turrets.containsKey(calculateTurretKey(x - 5, y))
				&& !gameState.turrets.containsKey(calculateTurretKey(x - 5, y - 5))
				&& !gameState.turrets.containsKey(calculateTurretKey(x - 5, y + 5))
				&& !gameState.turrets.containsKey(calculateTurretKey(x + 5, y))
				&& !gameState.turrets.containsKey(calculateTurretKey(x + 5, y - 5))
				&& !gameState.turrets.containsKey(calculateTurretKey(x + 5, y + 5));
	}
	
	private Integer calculateTurretKey(final int x, final int y) {
		return new Integer((y / 5) * 32 + (x / 5));
	}
}
