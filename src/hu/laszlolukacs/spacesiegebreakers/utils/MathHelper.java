/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

public final class MathHelper {

	private MathHelper() {
	}

	public static int clamp(final int value, final int min, final int max) {
		int clamped = Math.max(min, value);
		return Math.min(clamped, max);
	}
}
