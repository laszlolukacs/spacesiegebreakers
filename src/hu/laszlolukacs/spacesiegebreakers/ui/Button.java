/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.ui;

import javax.microedition.lcdui.game.Sprite;

/**
 * Defines the contracts for a button control.
 */
public interface Button {
	
	boolean isActive();

	void setActive(boolean isActive);

	int getIndex();

	Sprite getSprite();

	String getDescription();
}
