package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class GameScene extends GameCanvas implements Scene {
	public static final String TAG = "GameScene";

	private final Display display;
	private Graphics g;

	private long m_timeButtonLastPressed = 0;

	private int m_screenWidth, m_screenHeight, m_centerHorizontal,
			m_centerVertical, m_cornerX, m_cornerY;
	
	private int[] num_DeathFXFrameCounter;

	// game state descriptors
	private volatile boolean isRunning;
	private volatile boolean isWave;
	private volatile boolean isGameover;
	private boolean isVictory;
	private boolean isThereAnyMinionAlive;
	private int g_GameState = 0;
	private int currWaveNumber;
	private int currWaveMinionHealth;
	private int currWaveMinionReward;
	private int playerCredits;
	private int playerLives;
	private int playerScore;
	private long timeWaveStart;
	
	// game element descriptors
	// defining map, turrets' damage, minons' health, minions move direction
	private int[] g_turretDmg, g_minionHth, moveDirection; // the direction of the minions movement, left: 1, right: 2, up: 3, down: 4
	private int g_turretNum, g_minionNum; 	// number of turrets, minions
	private long timeSpawnCondition; 	// minion spawning cooldown time
	private long[] timeTurretLastShoot;	// turret cooldown time
	
	// graphics assets
	private LayerManager layMan_Game;
	private LayerManager layMan_UI;
	private TiledLayer tiled_Map;
	private Sprite spr_Placeholder;
	private Sprite[] spr_FX = new Sprite[20];
	private Sprite[] spr_Minions = new Sprite[20];
	private Sprite[] spr_Turrets = new Sprite[108];
	private Sprite[] spr_UI_icons = new Sprite[3];
	private Sprite[] spr_UI_menu = new Sprite[2];
	private Sprite[] spr_UI_controls = new Sprite[3];

	private Image img_Background;
	private Image img_MinionDeathFX;
	private Image img_Map;
	private Image img_Minion;
	private Image img_Placeholder;
	private Image img_Turret;
	private Image ui_Header;
	private Image ui_HUDicons;
	private Image ui_HUDcontrols;
	private Image ui_Tray;

	// UI descriptors
	private boolean isUIControlsMoving;
	private boolean isUIControlsMovingToLeft;
	private int ui_CurrentMessageIndex;
	private int ui_ControlSelectedIndex;
	private int ui_SliderCounter;
	private String sz_Credits, sz_Lives, sz_Score;
	private String[] sz_CurrentInfo;
	
	private int[] g_map = new int[] { 	
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

	public GameScene(final Display display) {
		super(true);
		this.display = display;
		this.g = super.getGraphics();
		this.m_screenWidth = super.getWidth(); // width of the LCD
		this.m_screenHeight = super.getHeight(); // height of the LCD
		this.m_centerHorizontal = m_screenWidth / 2; // center of the LCD
		this.m_centerVertical = m_screenHeight / 2;
		this.m_cornerX = (m_screenWidth - 160) / 2; // corner of the Game Canvas
		this.m_cornerY = (m_screenHeight - 180) / 2;
	}

	public void init() {
		// TODO Auto-generated method stub
		Log.i(TAG, "Loading scene resources... ");
		try {
			this.img_Background = Image.createImage("/legacy/abs_bkg.png");
			this.img_MinionDeathFX = Image.createImage("/legacy/fx.png");
			this.img_Map = Image.createImage("/legacy/map_tiles.png");
			this.img_Minion = Image.createImage("/legacy/minions.png");
			this.img_Placeholder = Image.createImage("/legacy/placeholder.png");
			this.img_Turret = Image.createImage("/legacy/turret.png");

			this.ui_Header = Image.createImage("/legacy/ui_header.png");
			this.ui_HUDicons = Image.createImage("/legacy/ui_icons.png");
			this.ui_HUDcontrols = Image.createImage("/legacy/ui_controls.png");
			this.ui_Tray = Image.createImage("/legacy/ui_tray.png");

			// creates sprites and map
			Log.i(TAG, "Creating sprites...");
			// creates the layer manager instance
			this.layMan_Game = new LayerManager();

			// the turret place picker square
			this.spr_Placeholder = new Sprite(img_Placeholder, 10, 10);
			this.spr_Placeholder.defineReferencePixel(5, 5);
			this.spr_Placeholder.setVisible(false);
			this.layMan_Game.append(spr_Placeholder);

			// creates minion death effect animation sprites
			this.spr_FX = new Sprite[20];
			for (int i = 0; i < 20; i++) {
				this.spr_FX[i] = new Sprite(this.img_MinionDeathFX, 20, 20);
				this.spr_FX[i].defineReferencePixel(10, 10);
				this.spr_FX[i].setVisible(false);
				this.layMan_Game.append(spr_FX[i]);
			}

			// creates turret sprites
			this.spr_Turrets = new Sprite[108];
			for (int i = 0; i < 108; i++) {
				this.spr_Turrets[i] = new Sprite(this.img_Turret, 10, 10);
				this.spr_Turrets[i].setVisible(false);
				this.layMan_Game.append(this.spr_Turrets[i]);
			}

			// creates map from the int array
			this.tiled_Map = new TiledLayer(16, 17, this.img_Map, 10, 10);
			for (int i = 0; i < g_map.length; i++) {
				int column = i % 16;
				int row = (i - column) / 16;
				this.tiled_Map.setCell(column, row, g_map[i]);
			}
			this.layMan_Game.append(this.tiled_Map);

			// sets up the in-game user interface (HUD)
			this.layMan_UI = new LayerManager();

			// creates the status icons
			this.spr_UI_icons = new Sprite[3];
			for (int i = 0; i < 3; i++) {
				this.spr_UI_icons[i] = new Sprite(this.ui_HUDicons, 16, 16);
				this.layMan_UI.append(this.spr_UI_icons[i]);
			}
			// places and sets the status icons
			this.spr_UI_icons[1].setPosition(this.m_screenWidth - 36, 2);
			this.spr_UI_icons[0].setPosition(this.spr_UI_icons[1].getX() - 56,
					2);
			this.spr_UI_icons[2].setPosition(2, 2);
			this.spr_UI_icons[1].nextFrame();
			this.spr_UI_icons[2].nextFrame();
			this.spr_UI_icons[2].nextFrame();

			// creates control icons
			this.spr_UI_controls = new Sprite[3];
			for (int i = 0; i < 3; i++) {
				this.spr_UI_controls[i] = new Sprite(this.ui_HUDcontrols, 32,
						32);
				this.layMan_UI.append(this.spr_UI_controls[i]);
			}
			// places and sets the control icons
			this.spr_UI_controls[0].setPosition(m_centerHorizontal - 16,
					m_screenHeight - 32);
			this.spr_UI_controls[1].setPosition(m_centerHorizontal + 18,
					m_screenHeight - 32);
			this.spr_UI_controls[2].setPosition(m_centerHorizontal + 52,
					m_screenHeight - 32);
			this.spr_UI_controls[0].nextFrame();
			this.spr_UI_controls[1].nextFrame();
			this.spr_UI_controls[1].nextFrame();
			for (int i = 0; i < 4; i++) {
				this.spr_UI_controls[2].nextFrame();
			}
			
			// game state descriptors
			isRunning = false;
			isGameover = false;
			isVictory = false;
			g_GameState = 0;
			isWave = false;
			isUIControlsMoving = false;
			isUIControlsMovingToLeft = false;
			isThereAnyMinionAlive = true;
			timeWaveStart = 0;
			currWaveMinionHealth = 10;
			currWaveMinionReward = 1;
			currWaveNumber = 0;
			
			num_DeathFXFrameCounter = new int[20];
			sz_Credits = new String("40");
			sz_Lives = new String("20");
			sz_Score = new String("0");
			sz_CurrentInfo = new String[7];
			sz_CurrentInfo[0] = new String("Place new turret");
			sz_CurrentInfo[1] = new String("Call for a wave");
			sz_CurrentInfo[2] = new String("Retreat!");
			sz_CurrentInfo[3] = new String("Pick a location");
			sz_CurrentInfo[4] = new String("Not enough credits.");
			sz_CurrentInfo[5] = new String("Wave completed!");
			sz_CurrentInfo[6] = new String("WARNING! Defeat imminent");
			
			if(Log.getEnabled()) {			
				Log.i(TAG, "Loading complete."); 
			}
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update() {
		// TODO Auto-generated method stub
		this.getInput();
				
		if(isUIControlsMoving){
			ui_SliderCounter++;
			if(isUIControlsMovingToLeft && (spr_UI_controls[0].getX() > 32)){
				for(int i = 0; i < 3; i++){
					spr_UI_controls[i].setPosition(spr_UI_controls[i].getX() - 4, spr_UI_controls[i].getY());
				}
			}
			else if(!isUIControlsMovingToLeft && (spr_UI_controls[2].getX() < this.m_screenWidth - 32)){
				for(int i = 0; i < 3; i++){
					spr_UI_controls[i].setPosition(spr_UI_controls[i].getX() + 4, spr_UI_controls[i].getY());
				}
			}
			if(ui_SliderCounter == 8){
				isUIControlsMoving = false;
				ui_SliderCounter = 0;
			}
		}
		if(!isGameover){
			timeSpawnCondition = (System.currentTimeMillis() - timeWaveStart) / 500;
			if(timeSpawnCondition > 20)
				timeSpawnCondition = 20;
			for(int i = 0; i < 20; i++){
				if(spr_FX[i].isVisible() && num_DeathFXFrameCounter[i] < 5){
					spr_FX[i].nextFrame();
					num_DeathFXFrameCounter[i]++;
				}
				else{
					spr_FX[i].setVisible(false);
				}
			}
			if(isWave){
//				if(checkPath()){
//					this.display.vibrate(166);
//					playerLives--;
//				}
				if(playerLives <= 0){
					isWave = false;
					isGameover = true;
					g_GameState = 19;
				}
				else if(playerLives < 6){
					ui_CurrentMessageIndex = 6;
				}

				if(g_turretNum != 0){
//					rangeCheck();
				}
				if(isThereAnyMinionAlive == false){
					isWave = false;
					System.out.println("INFORMATION: Wave no. " + currWaveNumber + " completed!");
					if(currWaveNumber == 10){
						isWave = false;
						isVictory = true;
						isGameover = false;
						g_GameState = 19;
					}
					else{
						// TODO
						ui_CurrentMessageIndex = 5;
					}
				}
				Integer tmp = new Integer(playerCredits);
				sz_Credits = tmp.toString();
				tmp = new Integer(playerLives);
				sz_Lives = tmp.toString();
				tmp = new Integer(playerScore);
				sz_Score = tmp.toString();
				
			}
		}
		// helps drawing the info on the HUD
		Integer tmp = new Integer(playerCredits);
		sz_Credits = tmp.toString();
		tmp = new Integer(playerScore);
		sz_Score = tmp.toString();
	}

	public void render() {
		// draws the starscape background
		this.g.drawImage(img_Background, m_centerHorizontal, m_centerVertical,
				Graphics.VCENTER | Graphics.HCENTER);
		// draws the HUD
		g.drawImage(ui_Header, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawImage(ui_Tray, 0, m_screenHeight, Graphics.BOTTOM | Graphics.LEFT);
		this.g.setColor(255, 255, 255);
		this.g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD,
				Font.SIZE_LARGE));
		g.drawString(sz_Credits, m_screenWidth - 38, 0,
				Graphics.TOP | Graphics.RIGHT);
		g.drawString(sz_Lives, m_screenWidth - 2, 0,
				Graphics.TOP | Graphics.RIGHT);
		g.drawString(sz_Score, 24, 0, Graphics.TOP | Graphics.LEFT);
		this.g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
				Font.SIZE_MEDIUM));
		g.drawString(sz_CurrentInfo[ui_CurrentMessageIndex], m_centerHorizontal,
				m_screenHeight - 42, Graphics.BOTTOM | Graphics.HCENTER);
		// draws the map and the entities
		layMan_Game.paint(g, m_cornerX, m_cornerY);
		// draws the graphics elements of the UI
		layMan_UI.paint(g, 0, 0);
		
		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();		
		
		switch(this.g_GameState){
		// base state for a running game, listening for main controls
		case 0:
			if(System.currentTimeMillis() - m_timeButtonLastPressed > 166) {
				if (((keyStates & LEFT_PRESSED)		 != 0) && (ui_ControlSelectedIndex > 0)) {
					m_timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_controls[ui_ControlSelectedIndex].prevFrame();
					ui_ControlSelectedIndex--;
					ui_CurrentMessageIndex = ui_ControlSelectedIndex;
					isUIControlsMoving = true;
					isUIControlsMovingToLeft = false;
					spr_UI_controls[ui_ControlSelectedIndex].nextFrame();
				}
				else if (((keyStates & RIGHT_PRESSED) != 0) && (ui_ControlSelectedIndex < 2)) {
					m_timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_controls[ui_ControlSelectedIndex].prevFrame();
					ui_ControlSelectedIndex++;
					ui_CurrentMessageIndex = ui_ControlSelectedIndex;
					isUIControlsMoving = true;
					isUIControlsMovingToLeft = true;
					spr_UI_controls[ui_ControlSelectedIndex].nextFrame();
				}
				else if ((keyStates & FIRE_PRESSED) != 0) {
					m_timeButtonLastPressed = System.currentTimeMillis();
					isUIControlsMoving = false;
					isUIControlsMovingToLeft = false;
					switch(ui_ControlSelectedIndex){
//					case 0:
//						spr_Placeholder.setVisible(true);
//						g_GameState = 7;
//						ui_CurrentMessageIndex = 3;
//						break;
//					case 1:
//						if(!isWave)
//						g_GameState = 6;
//						break;
//					case 2:
//						g_GameState = 1;
//						reset();
//						isMenu = true;
//						isSplash = false;
//						isGameover = false;
//						isTheatre = false;
//						break;
					}
				}
			}
			break;
				
		// spawning minions in
		case 6:
//			currWaveNumber++;
//			currWaveMinionHealth = (currWaveNumber * 15);
//			spawn(currWaveMinionHealth, 20);
//			isWave = true;
//			isThereAnyMinionAlive = true;
//			timeWaveStart = System.currentTimeMillis();
//			g_GameState = 0;
		break;
		
		// new turret building game state
		case 7:
			if(System.currentTimeMillis() - m_timeButtonLastPressed > 66) {
//				if 		(((keyStates & LEFT_PRESSED)		!= 0) && (spr_Placeholder.getX() > (0))) 		{
//					spr_Placeholder.setPosition(spr_Placeholder.getX() - 5, spr_Placeholder.getY());
//					m_timeButtonLastPressed = System.currentTimeMillis();
//				}
//		    	else if (((keyStates & RIGHT_PRESSED) 	!= 0) && (spr_Placeholder.getX() < (160 - 10))) {
//		    		spr_Placeholder.setPosition(spr_Placeholder.getX() + 5, spr_Placeholder.getY());
//		    		m_timeButtonLastPressed = System.currentTimeMillis();
//				}
//		    	else if (((keyStates & UP_PRESSED) 		!= 0) && (spr_Placeholder.getY() > (0))) 		{
//		    		spr_Placeholder.setPosition(spr_Placeholder.getX(), spr_Placeholder.getY() - 5);
//		    		m_timeButtonLastPressed = System.currentTimeMillis();
//				}
//		    	else if (((keyStates & DOWN_PRESSED) 	!= 0) && (spr_Placeholder.getY() < (170 - 10))) {
//		    		spr_Placeholder.setPosition(spr_Placeholder.getX(), spr_Placeholder.getY() + 5);
//		    		m_timeButtonLastPressed = System.currentTimeMillis();
//				}
//		    	else if ((keyStates & FIRE_PRESSED) 	!= 0 && (System.currentTimeMillis() - timeButtonLastPressed > 333)){
//		    		build(spr_Placeholder.getX(), spr_Placeholder.getY(), 8, 20);
//		    		m_timeButtonLastPressed = System.currentTimeMillis();
//		    		g_GameState = 0;
//					spr_Placeholder.setVisible(false);
//		    	}
			}
		break;
		}
	}
}
