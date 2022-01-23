/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.LayerManager;

public class SpriteAdapterFactory {

	private static LayerManager manager;
	private static Image minionSprites;
	private static Image explosionFrames;
	private static Image turretImage;
	private static Layer mazeLayer;

	private SpriteAdapterFactory() {
	}

	public static void setLayerManager(final LayerManager layerManager) {
		manager = layerManager;
	}
	
	public static void setMazeLayer(final Layer layer) {
		mazeLayer = layer;
		manager.append(mazeLayer);
	}

	public static void setMinionImage(final Image image) {
		minionSprites = image;
	}

	public static void setExplosionImage(final Image image) {
		explosionFrames = image;
	}
	
	public static void setTurretImage(final Image image) {
		turretImage = image;
	}

	public static SpriteAdapter createMinionSprite(final int waveNumber) {
		if (manager != null 
				&& minionSprites != null
				&& explosionFrames != null) {
			MinionSprite sprite = new MinionSprite(minionSprites,
					explosionFrames, 
					waveNumber);
			manager.remove(mazeLayer);
			manager.append(sprite.getSprite());
			manager.append(sprite.getAnimatedSprite());
			manager.append(mazeLayer);
			return sprite;
		} else {
			throw new NullPointerException();
		}
	}
	
	public static SpriteAdapter createTurretSprite(final int x, final int y) {
		if (manager != null	&& turretImage != null) {
			TurretSprite sprite = new TurretSprite(turretImage, x, y);
			manager.remove(mazeLayer);
			manager.append(sprite.getSprite());
			manager.append(mazeLayer);
			return sprite;
		} else {
			throw new NullPointerException();
		}
	}
}
