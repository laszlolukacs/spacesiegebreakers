package hu.laszlolukacs.spacesiegebreakers.scenes;

/**
 * 
 * 
 * @author laszlolukacs
 */
public interface Scene {
	void init();
	
	/**
	 * Allows the scene to gather input and update the game state.
	 */
	void update();
	
	/**
	 * Draws a frame based on the current scene state.
	 */
	void render();
}
