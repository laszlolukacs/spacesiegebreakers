/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class MicroEditionTextRenderer implements TextRenderer {

	private final Font headerFont = Font.getFont(Font.FACE_PROPORTIONAL,
			Font.STYLE_BOLD, Font.SIZE_LARGE);
	private final Font normalFont = Font.getFont(Font.FACE_PROPORTIONAL,
			Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	private final Font thinFont = Font.getFont(Font.FACE_PROPORTIONAL,
			Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	private final Font monospacedFont = Font.getFont(Font.FACE_MONOSPACE,
			Font.STYLE_BOLD, Font.SIZE_LARGE);

	private final Graphics graphics;

	public MicroEditionTextRenderer(Graphics graphics) {
		this.graphics = graphics;
	}

	public void drawHeaderText(String text, int x, int y) {
		drawHeaderText(text, x, y, TextAnchor.TOP | TextAnchor.LEFT);
	}

	public void drawHeaderText(String text, int x, int y, int anchor) {
		drawText(headerFont, text, x, y, anchor);
	}

	public void drawNormalText(String text, int x, int y) {
		drawNormalText(text, x, y, TextAnchor.TOP | TextAnchor.LEFT);
	}

	public void drawNormalText(String text, int x, int y, int anchor) {
		drawText(normalFont, text, x, y, anchor);
	}

	public void drawThinText(String text, int x, int y) {
		drawThinText(text, x, y, TextAnchor.TOP | TextAnchor.LEFT);
	}

	public void drawThinText(String text, int x, int y, int anchor) {
		drawText(thinFont, text, x, y, anchor);
	}

	public void drawMonospaced(String text, int x, int y, int anchor) {
		drawText(monospacedFont, text, x, y, anchor);
	}

	private void drawText(final Font font, 
			final String text, 
			final int x,
			final int y, 
			final int anchor) {
		graphics.setColor(255, 255, 255);
		graphics.setFont(font);
		graphics.drawString(text, x, y, anchor);
	}
}
