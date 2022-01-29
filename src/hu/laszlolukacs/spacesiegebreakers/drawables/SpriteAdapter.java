/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.game.Layer;

/**
 * Defines a wrapper for interfacing with the Java ME {@link javax.microedition.lcdui.game.Sprite}.
 */
public interface SpriteAdapter {

	Layer getSprite();

	void setVisible(boolean visible);

	void setPosition(int x, int y);
}
