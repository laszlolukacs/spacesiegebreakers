/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.drawables;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.TiledLayer;

public class MazeLayer implements MicroEditionLayerProvider {
	
	private static final int MAZE_WIDTH = 16;
	private static final int MAZE_HEIGHT = 17;
	
	/**
	 * This array indicates which sprite tiles should be used to draw the maze. 
	 */
	private final byte[] terrainTileIndices = new byte[] { // 16x17
			0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 0,
			0, 0, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 0, 0,
			0, 0, 1, 2, 9,10,10,10,10,10,10,11, 1, 2, 0, 0,
			0, 0, 3, 4,16, 0, 0, 0, 0, 0, 0,12, 3, 4, 0, 0,
			0, 0, 1, 2,16, 0, 7, 8, 7, 8, 0,12, 1, 2, 0, 0,
			0, 0,19,18,17, 0, 5, 6, 5, 6, 0,12, 3, 4, 0, 0,
			0, 0, 0, 0, 0, 0, 7, 8, 7, 8, 0,12, 1, 2, 0, 0,
			5, 6, 5, 6, 5, 6, 5, 6, 5, 6, 0,12, 3, 4, 0, 0,
			7, 8, 7, 8, 7, 8, 7, 8, 7, 8, 0,12, 1, 2, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,12, 3, 4, 0, 0,
			0, 0,13,14,14,14,14,14,14,14,14,15, 1, 2, 0, 0,
			0, 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 0,
			0, 0, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 3, 4, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
	
	private final Image terrainTiles;
	private final Layer terrainLayer;
	
	public MazeLayer(final Image terrainTiles) {
		this.terrainTiles = terrainTiles;
		this.terrainLayer = createTiledLayer(this.terrainTiles);
	}

	public Layer getDrawableLayer() {
		return this.terrainLayer;
	}
	
	private Layer createTiledLayer(Image terrainTiles) {
		final int width = MAZE_WIDTH;
		final int height = MAZE_HEIGHT;
		final TiledLayer tiledLayer = new TiledLayer(width, height, terrainTiles, 10, 10);
		for (int i = 0; i < terrainTileIndices.length; i++) {
			int column = i % width;
			int row = (i - column) / (height - 1);
			tiledLayer.setCell(column, row, terrainTileIndices[i]);
		}
		
		return tiledLayer;
	}
}
