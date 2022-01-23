/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.game.Layer;

public interface SpriteAdapter {
	
	Layer getSprite();
	void setVisible(final boolean visible);
	void setPosition(final int x, final int y);
}
