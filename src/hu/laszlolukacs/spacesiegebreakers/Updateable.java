/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers;

public interface Updateable {

	/**
	 * Allows the implementor to gather input and update its state.
	 * 
	 * @param delta
	 *            The elapsed time in milliseconds since the last update.
	 */
	void update(long delta);
}
