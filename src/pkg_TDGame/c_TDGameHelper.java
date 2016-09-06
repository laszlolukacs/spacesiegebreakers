package pkg_TDGame;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.midlet.MIDletStateChangeException;

class c_TDGameHelper extends GameCanvas implements Runnable {
	// TODO: set variables
	// system resources
	private c_MIDletHelper manager;
	private Display dgDisp;
	
	// game state descriptors
	private volatile boolean isRunning;
	private volatile boolean isWave;
	private volatile boolean isGameover;
	private boolean isVictory;
	private boolean isThereAnyMinionAlive;
	private int g_GameState;
	private int currWaveNumber;
	private int currWaveMinionHealth;
	private int currWaveMinionReward;
	private int playerCredits;
	private int playerLives;
	private int playerScore;
	private long timeWaveStart;
	
	// graphics descriptors
	private int w, h, cx, cy, rx, ry;
	private int[] num_DeathFXFrameCounter;
	
	// sync descriptors
	private long cycleStartTime;
	private long cycleCompleteTime;
	private long g_TargetSpeed; // target running speed: millisec / frame
	private long timeButtonLastPressed; // to prevent very fast button push repeating
	
	// game element descriptors
	// defining map, turrets' damage, minons' health, minions move direction
	private int[] g_map, g_turretDmg, g_minionHth, moveDirection; // the direction of the minions movement, left: 1, right: 2, up: 3, down: 4
	private int g_turretNum, g_minionNum; 	// number of turrets, minions
	private long timeSpawnCondition; 	// minion spawning cooldown time
	private long[] timeTurretLastShoot;	// turret cooldown time
	
	// UI descriptors
	private boolean isTheatre;
	private boolean isMenu;
	private boolean isSplash;
	private boolean isUIControlsMoving;
	private boolean isUIControlsMovingToLeft;
	private int ui_CurrentMessageIndex;
	private int ui_ControlSelectedIndex;
	private int ui_SliderCounter;
	private int menu_OptionSelectedIndex;
	private Font fnt_CurrFont;
	private String[] sz_MenuLabels;	// strings for the UI
	private String sz_Credits, sz_Lives, sz_Score;
	private String[] sz_CurrentInfo;
	
	// graphics assets
	private LayerManager layMan_Game;
	private LayerManager layMan_Menu;
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
	private Image splash_Logo;
	private Image splash_Startup;
	private Image splash_Menu;
	private Image splash_Gameover;
	private Image ui_Defeat;
	private Image ui_Header;
	private Image ui_HUDicons;
	private Image ui_HUDcontrols;
	private Image ui_Main;
	private Image ui_Tray;
	private Image ui_Victory;
	
	// default ctor
	protected c_TDGameHelper(c_MIDletHelper MIDletMan_in, Display dgDisp_in) {
		super(true);
		setFullScreenMode(true);
		manager = MIDletMan_in;
		dgDisp = dgDisp_in;
		initVars();
		initMap();
		try	{
			initAssets();
		}
		catch (IOException ioex){
			System.err.println("WARNING: The following exception occurred: " + ioex.toString());
			ioex.printStackTrace();
		}
	}
	
