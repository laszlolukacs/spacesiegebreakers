/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;

public class MinionSprite
		implements MicroEditionLayerProvider, AnimatedSpriteAdapter {

	private static final int MINION_SIZE_PX = 20;

	private boolean isActive = false;

	private Sprite explosionSprite;
	private Sprite minionSprite;

	private final int animationPeriodTime = 33; // ms
	private int currentPeriodTime = 0; // ms
	private int animationFrameIndex = 0;

	public MinionSprite(final Image minionSprites, 
			final Image explosionFrames,
			final int waveNumber) {
		buildMinionSprite(minionSprites, waveNumber);
		buildExplosionSprite(explosionFrames);
	}

	private void buildMinionSprite(final Image minionSpritesImage,
			final int waveNumber) {
		minionSprite = new Sprite(minionSpritesImage, MINION_SIZE_PX, MINION_SIZE_PX);
		
		// workaround for the 5 available sprites for the 10 waves
		int spriteIndex = (waveNumber - 1 < 5) 
				? waveNumber - 1
				: waveNumber - 6;
		minionSprite.setFrame(spriteIndex);

		minionSprite.defineReferencePixel(10, 10);
		// 2px transparent padding around sprite
		minionSprite.defineCollisionRectangle(2, 2, 16, 16);
	}

	private void buildExplosionSprite(final Image explosionFramesImage) {
		explosionSprite = new Sprite(explosionFramesImage, MINION_SIZE_PX, MINION_SIZE_PX);
		explosionSprite.defineReferencePixel(10, 10);
		explosionSprite.setVisible(false);
	}

	public Layer getDrawableLayer() {
		return minionSprite;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		this.isActive = active;
	}

	public void setVisible(boolean visible) {
		minionSprite.setVisible(visible);
	}

	public void setPosition(int x, int y) {
		minionSprite.setPosition(x, y);
	}

	public void setAnimation(boolean animating) {
		explosionSprite.setVisible(animating);
	}

	public void updateAnimation(long delta) {
		if (explosionSprite.isVisible() && animationFrameIndex < 5) {
			currentPeriodTime += delta;
			if (currentPeriodTime > animationPeriodTime) {
				explosionSprite.nextFrame();
				animationFrameIndex++;
				if (animationFrameIndex == 5) {
					explosionSprite.setVisible(false);
					animationFrameIndex = 0;
				}

				currentPeriodTime = 0;
			}
		}
	}
}
