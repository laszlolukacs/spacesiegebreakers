package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.TiledLayer;

public class MazeImpl implements Maze {
	private static final int MAZE_WIDTH = 16;
	private static final int MAZE_HEIGHT = 17;
	
	/**
	 * This array indicates which sprite tiles should be used to draw the maze. 
	 */
	private byte[] terrainTileIndices = new byte[] { // 16x17
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
	
	/**
	 * This array indicates where could be the turrets and the minions located within the maze.
	 */
	private byte[] terrainHeight = new byte[] { // 16x17
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
	
	private Image terrainTiles = null;
	private Layer terrainLayer = null;

	public void setTerrainTileIndices(byte[] terrainTileIndices) {
		if(terrainTileIndices.length != MazeImpl.MAZE_WIDTH * MazeImpl.MAZE_HEIGHT) {
			throw new IllegalStateException("`terrainTileIndices.lenght` must match the required maze length.");
		}
		
		this.terrainTileIndices = terrainTileIndices;
		this.terrainLayer = this.createTiledLayer(MazeImpl.MAZE_WIDTH, MazeImpl.MAZE_HEIGHT, this.terrainTiles, this.terrainTileIndices);
	}
	
	public void setTerrainTileImage(Image terrainTiles) {
		this.terrainTiles = terrainTiles;
	}

	public void setTerrainHeights(byte[] terrainHeights) {
		this.terrainHeight = terrainHeights;
	}
	
	public byte getHeightAt(int x, int y) {
		// limits+validates the input
		x = Math.max(0, x);
		x = Math.min(x, MazeImpl.MAZE_WIDTH - 1);
		
		y = Math.max(0,  y);
		y = Math.min(y, MazeImpl.MAZE_HEIGHT - 1);
		
		return this.terrainHeight[y * MazeImpl.MAZE_WIDTH + x];
	}

	public Layer getDrawableLayer() {
		if(this.terrainLayer == null) {
			this.terrainLayer = this.createTiledLayer(MazeImpl.MAZE_WIDTH, MazeImpl.MAZE_HEIGHT, this.terrainTiles, this.terrainTileIndices);
		}
		
		return this.terrainLayer;
	}
	
	private Layer createTiledLayer(int width, int height, Image terrainTiles, byte[] terrainTileIndices) {
		Layer tiledLayer = new TiledLayer(width, height, terrainTiles, 10, 10);
		for (int i = 0; i < terrainTileIndices.length; i++) {
			int column = i % width;
			int row = (i - column) / (height - 1);
			((TiledLayer) tiledLayer).setCell(column, row, terrainTileIndices[i]);
		}
		
		return tiledLayer;
	}
}