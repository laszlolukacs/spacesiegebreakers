/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import java.util.Hashtable;
import java.util.Vector;

public class TowerDefenseGameState {

	// the player stats
	public int playerCredits = 40;
	public int playerLives = 20;
	public int playerScore = 0;

	Hashtable turrets = new Hashtable(); // turrets with unique id for collision
	Vector minions = new Vector();

	boolean isWave = false;
	boolean isGameover = false;
	boolean isVictory = false;
	
	int numberOfMinions = 0;
	long minionSpawnCooldown = 0; // milliseconds
	
	boolean hasMinionCollided = false;

	int currWaveNumber = 0;
	int currWaveMinionHealth;
	int currWaveMinionReward;	
}
