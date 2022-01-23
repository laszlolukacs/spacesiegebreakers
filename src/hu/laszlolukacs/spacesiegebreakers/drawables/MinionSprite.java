/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.utils.GameTimer;

public class MinionSprite implements AnimatedSpriteAdapter {

	private static final int MINION_SIZE_PX = 20;

	private Sprite explosionSprite;
	private Sprite minionSprite;

	private final int animationPeriodTime = 33; // ms
	private GameTimer animationTimer = new GameTimer(animationPeriodTime);
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

	public Layer getSprite() {
		return minionSprite;
	}

	public void setVisible(boolean visible) {
		minionSprite.setVisible(visible);
	}

	public void setPosition(int x, int y) {
		minionSprite.setPosition(x, y);
		explosionSprite.setPosition(x, y);
	}
	
	public Layer getAnimatedSprite() {
		return explosionSprite;
	}

	public void setAnimation(boolean animating) {
		explosionSprite.setVisible(animating);
		animationTimer.setEnabled(animating);
	}

	public void updateAnimation(long delta) {
		if (explosionSprite.isVisible() && animationFrameIndex < 5) {
			animationTimer.update(delta);
			if (animationTimer.isThresholdReached()) {
				explosionSprite.nextFrame();
				animationFrameIndex++;
				if (animationFrameIndex == 5) {
					explosionSprite.setVisible(false);
					animationTimer.setEnabled(false);
					animationFrameIndex = 0;
				}

				animationTimer.reset();
			}
		}
	}
}
