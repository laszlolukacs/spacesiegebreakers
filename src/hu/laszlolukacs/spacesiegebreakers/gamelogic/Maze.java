/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import hu.laszlolukacs.spacesiegebreakers.utils.MathHelper;

public class Maze {

	private static final int MAZE_WIDTH = 16;
	private static final int MAZE_HEIGHT = 17;
	
	/**
	 * This array indicates where could be the turrets and the minions located within the maze.
	 */
	private final byte[] terrainHeight = new byte[] { // 16x17
			0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
			0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
			0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
			0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
			0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
			0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
			0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0,
			0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
			0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		
	public byte getHeightAt(final int x, final int y) {
		// limits+validates the input
		int validX = MathHelper.clamp(convertScreenToMazeCoordinate(x), 0, MAZE_WIDTH - 1);
		int validY = MathHelper.clamp(convertScreenToMazeCoordinate(y), 0, MAZE_HEIGHT - 1);
		return this.terrainHeight[validY * MAZE_WIDTH + validX];
	}
	
	private int convertScreenToMazeCoordinate(final int coord) {
		if (coord % 10 != 0) {
			throw new IllegalArgumentException("input coordinate must a multiple of 10");
		}

		int result = coord;
		result /= 10;
		return result;
	}
}
