/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

public interface AnimatedSpriteAdapter extends SpriteAdapter {
	
	void setAnimation(final boolean animating);
	void updateAnimation(long delta);
}
