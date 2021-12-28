/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

public interface TowerDefenseEventHandler {

	void onCollision();
	void onLowLives();
	void onDefeat();
	void onWaveComplete();
	void onVictory();
}
