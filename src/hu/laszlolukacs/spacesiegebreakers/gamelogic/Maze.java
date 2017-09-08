package hu.laszlolukacs.spacesiegebreakers.gamelogic;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;

public interface Maze {
	void setTerrainTileIndices(final byte[] terrainTileIndices);
	void setTerrainTileImage(Image terrainTiles);
	void setTerrainHeights(final byte[] terrainHeights);
	byte getHeightAt(final int x, final int y);
	Layer getDrawableLayer();
}
