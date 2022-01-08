/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;

public class SpriteAdapterFactory {

	private static LayerManager manager;
	private static Image minionSprites;
	private static Image explosionFrames;

	private SpriteAdapterFactory() {
	}

	public static void setLayerManager(final LayerManager layerManager) {
		manager = layerManager;
	}

	public static void setMinionImage(final Image image) {
		minionSprites = image;
	}

	public static void setExplosionImage(final Image image) {
		explosionFrames = image;
	}

	public static SpriteAdapter createMinionSprite(final int waveNumber) {
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
