package hu.laszlolukacs.spacesiegebreakers.gamelogic;

public class TowerDefenseGameState {

	// the player stats
	public int playerCredits = 40;
	public int playerLives = 20;
	public int playerScore = 0;

	TurretImpl[] turrets;
	int numTurrets;

	MinionImpl[] minions;
	int numMinions;

	long timeWaveStart;
	long timeSpawnCondition; // minion spawning cooldown time

	int g_GameState;

	boolean isWave;
	boolean isGameover;
	boolean isVictory;
	boolean isThereAnyMinionAlive;

	int currWaveNumber;
	int currWaveMinionHealth;
	int currWaveMinionReward;
}
