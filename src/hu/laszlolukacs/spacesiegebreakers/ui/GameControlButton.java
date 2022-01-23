/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.ui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.utils.ScreenSize;

public class GameControlButton {

	public static final int MIN_INDEX = 1;
	public static final int BUILD_TURRET = 1;
	public static final int NEW_WAVE = 2;
	public static final int QUIT = 3;
	public static final int MAX_INDEX = 3;
	
	static final int GAME_CONTROL_BUTTON_VELOCITY = 4; // px/movePeriodTime
	static final int GAME_CONTROL_BUTTON_MOVE_PERIOD_TIME = 33; // ms

	private static final int UI_CONTROL_SIZE_PX = 32;

	private static final String BUILD_TURRET_BUTTON_DESCRIPTION = "Place new turret";
	private static final String NEW_WAVE_BUTTON_DESCRIPTION = "Call for a wave";
	private static final String QUIT_BUTTON_DESCRIPTION = "Retreat!";

	private final Integer index;
	private final Sprite sprite;
	private final String description;

	private boolean isActive = false;

	private GameControlButton(final Integer index, 
			final Sprite sprite,
			final String description) {
		this.index = index;
		this.sprite = sprite;
		this.description = description;
	}

	public static GameControlButton build(final Integer index,
			final Image buttonFramesImage, 
			final ScreenSize screen) {
		Sprite buttonSprite = new Sprite(buttonFramesImage, UI_CONTROL_SIZE_PX,	UI_CONTROL_SIZE_PX);
		int spriteXCoord = screen.getCenterX();
		int spriteYCoord = screen.getHeight() - 32;
		String description = "";
		switch (index.intValue()) {
		case BUILD_TURRET:
			buttonSprite.setPosition(spriteXCoord - 16, spriteYCoord);
			buttonSprite.setFrame(0);
			description = BUILD_TURRET_BUTTON_DESCRIPTION;
			break;
		case NEW_WAVE:
			buttonSprite.setPosition(spriteXCoord + 18, spriteYCoord);
			buttonSprite.setFrame(2);
			description = NEW_WAVE_BUTTON_DESCRIPTION;
			break;
		case QUIT:
			buttonSprite.setPosition(spriteXCoord + 52, spriteYCoord);
			buttonSprite.setFrame(4);
			description = QUIT_BUTTON_DESCRIPTION;
			break;
		default:
			throw new IllegalArgumentException("index must have a valid value from GameControls");
		}

		return new GameControlButton(index, buttonSprite, description);
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
		return index.intValue();
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getDescription() {
		return description;
	}
}
