/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import java.util.Enumeration;
import java.util.Vector;

import hu.laszlolukacs.spacesiegebreakers.Updateable;
import hu.laszlolukacs.spacesiegebreakers.utils.Position2D;

public class Turret implements Updateable {

	private final Position2D position;
	private final int rangeSquare = 400;
	private final long attackRate = 600; // milliseconds
	private long attackCooldownCounter = 0; // milliseconds
	
	private int attackDamage = 8;
	int cost = 9;
	
	public Turret(final Position2D position) {
		this.position = position;
	}
	
	public Position2D getPosition() {
		return position;
	}

	public void update(long delta) {
		if (attackCooldownCounter > 0) {
			attackCooldownCounter -= delta;
		}
	}

	public boolean attack(final Vector minions) {
		if (canAttack()) {
	        Enumeration e = minions.elements();
	        while (e.hasMoreElements()) {
	        	Minion minion = (Minion) e.nextElement();
	        	
				if (minion.isAlive() && isMinionInRange(minion)) {
					minion.takeHit(attackDamage);
					attackCooldownCounter = attackRate;
					return true;
				}
	        }
		}

		return false;
	}

	private boolean isMinionInRange(final Minion minion) {
		int dx = minion.x - position.x;
		int dy = minion.y - position.y;
		return (dx * dx) + (dy * dy) <= rangeSquare;
	}

	private boolean canAttack() {
		return attackCooldownCounter <= 0;
	}
}
