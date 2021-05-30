/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

public class ScreenSize {

	private final int width;
	private final int height;
	private final int centerX;
	private final int centerY;
	private final int areaLeft;
	private final int areaTop;

	public ScreenSize(int width, int height) {
		this.width = width;
		this.height = height;
		centerX = this.width / 2;
		centerY = this.height / 2;
		areaLeft = (this.width - 160) / 2;
		areaTop = (this.height - 180) / 2;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public int getGameAreaX() {
		return areaLeft;
	}

	public int getGameAreaY() {
		return areaTop;
	}
}
