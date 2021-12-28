/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;

public class TurretSprite implements MicroEditionLayerProvider {

	private static final int TURRET_SIZE_PX = 10;

	private Image image;
	Sprite sprite;

	public TurretSprite(final Image image, int x, int y) {
		this.image = image;
		init(x, y);
	}

	private void init(int x, int y) {
		sprite = new Sprite(image, TURRET_SIZE_PX, TURRET_SIZE_PX);
		sprite.setRefPixelPosition(5, 5);
		sprite.defineCollisionRectangle(0, 0, TURRET_SIZE_PX, TURRET_SIZE_PX);
		sprite.setPosition(x, y);
		sprite.setVisible(true);
	}

	public Layer getDrawableLayer() {
		return sprite;
	}
}
