/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import hu.laszlolukacs.spacesiegebreakers.Drawable;
import hu.laszlolukacs.spacesiegebreakers.Updateable;

/**
 * Defines the contracts for the game scenes.
 */
public interface Scene extends Updateable, Drawable {

	void init();
}
