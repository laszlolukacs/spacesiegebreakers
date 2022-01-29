/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.drawables.MinionSprite;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteAdapterFactory;
import hu.laszlolukacs.spacesiegebreakers.utils.Direction2D;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;

public class Minion implements Updateable {

	private static final int MINION_START_X = 40;
	private static final int MINION_START_Y = -20;
	private static final int MINION_MOVE_PERIOD_TIME = 33; // ms

	private final int maxHitpoints;
	private int hitpoints;

	private final int startX;
	private final int startY;
	private int x;
	private int y;

	private final GameTimer movementTimer = new GameTimer(MINION_MOVE_PERIOD_TIME);
	private int velocity = 1; // px/movePeriodTime
	private int direction = Direction2D.DOWN;
	
	private boolean hasCollided = false;

	private MinionSprite drawable;

	private Minion(int x, int y, int hitpoints, int waveNumber) {
		this.x = this.startX = x;
		this.y = this.startY = y;
		this.hitpoints = this.maxHitpoints = hitpoints;
		drawable = (MinionSprite) SpriteAdapterFactory.createMinionSprite(waveNumber);
	}

	public static Minion spawn(int waveNumber, int hitpoints) {
		Minion minion = new Minion(MINION_START_X, MINION_START_Y, hitpoints, waveNumber);
		minion.movementTimer.setEnabled(true);
		return minion;
	}

	public void update(long delta) {
		if (isAlive()) {
			movementTimer.update(delta);
			if (movementTimer.isThresholdReached()) {
				direction = changeOrientation();
				move();
			}

			drawable.setPosition(x, y);
		} else {
			// updates the destruction animation
			drawable.updateAnimation(delta);
		}
	}

	public boolean isAlive() {
		return hitpoints > 0;
	}

	public void takeHit(int damage) {
		if (!isAlive()) {
			throw new IllegalStateException("This minion has already been destroyed.");
		}

		hitpoints -= damage;
		if (!isAlive()) {
			onDestruction();
		}
	}
	
	public int getMaxHitpoints() {
		return maxHitpoints;
	}
	
	public int getHitpoints() {
		return hitpoints;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public boolean hasCollided() {
		return hasCollided;
	}
	
	public void setCollided() {
		hasCollided = true;
	}
	
	public void close() {
		drawable.setVisible(false);
		drawable.setAnimation(false);
		drawable = null;
	}

	private void move() {
		switch (direction) {
		case Direction2D.LEFT:
			x -= velocity;
			break;
		case Direction2D.RIGHT:
			x += velocity;
			break;
		case Direction2D.UP:
			y -= velocity;
			break;
		case Direction2D.DOWN:
			y += velocity;
			break;
		default:
			throw new IllegalStateException("Minion.direction has an invalid value.");
		}
	}

	private int changeOrientation() {
		switch (x) {
		case 0:
			switch (y) {
			case 0:
				return Direction2D.DOWN;
			case 70:
				return Direction2D.RIGHT;
			case 110:
				return Direction2D.DOWN;
			case 150:
				return Direction2D.RIGHT;
			}
			
			break;
		case 40:
			switch (y) {
			case -20:
				return Direction2D.DOWN;
			case 0:
				return Direction2D.LEFT;
			case 40:
				return Direction2D.RIGHT;
			case 70:
				return Direction2D.UP;
			}

			break;
		case 80:
			switch (y) {
			case 0:
				return Direction2D.UP;
			case -20:
				// minion has collided with the objective
				respawn();
				return Direction2D.DOWN;
			}

			break;
		case 100:
			switch (y) {
			case 40:
				return Direction2D.DOWN;
			case 110:
				return Direction2D.LEFT;
			}

			break;
		case 140:
			switch (y) {
			case 0:
				return Direction2D.LEFT;
			case 150:
				return Direction2D.UP;
			}

			break;
		}

		return direction;
	}

	/**
	 * Teleports the minion to its starting point.
	 */
	private void respawn() {
		x = startX;
		y = startY;
		hasCollided = false;
	}
	
	private void onDestruction() {
		drawable.setVisible(false);
		drawable.setAnimation(true);
		movementTimer.setEnabled(false);
		movementTimer.reset();
	}
}
