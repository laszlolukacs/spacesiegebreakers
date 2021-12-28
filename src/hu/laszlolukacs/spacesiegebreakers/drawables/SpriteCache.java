/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;

public class SpriteCache {

	private static LayerManager manager;
	private static Image minionSprites;
	private static Image explosionFrames;

	private SpriteCache() {
	}

	public static void setLayerManager(final LayerManager layerManager) {
		manager = layerManager;
	}

	public static void setImage(final Image image) {
		minionSprites = image;
	}

	public static void setExplosion(final Image image) {
		explosionFrames = image;
	}

	public static SpriteAdapter getSprite(final int waveNumber) {
		if (manager != null 
				&& minionSprites != null
				&& explosionFrames != null) {
			MinionSprite sprite = new MinionSprite(minionSprites,
					explosionFrames, waveNumber);
			manager.append(sprite.getDrawableLayer());
			return sprite;
		} else {
			throw new NullPointerException();
		}
	}
}
