/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import javax.microedition.lcdui.game.GameCanvas;

import hu.laszlolukacs.spacesiegebreakers.utils.MicroEditionTextRenderer;
import hu.laszlolukacs.spacesiegebreakers.utils.ScreenSize;
import hu.laszlolukacs.spacesiegebreakers.utils.TextRenderer;

/**
 * Abstract base class which provides Java ME related services for {@link Scene}
 * implementations.
 */
public abstract class BaseMicroEditionScene extends GameCanvas
		implements Scene {

	protected final TextRenderer textRenderer;
	protected final ScreenSize screen;

	protected long lastTimeButtonPressed = 0;

	protected BaseMicroEditionScene() {
		super(true);
		textRenderer = new MicroEditionTextRenderer(super.getGraphics());
		screen = new ScreenSize(super.getWidth(), super.getHeight());
	}

	public void init() {
	}

	public void update(final long delta) {
	}

	public void render() {
	}
}
