/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import java.util.Enumeration;
import java.util.Vector;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.drawables.SpriteAdapterFactory;
import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;

public class Turret implements Updateable {

	private static final int TURRET_HALF_SIZE = 5; //px
	
	private final int x, y; // reference coordinates
	private final int rangeSquare = 800; // range^2
	private final long attackRate = 600; // milliseconds
	private GameTimer attackTimer;

	private int attackDamage = 8;
	private int cost = 9;

	public Turret(final int x, final int y) {
		this.x = x + TURRET_HALF_SIZE;
		this.y = y + TURRET_HALF_SIZE;
		attackTimer = new GameTimer(attackRate);
	}

	public void build() {
		SpriteAdapterFactory.createTurretSprite(x - TURRET_HALF_SIZE, y - TURRET_HALF_SIZE);
	}

	public int getCost() {
		return cost;
	}

	public void update(long delta) {
		attackTimer.update(delta);
	}

	public boolean attack(final Vector minions) {
		if (canAttack()) {
			Enumeration e = minions.elements();
			while (e.hasMoreElements()) {
				Minion minion = (Minion) e.nextElement();
				if (minion.isAlive() && isMinionInRange(minion)) {
					minion.takeHit(attackDamage);
					attackTimer.setEnabled(true);
					attackTimer.reset();
					return true;
				}
			}
		}

		return false;
	}

	private boolean isMinionInRange(final Minion minion) {
		int dx = minion.getX() - x;
		int dy = minion.getY() - y;
		return (dx * dx) + (dy * dy) <= rangeSquare;
	}

	private boolean canAttack() {
		return attackTimer.isThresholdReached();
	}
}
