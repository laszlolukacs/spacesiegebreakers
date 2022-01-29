/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

/**
 * Extends the {@link SpriteAdapter} with animation related methods.
 */
public interface AnimatedSpriteAdapter extends SpriteAdapter {

	void setAnimation(boolean animating);

	void updateAnimation(long delta);
}
