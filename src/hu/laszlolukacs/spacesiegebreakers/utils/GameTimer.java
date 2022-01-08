/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

import hu.laszlolukacs.spacesiegebreakers.Updateable;

public class GameTimer implements Updateable {

	private final long threshold; // in milliseconds
	private long elapsed; // in milliseconds
	private boolean enabled;

	public GameTimer(final long threshold) {
		this.threshold = threshold;
	}

	public GameTimer(final long threshold, final boolean enabled) {
		this(threshold);
		this.enabled = enabled;
	}

	public void update(final long delta) {
		if (enabled) {
			elapsed += delta;
		}
	}

	/**
	 * Returns whether the threshold time has been reached. If the threshold has
	 * been reached, adjusts the elapsed time accordingly.
	 * 
	 * If the timer is not enabled, returns true.
	 * 
	 * @return Whether the threshold time has been reached. If the timer is not
	 *         enabled, returns true.
	 */
	public boolean isThresholdReached() {
		if (!enabled) {
			return true;
		}

		if (elapsed >= threshold) {
			elapsed -= threshold;
			return true;
		}

		return false;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void reset() {
		elapsed = 0;
	}
}
