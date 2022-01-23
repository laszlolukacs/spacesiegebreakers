/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.ui;

import javax.microedition.lcdui.game.Sprite;

/**
 * A {@link Button} used to display the options in the menus.
 */
public class MenuButton implements Button {
	
	public static final int MIN_INDEX = 1;
	public static final int START_GAME = 1;
	public static final int QUIT = 2;
	public static final int MAX_INDEX = 2;

	private final int index;
	private final Sprite sprite;
	private final String description;

	private boolean isActive = false;

	public MenuButton(final int index, 
			final Sprite sprite,
			final String description) {
		this.index = index;
		this.sprite = sprite;
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		if (!this.isActive && isActive) {
			sprite.nextFrame();
		} else if (this.isActive && !isActive) {
			sprite.prevFrame();
		}

		this.isActive = isActive;
	}

	public int getIndex() {
		return index;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getDescription() {
		return description;
	}

}
