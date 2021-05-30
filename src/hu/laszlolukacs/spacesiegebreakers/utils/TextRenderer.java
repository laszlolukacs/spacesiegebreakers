/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

public interface TextRenderer {
	
	public void drawHeaderText(final String text, int x, int y);

	public void drawHeaderText(final String text, int x, int y, int anchor);
		
	public void drawNormalText(final String text, int x, int y);
	
	public void drawNormalText(final String text, int x, int y, int anchor);
	
	public void drawThinText(final String text, int x, int y);
	
	public void drawThinText(final String text, int x, int y, int anchor);
	
	public void drawMonospaced(final String text, int x, int y, int anchor);
}
