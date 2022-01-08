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

	private static final int MAX_NUMBER_OF_MINIONS = 20;
	static final long MINION_SPAWN_RATE = 500; // milliseconds

	private final TowerDefenseEventHandler eventHandler;
	private TowerDefenseGameState gameState;

	public TowerDefenseGame(final TowerDefenseGameState gameState, final TowerDefenseEventHandler eventHandler) {
		if (gameState == null) {
			throw new NullPointerException("gameState cannot be null");
		}

		this.gameState = gameState;
		this.eventHandler = eventHandler;
	}

	public void update(long delta) {
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
					&& gameState.playerLives < 6) {
				// Defeat is imminent
				onLowLives();
			}

			this.fireTurrets(delta);
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
			gameState.currWaveNumber++;
			gameState.currWaveMinionHealth = (15 * gameState.currWaveNumber);
			gameState.currWaveMinionReward = 1;
			gameState.isWave = true;
		}
	}

	public void buildTurret(int x, int y) {
		// TODO Auto-generated method stub

	}

	private boolean canTick() {
		return !gameState.isGameover && !gameState.isVictory
				&& gameState.isWave;
	}

	private void cleanUpDeadMinions() {
		Vector minions = gameState.minions;
		boolean[] deadMinionIndices = new boolean[minions.size()];

		for (int i = 0; i < minions.size(); i++) {
			Minion minion = (Minion) minions.elementAt(i);
			deadMinionIndices[i] = !minion.isAlive();
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
						+ gameState.numberOfMinions + "/"
						+ MAX_NUMBER_OF_MINIONS + ")");
		Minion minion = Minion.spawn(gameState.currWaveMinionHealth);
		gameState.minions.addElement(minion);
		gameState.numberOfMinions++;
		gameState.minionSpawnTimer.reset();
		boolean spawnTimerEnabled = gameState.numberOfMinions != MAX_NUMBER_OF_MINIONS;
		gameState.minionSpawnTimer.setEnabled(spawnTimerEnabled);
	}

	private boolean canSpawnMinion() {
		return (gameState.numberOfMinions < MAX_NUMBER_OF_MINIONS
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
		return minion.x == 80 && minion.y == -20;
	}

	private void onMinionCollision() {
		Log.i(TAG, "Minion collided!");
		gameState.playerLives--;
		gameState.hasMinionCollided = true;
		if (this.eventHandler != null) {
			this.eventHandler.onCollision();
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

	private boolean isAnyMinionAlive() {
		Enumeration e = gameState.minions.elements();
		while (e.hasMoreElements()) {
			Minion minion = (Minion) e.nextElement();
			if (minion.isAlive()) {
				return true;
			}
		}

		return false;
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
	}
}
