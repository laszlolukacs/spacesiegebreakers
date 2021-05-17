package hu.laszlolukacs.spacesiegebreakers.scenes;

import hu.laszlolukacs.spacesiegebreakers.Game;

public final class SceneFactory {
	
	public static Scene createSceneByKey(int sceneKey) {
		switch(sceneKey) {
		case 1:
			return new SplashScreenScene();
		case 2:
			return new MainMenuScene();
		case 3:
			return new GameScene(Game.display);
		case 4:
			return new DefeatScene();
		case 5:
			return new VictoryScene();
		default:
			throw new IllegalArgumentException("There is no scene defined for the specified sceneKey");
		}		
	}
}
