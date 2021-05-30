/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers;

public interface Drawable {

	/**
	 * Allows the implementor to draw into a frame.
	 * 
	 * @param delta
	 *            The elapsed time in milliseconds since the last draw.
	 */
	void render(long delta);
}
