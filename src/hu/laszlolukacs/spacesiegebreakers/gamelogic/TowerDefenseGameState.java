/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import java.util.Hashtable;
import java.util.Vector;

import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;

public class TowerDefenseGameState {

	static final int MAX_NUMBER_OF_MINIONS = 20;
	
	long startTime; // milliseconds
	long elapsedTime; // milliseconds
	
	// the player stats
	public int playerCredits = 40;
	public int playerLives = 20;
	public int playerScore = 0;

	Maze maze = new Maze();
	Hashtable turrets = new Hashtable(); // turrets with unique id for collision
	Vector minions = new Vector();

	boolean isWave = false;
	boolean isGameover = false;
	boolean isVictory = false;
	
	int numberOfMinions = 0;
	GameTimer minionSpawnTimer = new GameTimer(TowerDefenseGame.MINION_SPAWN_RATE);
	int numberOfAliveMinions = 0;
	boolean hasMinionCollided = false;

	int currWaveNumber = 0;
	int currWaveMinionHealth;
	int currWaveMinionReward;	
}