	// TODO: set up vars
	private void initVars(){
		// game state descriptors
		isRunning = false;
		isGameover = false;
		isVictory = false;
		g_GameState = 18;
		isWave = false;
		isTheatre = false;
		isSplash = true;
		isMenu = false;
		isUIControlsMoving = false;
		isUIControlsMovingToLeft = false;
		isThereAnyMinionAlive = true;
		timeWaveStart = 0;
		timeButtonLastPressed = System.currentTimeMillis();
		currWaveMinionHealth = 10;
		currWaveMinionReward = 1;
		currWaveNumber = 0;

		// graphics descriptors
		w = getWidth();		// width of the LCD
		h = getHeight();	// height of the LCD
		cx = w / 2;			// center of the LCD
		cy = h / 2;
		rx = (w - 160) / 2;	// corner of the Game Canvas
		ry = (h - 180) / 2;
		num_DeathFXFrameCounter = new int[20];
		ui_ControlSelectedIndex = 0;
		ui_CurrentMessageIndex = 0;
		ui_SliderCounter = 0;
		menu_OptionSelectedIndex = 0;
		sz_MenuLabels = new String[2];
		sz_MenuLabels[0] = new String("Begin the defense");
		sz_MenuLabels[1] = new String("Abandon ship");
		
		// sync descriptors
		g_TargetSpeed = 20; // target running speed: ~millisec / frame
		
		// game element descriptors
		g_turretDmg = new int[108];
		g_turretNum = 0;
		g_minionHth = new int[20];
		g_minionNum = 0;
		moveDirection = new int[20]; // the direction of the minions movement, left: 1, right: 2, up: 3, down: 4
		timeTurretLastShoot = new long[108];
		
		// player status descriptors
		playerCredits = 40;
		playerLives = 20;
		playerScore = 0;
		
		// strings for the UI
		fnt_CurrFont = Font.getDefaultFont(); 
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
	}
	// TODO: set images
	private void initAssets() throws IOException{
		// loads image assets
		System.out.println("INFORMATION: Loading image assets...");
		img_Background = Image.createImage("/abs_bkg.png");
		img_MinionDeathFX = Image.createImage("/fx.png");
		img_Map = Image.createImage("/map_tiles.png");
		img_Minion = Image.createImage("/minions.png");
		img_Placeholder = Image.createImage("/placeholder.png");
		img_Turret = Image.createImage("/turret.png");

		splash_Logo = Image.createImage("/splash_logo.png");
		splash_Startup = Image.createImage("/splash_startup.png");
		splash_Menu = Image.createImage("/splash_menu.png");
		splash_Gameover = Image.createImage("/splash_gameover.png");
		
		ui_Defeat = Image.createImage("/ui_defeat.png");
		ui_Header = Image.createImage("/ui_header.png");
		ui_HUDicons = Image.createImage("/ui_icons.png");
		ui_HUDcontrols = Image.createImage("/ui_controls.png");
		ui_Main = Image.createImage("/ui_main.png");
		ui_Tray = Image.createImage("/ui_tray.png");
		ui_Victory = Image.createImage("/ui_victory.png");
		
		// creates sprites and map
		System.out.println("INFORMATION: Creating sprites...");
	    // creates the layer manager instance
	    layMan_Game = new LayerManager();
	    
		// the turret place picker square
		spr_Placeholder = new Sprite(img_Placeholder, 10, 10);
		spr_Placeholder.defineReferencePixel(5, 5);
		spr_Placeholder.setVisible(false);
		layMan_Game.append(spr_Placeholder);
		
		// creates minion death effect animation sprites
		spr_FX = new Sprite[20];
		for(int i = 0; i < 20; i++){
			spr_FX[i] = new Sprite(img_MinionDeathFX, 20, 20);
			spr_FX[i].defineReferencePixel(10, 10);
			spr_FX[i].setVisible(false);
			layMan_Game.append(spr_FX[i]);
		}
		
		// creates turret sprites
	    spr_Turrets = new Sprite[108];
	    for(int i = 0; i < 108; i++){
	    	spr_Turrets[i] = new Sprite(img_Turret, 10, 10);
	    	spr_Turrets[i].setVisible(false);
	    	layMan_Game.append(spr_Turrets[i]);
	    }
	    
		// creates map from the int array
		tiled_Map = new TiledLayer(16, 17, img_Map, 10, 10);
	    for (int i = 0; i < g_map.length; i++) {
	        int column = i % 16;
	        int row = (i - column) / 16;
	        tiled_Map.setCell(column, row, g_map[i]);
	    }
		layMan_Game.append(tiled_Map);

		// sets up the in-game user interface (HUD)
		layMan_UI = new LayerManager();
		
		// creates the status icons
		spr_UI_icons = new Sprite[3];
		for(int i = 0; i < 3; i++){
			spr_UI_icons[i] = new Sprite(ui_HUDicons, 16, 16);
			layMan_UI.append(spr_UI_icons[i]);
		}
		// places and sets the status icons
		spr_UI_icons[1].setPosition(w - 36, 2);
		spr_UI_icons[0].setPosition(spr_UI_icons[1].getX() - 56, 2);
		spr_UI_icons[2].setPosition(2, 2);
		spr_UI_icons[1].nextFrame();
		spr_UI_icons[2].nextFrame();
		spr_UI_icons[2].nextFrame();
		
		// creates control icons
		spr_UI_controls = new Sprite[3];
		for(int i = 0; i < 3; i++){
			spr_UI_controls[i] = new Sprite(ui_HUDcontrols, 32, 32);
			layMan_UI.append(spr_UI_controls[i]);
		}
		// places and sets the control icons
		spr_UI_controls[0].setPosition(cx - 16, h - 32);
		spr_UI_controls[1].setPosition(cx + 18, h - 32);
		spr_UI_controls[2].setPosition(cx + 52, h - 32);
		spr_UI_controls[0].nextFrame();
		spr_UI_controls[1].nextFrame();
		spr_UI_controls[1].nextFrame();
		for(int i = 0; i < 4; i++){
			spr_UI_controls[2].nextFrame();
		}
		
		// sets up the main menu GUI
		layMan_Menu = new LayerManager();
		
		// sets up the icons in the main menu
		spr_UI_menu = new Sprite[2];
		for(int i = 0; i < 2; i++){
			spr_UI_menu[i] = new Sprite(ui_HUDcontrols, 32, 32);
			layMan_Menu.append(spr_UI_menu[i]);
		}
		// places and sets the main menu icons
		spr_UI_menu[0].setPosition(cx - 16, cy - 18);
		spr_UI_menu[1].setPosition(cx - 16, cy + 18);
		spr_UI_menu[0].nextFrame();
		spr_UI_menu[0].nextFrame();
		spr_UI_menu[0].nextFrame();
		spr_UI_menu[1].nextFrame();
		spr_UI_menu[1].nextFrame();
		spr_UI_menu[1].nextFrame();
		spr_UI_menu[1].nextFrame();

		// when finished put out on the console
		System.out.println("INFORMATION: Load complete");
	}
	
