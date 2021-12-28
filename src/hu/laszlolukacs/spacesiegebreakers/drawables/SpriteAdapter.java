/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

public interface SpriteAdapter {
	
	boolean isActive();
	void setActive(final boolean active);
	void setVisible(final boolean visible);
	void setPosition(final int x, final int y);
}
