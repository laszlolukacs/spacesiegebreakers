/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.drawables.MicroEditionLayerProvider;
import hu.laszlolukacs.spacesiegebreakers.drawables.MinionSprite;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteCache;

public class Minion implements Updateable, DrawableContainer {

	private static final int MINION_START_X = 40;
	private static final int MINION_START_Y = -20;

	int hitpoints, maxHitpoints = 10;

	int x, y, startx, starty; // pos
	int velocity = 1; // px/movePeriodTime
	int direction = Direction.DOWN;
	int movePeriodTime = 33; // ms
	int currentPeriodTime = 0; // ms

	private MinionSprite drawable;

	private Minion() {
	}

	private Minion(int x, int y, int hitpoints) {
		this.x = startx = x;
		this.y = starty = y;
		this.hitpoints = maxHitpoints = hitpoints;
		drawable = (MinionSprite) SpriteCache.getSprite(1);
	}

	public static Minion spawn(int hitpoints) {
		return new Minion(MINION_START_X, MINION_START_Y, hitpoints);
	}

	public void update(long delta) {
		if (isAlive()) {
			currentPeriodTime += delta;
			direction = changeOrientation();
			move();
			drawable.setPosition(x, y);
		} else {
			drawable.updateAnimation(delta);
		}
	}

	public boolean isAlive() {
		return hitpoints > 0;
	}

	public void hit(int damage) {
		if (!isAlive()) {
			throw new IllegalStateException("The minion is already dead");
		}

		this.hitpoints -= damage;
		if (!isAlive()) {
			drawable.setVisible(false);
			drawable.setAnimation(true);
		}
	}

	void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	private boolean canMove() {
		if (currentPeriodTime - movePeriodTime > 0) {
			currentPeriodTime -= movePeriodTime;
			return true;
		}

		return false;
	}

	private void move() {
		if (canMove()) {
			switch (direction) {
			case Direction.LEFT:
				x -= velocity;
				break;
			case Direction.RIGHT:
				x += velocity;
				break;
			case Direction.UP:
				y -= velocity;
				break;
			case Direction.DOWN:
				y += velocity;
				break;
			}
		}
	}

	static final class Direction {
		public static final int LEFT = 1;
		public static final int RIGHT = 2;
		public static final int UP = 4;
		public static final int DOWN = 8;
	}

	private int changeOrientation() {
		switch (x) {
		case 0:
			switch (y) {
			case 0:
				return Direction.DOWN;
			case 70:
				return Direction.RIGHT;
			case 110:
				return Direction.DOWN;
			case 150:
				return Direction.RIGHT;
			}

			break;
		case 40:
			switch (y) {
			case -20:
				return Direction.DOWN;
			case 0:
				return Direction.LEFT;
			case 40:
				return Direction.RIGHT;
			case 70:
				return Direction.UP;
			}

			break;
		case 80:
			switch (y) {
			case 0:
				return Direction.UP;
			case -20:
				// minion has collided with the objective
				respawn();
				return Direction.DOWN;
			}

			break;
		case 100:
			switch (y) {
			case 40:
				return Direction.DOWN;
			case 110:
				return Direction.LEFT;
			}

			break;
		case 140:
			switch (y) {
			case 0:
				return Direction.LEFT;
			case 150:
				return Direction.UP;
			}

			break;
		}

		return direction;
	}

	/**
	 * Teleports the minion to its starting point.
	 */
	private void respawn() {
		x = startx;
		y = starty;
	}

	public MicroEditionLayerProvider getDrawable() {
		return drawable;
	}
}