	private void reset(){
		System.out.println("INFORMATION: Resetting variables...");	  
		// game state descriptors
		isGameover = false;
		g_GameState = 1;
		isWave = false;
		isSplash = false;
		isMenu = false;
		isVictory = false;
		isTheatre = false;
		isThereAnyMinionAlive = true;
		timeWaveStart = 0;
		currWaveMinionHealth = 10;
		currWaveMinionReward = 1;
		currWaveNumber = 0;

		// game element descriptors
		g_turretDmg = new int[108];
		g_turretNum = 0;
		g_minionHth = new int[20];
		g_minionNum = 0;
		
		// player status descriptors
		playerCredits = 40;
		playerLives = 20;
		playerScore = 0;
		
		// strings for the UI
		fnt_CurrFont = Font.getDefaultFont(); 
		sz_Credits = new String("40");
		sz_Lives = new String("20");
		sz_Score = new String("0");
		//ui_ControlSelectedIndex = 0;
		//menu_OptionSelectedIndex = 0;
		
		System.out.println("INFORMATION: Resetting layer manager...");	    
	    layMan_Game = new LayerManager();
		layMan_Game.append(spr_Placeholder);
	    
		for(int i = 0; i < 20; i++){
			spr_FX[i].setVisible(false);
			layMan_Game.append(spr_FX[i]);
		}
		
	    for(int i = 0; i < 108; i++){
	    	spr_Turrets[i].setVisible(false);
	    	layMan_Game.append(spr_Turrets[i]);
	    }
		layMan_Game.append(tiled_Map);
	}
	
	public void stop(){ isRunning = false;	}
	
	public void start() {
		isRunning = true;
		Thread gameThread = new Thread(this);
		gameThread.start();
	}
	
	// play the explosion minion death animation
	private void playMinionDeathAnim(int minionID_in){
		int spawnX = spr_Minions[minionID_in].getX();
		int spawnY = spr_Minions[minionID_in].getY();
				
		spr_FX[minionID_in].setPosition(spawnX, spawnY);
		spr_FX[minionID_in].setVisible(true);
		num_DeathFXFrameCounter[minionID_in] = 0;
	}
	
	// pushes a minion around
	private void move(Sprite minion, int dir){
		switch(dir){
		case 1:
			minion.setPosition(minion.getX() - 1, minion.getY());
			break;
		case 2:
			minion.setPosition(minion.getX() + 1, minion.getY());
			break;
		case 3:
			minion.setPosition(minion.getX(), minion.getY() - 1);
			break;
		case 4:
			minion.setPosition(minion.getX(), minion.getY() + 1);
			break;
		}
	}
	
