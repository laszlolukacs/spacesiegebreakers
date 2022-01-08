/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.drawables.MicroEditionLayerProvider;
import hu.laszlolukacs.spacesiegebreakers.drawables.MinionSprite;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteAdapterFactory;
import hu.laszlolukacs.spacesiegebreakers.utils.Direction2D;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;

public class Minion implements Updateable, DrawableContainer {

	private static final int MINION_START_X = 40;
	private static final int MINION_START_Y = -20;

	private final int maxHitpoints;
	private int hitpoints;

	private final int startX, startY; // pos
	int x, y; // pos

	private static final int MINION_MOVE_PERIOD_TIME = 33; // ms
	private final GameTimer movementTimer = new GameTimer(MINION_MOVE_PERIOD_TIME);
	private int velocity = 1; // px/movePeriodTime
	private int direction = Direction2D.DOWN;

	private MinionSprite drawable;

	private Minion(int x, int y, int hitpoints) {
		this.x = this.startX = x;
		this.y = this.startY = y;
		this.hitpoints = this.maxHitpoints = hitpoints;
		drawable = (MinionSprite) SpriteAdapterFactory.createMinionSprite(1);
	}

	public static Minion spawn(int hitpoints) {
		Minion minion = new Minion(MINION_START_X, MINION_START_Y, hitpoints);
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
			drawable.updateAnimation(delta);
		}
	}

	public boolean isAlive() {
		return hitpoints > 0;
	}

	public void takeHit(int damage) {
		if (!isAlive()) {
			throw new IllegalStateException("The minion is already dead");
		}

		this.hitpoints -= damage;
		if (!isAlive()) {
			drawable.setVisible(false);
			drawable.setAnimation(true);
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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
	}

	public MicroEditionLayerProvider getDrawable() {
		return drawable;
	}
}
