/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;

public class TurretSprite implements SpriteAdapter {

	private static final int TURRET_SIZE_PX = 10;

	private final Sprite sprite;

	public TurretSprite(final Image image, final int x, final int y) {
		sprite = new Sprite(image, TURRET_SIZE_PX, TURRET_SIZE_PX);
		sprite.setPosition(x, y);
		sprite.defineCollisionRectangle(0, 0, TURRET_SIZE_PX, TURRET_SIZE_PX);
		sprite.setVisible(true);
	}

	public Layer getSprite() {
		return sprite;
	}

	public void setVisible(boolean visible) {
		sprite.setVisible(visible);
	}

	public void setPosition(int x, int y) {
		sprite.setPosition(x, y);
	}
}