	// makes the minions moving on their route
	private boolean checkPath(){
		for(int i = 0; i < timeSpawnCondition; i++){
			if(spr_Minions[i].isVisible() == true){
				switch(spr_Minions[i].getX()){
				case 0:
					switch(spr_Minions[i].getY()){
					case 0:
						moveDirection[i] = 4;
						break;
					case 70:
						moveDirection[i] = 2;
						break;
					case 110:
						moveDirection[i] = 4;
						break;
					case 150:
						moveDirection[i] = 2;
						break;
					}
					break;
				case 40:
					switch(spr_Minions[i].getY()){
					case -20:
						moveDirection[i] = 4;
						break;
					case 0:
						moveDirection[i] = 1;
						break;
					case 40:
						moveDirection[i] = 2;
						break;
					case 70:
						moveDirection[i] = 3;
						break;
					}
					break;
				case 80:
					if(spr_Minions[i].getY() == 0){
						moveDirection[i] = 3;
					}
					else if(spr_Minions[i].getY() == -20){
						moveDirection[i] = 4;
						spr_Minions[i].setPosition(40, -20);
						move(spr_Minions[i], moveDirection[i]);
						return true;
					}
					break;
				case 100:
					switch(spr_Minions[i].getY()){
					case 40:
						moveDirection[i] = 4;
						break;
					case 110:
						moveDirection[i] = 1;
						break;
					}
					break;
				case 140:
					switch(spr_Minions[i].getY()){
					case 0:
						moveDirection[i] = 1;
						break;
					case 150:
						moveDirection[i] = 3;
						break;
					}
					break;
				}
				//
				move(spr_Minions[i], moveDirection[i]);
			}
		}
		return false;
	}
	
	private void rangeCheck(){
		isThereAnyMinionAlive = false;
		for(int i = 0; i < g_turretNum; i++){
			for(int j = 0; j < g_minionNum; j++){
				if(spr_Minions[j].isVisible() && spr_Turrets[i].collidesWith(spr_Minions[j], false) && ((System.currentTimeMillis() - timeTurretLastShoot[i]) >= 600)){
					timeTurretLastShoot[i] = System.currentTimeMillis();
					g_minionHth[j] -= g_turretDmg[i];
					if(g_minionHth[j] <= 0){
						spr_Minions[j].setVisible(false);
						playMinionDeathAnim(j);
						playerCredits += currWaveMinionReward;
						playerScore += currWaveMinionHealth;
					}
				}
				if(spr_Minions[j].isVisible() == true){
					isThereAnyMinionAlive = true;
				}
			}
		}
	}
	
