/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.utils;

import java.util.Calendar;

public class Log {
	private static boolean isEnabled = true;

	private Log() {
	}

	public static boolean getEnabled() {
		return Log.isEnabled;
	}

	public static void enable(final boolean isEnabled) {
		Log.isEnabled = isEnabled;
	}

	public static void d(final String tag, final String message) {
		if (Log.isEnabled) {
			Log.log("d", tag, message);
		}
	}

	public static void i(final String tag, final String message) {
		if (Log.isEnabled) {
			Log.log("i", tag, message);
		}
	}

	public static void w(final String tag, final String message) {
		if (Log.isEnabled) {
			Log.log("w", tag, message);
		}
	}

	public static void e(final String tag, final String message) {
		if (Log.isEnabled) {
			Log.log("e", tag, message);
		}
	}

	private static void log(final String type, final String tag,
			final String message) {
		if (Log.isEnabled) {
			// there's no StringBuilder in J2ME, so falling back to StringBuffer
			// instead
			StringBuffer buffer = new StringBuffer();
			buffer.append(Calendar.getInstance().getTime().toString() + " - ");
			buffer.append(type + "/ ");
			buffer.append(tag + ": ");
			buffer.append(message);
			System.out.println(buffer.toString());
		}
	}
}
