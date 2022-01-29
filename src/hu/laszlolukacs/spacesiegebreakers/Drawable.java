/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers;

/**
 * Defines the contracts for drawing into a frame.
 */
public interface Drawable {

	/**
	 * Allows the implementor to draw into a frame.
	 * 
	 * @param delta
	 *            The elapsed time in milliseconds since the last draw.
	 */
	void render(long delta);
}
