/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.DefeatScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.MainMenuScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.SplashScreenScene;
import hu.laszlolukacs.spacesiegebreakers.scenes.frontend.VictoryScene;

public final class SceneFactory {

	public static final int SPLASH_SCREEN = 1;
	public static final int MAIN_MENU = 2;
	public static final int GAME_SCENE = 3;
	public static final int DEFEAT_SCREEN = 4;
	public static final int VICTORY_SCREEN = 5;
	
	private SceneFactory() {
	}

	public static Scene createSceneByKey(int sceneKey) {
		switch (sceneKey) {
		case SPLASH_SCREEN:
			return new SplashScreenScene();
		case MAIN_MENU:
			return new MainMenuScene();
		case GAME_SCENE:
			return new GameScene(Game.display);
		case DEFEAT_SCREEN:
			return new DefeatScene();
		case VICTORY_SCREEN:
			return new VictoryScene();
		default:
			throw new IllegalArgumentException(
					"There is no scene defined for the specified sceneKey");
		}
	}
}