	// updates game state
	private void tick() {
		if(isUIControlsMoving){
			ui_SliderCounter++;
			if(isUIControlsMovingToLeft && (spr_UI_controls[0].getX() > 32)){
				for(int i = 0; i < 3; i++){
					spr_UI_controls[i].setPosition(spr_UI_controls[i].getX() - 4, spr_UI_controls[i].getY());
				}
			}
			else if(!isUIControlsMovingToLeft && (spr_UI_controls[2].getX() < w - 32)){
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
				if(checkPath()){
					dgDisp.vibrate(333);
					playerLives--;
				}
				if(playerLives <= 0){
					isWave = false;
					isTheatre = false;
					isGameover = true;
					isMenu = false;
					isSplash = false;
					g_GameState = 19;
				}
				else if(playerLives < 6){
					ui_CurrentMessageIndex = 6;
				}

				if(g_turretNum != 0){
					rangeCheck();
				}
				if(isThereAnyMinionAlive == false){
					isWave = false;
					System.out.println("INFORMATION: Wave no. " + currWaveNumber + " completed!");
					if(currWaveNumber == 10){
						isWave = false;
						isTheatre = false;
						isVictory = true;
						isGameover = false;
						isMenu = false;
						isSplash = false;
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
	
	// monitors keypresses
	// NOTE: keypresses are high-active
	private void getInput() {
		int keyState = getKeyStates();
		
		// processes keypresses according to game state
		switch(g_GameState){
		// base state for a running game, listening for main controls
		case 0:
			if(System.currentTimeMillis() - timeButtonLastPressed > 166) {
				if (((keyState & LEFT_PRESSED)		 != 0) && (ui_ControlSelectedIndex > 0)) {
					timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_controls[ui_ControlSelectedIndex].prevFrame();
					ui_ControlSelectedIndex--;
					ui_CurrentMessageIndex = ui_ControlSelectedIndex;
					isUIControlsMoving = true;
					isUIControlsMovingToLeft = false;
					spr_UI_controls[ui_ControlSelectedIndex].nextFrame();
				}
				else if (((keyState & RIGHT_PRESSED) != 0) && (ui_ControlSelectedIndex < 2)) {
					timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_controls[ui_ControlSelectedIndex].prevFrame();
					ui_ControlSelectedIndex++;
					ui_CurrentMessageIndex = ui_ControlSelectedIndex;
					isUIControlsMoving = true;
					isUIControlsMovingToLeft = true;
					spr_UI_controls[ui_ControlSelectedIndex].nextFrame();
				}
				else if ((keyState & FIRE_PRESSED) != 0) {
					timeButtonLastPressed = System.currentTimeMillis();
					isUIControlsMoving = false;
					isUIControlsMovingToLeft = false;
					switch(ui_ControlSelectedIndex){
					case 0:
						spr_Placeholder.setVisible(true);
						g_GameState = 7;
						ui_CurrentMessageIndex = 3;
						break;
					case 1:
						if(!isWave)
						g_GameState = 6;
						break;
					case 2:
						g_GameState = 1;
						reset();
						isMenu = true;
						isSplash = false;
						isGameover = false;
						isTheatre = false;
						break;
					}
				}
			}
			break;
			
		// state for the main menu
		case 1:
			if(System.currentTimeMillis() - timeButtonLastPressed > 166) {
				if (((keyState & UP_PRESSED)		!= 0) && (menu_OptionSelectedIndex > 0)) {
					timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_menu[menu_OptionSelectedIndex].prevFrame();
					menu_OptionSelectedIndex = 0;
					spr_UI_menu[menu_OptionSelectedIndex].nextFrame();
				}
				else if (((keyState & DOWN_PRESSED) != 0) && (menu_OptionSelectedIndex < 1)) {
					timeButtonLastPressed = System.currentTimeMillis();
					spr_UI_menu[menu_OptionSelectedIndex].prevFrame();
					menu_OptionSelectedIndex = 1;
					spr_UI_menu[menu_OptionSelectedIndex].nextFrame();
				}
				else if (((keyState & FIRE_PRESSED) != 0) && (System.currentTimeMillis() - timeButtonLastPressed > 300)){
					timeButtonLastPressed = System.currentTimeMillis();
					switch(menu_OptionSelectedIndex){
					case 0:
						timeButtonLastPressed = System.currentTimeMillis();
						isSplash = false;
						isMenu = false;
						isGameover = false;
						isTheatre = true;
						g_GameState = 0;
					break;
					case 1:
						try {
							manager.destroyApp(true);
						}
						catch (MIDletStateChangeException e) {
							e.printStackTrace();
						}
						manager.notifyDestroyed();
						break;
					}
				}
			}
		break;
				
		// spawning minions in
		case 6:
			currWaveNumber++;
			currWaveMinionHealth = (currWaveNumber * 15);
			spawn(currWaveMinionHealth, 20);
			isWave = true;
			isThereAnyMinionAlive = true;
			timeWaveStart = System.currentTimeMillis();
			g_GameState = 0;
		break;
		
		// new turret building game state
		case 7:
			if(System.currentTimeMillis() - timeButtonLastPressed > 66) {
				if 		(((keyState & LEFT_PRESSED)		!= 0) && (spr_Placeholder.getX() > (0))) 		{
					spr_Placeholder.setPosition(spr_Placeholder.getX() - 5, spr_Placeholder.getY());
					timeButtonLastPressed = System.currentTimeMillis();
				}
		    	else if (((keyState & RIGHT_PRESSED) 	!= 0) && (spr_Placeholder.getX() < (160 - 10))) {
		    		spr_Placeholder.setPosition(spr_Placeholder.getX() + 5, spr_Placeholder.getY());
		    		timeButtonLastPressed = System.currentTimeMillis();
				}
		    	else if (((keyState & UP_PRESSED) 		!= 0) && (spr_Placeholder.getY() > (0))) 		{
		    		spr_Placeholder.setPosition(spr_Placeholder.getX(), spr_Placeholder.getY() - 5);
		    		timeButtonLastPressed = System.currentTimeMillis();
				}
		    	else if (((keyState & DOWN_PRESSED) 	!= 0) && (spr_Placeholder.getY() < (170 - 10))) {
		    		spr_Placeholder.setPosition(spr_Placeholder.getX(), spr_Placeholder.getY() + 5);
					timeButtonLastPressed = System.currentTimeMillis();
				}
		    	else if ((keyState & FIRE_PRESSED) 	!= 0 && (System.currentTimeMillis() - timeButtonLastPressed > 333)){
		    		build(spr_Placeholder.getX(), spr_Placeholder.getY(), 8, 20);
		    		timeButtonLastPressed = System.currentTimeMillis();
		    		g_GameState = 0;
					spr_Placeholder.setVisible(false);
		    	}
			}
		break;
		
		// startup splash screen
		case 18:
			if (((keyState & FIRE_PRESSED)) != 0 && (System.currentTimeMillis() - timeButtonLastPressed > 300)){
				timeButtonLastPressed = System.currentTimeMillis();
				isSplash = false;
				isGameover = false;
				isMenu = true;
				isTheatre = false;
				g_GameState = 1;
			}
		break;
		
		// defeat screen
		case 19:
			if (((keyState & FIRE_PRESSED) != 0) && (System.currentTimeMillis() - timeButtonLastPressed > 300)){
				timeButtonLastPressed = System.currentTimeMillis();
				reset();
				isSplash = false;
				isGameover = false;
				isVictory = false;
				isMenu = true;
				isTheatre = false;
				g_GameState = 1;
			}
			break;
		}
	}
	
	// required because this class implements Runnable interface
	public void run() {
		Graphics g = getGraphics();
		// main loop of the game
		while(isRunning) {
			cycleStartTime = System.currentTimeMillis();
			// handles input and events
			getInput();
			// updates game state
			tick();
			// renders a frame
			render(g);
			// makes current thread sleep -- helps syncing
			cycleCompleteTime = System.currentTimeMillis() - cycleStartTime;
			if (cycleCompleteTime < g_TargetSpeed) {
				synchronized (this) {
		                try {
							Thread.sleep(g_TargetSpeed - cycleCompleteTime);
						}
		                catch (InterruptedException e) { stop(); }
				}
			}
		    else
		    	Thread.yield();
		}
	}
	
	private void render(Graphics g) {
		if(isTheatre) {
			// draws the starscape background
			g.drawImage(img_Background, cx, cy, Graphics.VCENTER | Graphics.HCENTER);
			// draws the HUD
			g.drawImage(ui_Header, 0, 0, Graphics.TOP | Graphics.LEFT);
			g.drawImage(ui_Tray, 0, h, Graphics.BOTTOM | Graphics.LEFT);
			fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
			g.setFont(fnt_CurrFont);
			g.drawString(sz_Credits, w - 38, 0, Graphics.TOP | Graphics.RIGHT);
			g.drawString(sz_Lives, w - 2, 0, Graphics.TOP | Graphics.RIGHT);
			g.drawString(sz_Score, 24, 0, Graphics.TOP | Graphics.LEFT);
			fnt_CurrFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
			g.setFont(fnt_CurrFont);
			g.drawString(sz_CurrentInfo[ui_CurrentMessageIndex], cx, h - 42, Graphics.BOTTOM | Graphics.HCENTER);				
			// draws the map and the entities
			layMan_Game.paint(g, rx, ry);
			// draws the graphics elements of the UI
			layMan_UI.paint(g, 0, 0);
		}
		else {
			// splash
			if(isSplash) {
				g.drawImage(splash_Startup, cx, cy, Graphics.VCENTER | Graphics.HCENTER);
				g.drawImage(splash_Logo, cx, 24, Graphics.TOP | Graphics.HCENTER);
				g.setColor(255, 255, 255);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
				g.setFont(fnt_CurrFont);
				g.drawString("Press 'FIRE' to begin", cx, h - 48, Graphics.BOTTOM | Graphics.HCENTER);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
				g.setFont(fnt_CurrFont);
				g.drawString("Created by Laszlo Lukacs, 2010", cx, h - 12, Graphics.BOTTOM | Graphics.HCENTER);
			}
			else if(isMenu){
				g.drawImage(splash_Menu, cx, cy, Graphics.VCENTER | Graphics.HCENTER);
				g.drawImage(ui_Main, cx, 24, Graphics.TOP | Graphics.HCENTER);
				g.setColor(255, 255, 255);
				fnt_CurrFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
				g.setFont(fnt_CurrFont);
				g.drawString(sz_MenuLabels[menu_OptionSelectedIndex], cx, cy - 48, Graphics.TOP | Graphics.HCENTER);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
				g.setFont(fnt_CurrFont);
				g.drawString("Created by Laszlo Lukacs, 2010", cx, h - 12, Graphics.BOTTOM | Graphics.HCENTER);
				g.drawString("v1.0", w, h, Graphics.BOTTOM | Graphics.RIGHT);
				layMan_Menu.paint(g, 0, 0);
			}
			else if(isGameover){
				g.drawImage(splash_Gameover, cx, cy, Graphics.VCENTER | Graphics.HCENTER);
				g.drawImage(ui_Defeat, cx, 24, Graphics.TOP | Graphics.HCENTER);
				g.setColor(255, 255, 255);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
				g.setFont(fnt_CurrFont);
				g.drawString("You have been defeated!", cx, cy, Graphics.TOP | Graphics.HCENTER);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);
				g.setFont(fnt_CurrFont);
				g.drawString("Total awards: " + sz_Score, cx, cy + 16, Graphics.TOP | Graphics.HCENTER);
			}
			else if(isVictory){
				g.drawImage(splash_Gameover, cx, cy, Graphics.VCENTER | Graphics.HCENTER);
				g.drawImage(ui_Victory, cx, 24, Graphics.TOP | Graphics.HCENTER);
				g.setColor(255, 255, 255);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
				g.setFont(fnt_CurrFont);
				g.drawString("You have successfully defended the ship!", cx, cy, Graphics.TOP | Graphics.HCENTER);
				fnt_CurrFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_LARGE);
				g.setFont(fnt_CurrFont);
				g.drawString("Total awards: " + sz_Score, cx, cy + 16, Graphics.TOP | Graphics.HCENTER);
			}
		}
		// flushes graphics
		flushGraphics();
	}
	
	// builds a tower
	private boolean build(int X, int Y, int dmg, int range) {
		int price = 10;
		int tmp = playerCredits - price;
		switch(dmg){
		case 5:
			price = 7;
			break;
		case 8:
			price = 9;
			break;
		}
		if(tmp < 0) {
			return false;
		}
		// synces the int array and the screen coordinates
		int mapX = X / 10;
		int mapY = Y / 10;
		String dbgOut = new String(mapX + ", " + mapY);
		// selects the appropriate member from the int array
		if(g_map[mapX + mapY * 16] != 0){
			for(int i = 0; i < g_turretNum; i++)
				if((spr_Turrets[i].getX() == X) && (spr_Turrets[i].getY() == Y))
					return false;
			// places a Sprite here and sets up it's properties
			spr_Turrets[g_turretNum].setPosition(X, Y);
			spr_Turrets[g_turretNum].setVisible(true);
			// defining the range of the turrets
			spr_Turrets[g_turretNum].defineCollisionRectangle(0-range , 0-range, 2 * range, 2 * range);
			// sets turret's damage
			g_turretDmg[g_turretNum] = dmg;
			// increases global total turret number
			g_turretNum++;
			playerCredits = playerCredits - price;
			playerScore += (price);
			System.out.println("INFORMATION: Turret placed @ " + dbgOut);
			return true;
		}
		else
			return false;
	}
	
	private void spawn(int health, int num_minions) {
		System.out.println("INFORMATION: Starting new wave... (Level " + currWaveNumber + ", "  + health + " hp x " + num_minions + " minions)");
		g_minionNum = 0;
		spr_FX = new Sprite[num_minions];
		spr_Minions = new Sprite[num_minions];
		for(int i = 0; i < num_minions; i++){
			// places a Sprite and sets up its properties
			spr_FX[i] = new Sprite(img_MinionDeathFX, 20, 20);
			spr_Minions[i] = new Sprite(img_Minion, 20, 20);
			spr_Minions[i].setFrame(currWaveNumber - 1);
			spr_Minions[i].defineReferencePixel(10, 10);
			spr_FX[i].defineReferencePixel(10, 10);
			spr_FX[i].setVisible(false);
			spr_Minions[i].defineCollisionRectangle(2, 2, 16, 16);
			spr_Minions[i].setPosition(40, -20);
			// sets minion health
			g_minionHth[i] = health;
			// increases global total minion number
			g_minionNum = i + 1;
			// places a minion to the canvas
			layMan_Game.append(spr_FX[i]);
			layMan_Game.append(spr_Minions[i]);
		}
		System.out.println("INFORMATION: Minions have spawned!");
	}
	
	private void initMap() {
		g_map = new int[]{ 	0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0,
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
	}
}