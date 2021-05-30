/**
 * See LICENSE for details.
 */

package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class MainMenuScene extends BaseMicroEditionScene implements Scene {
	public static final String TAG = "MainMenuScene";

	private final Graphics g;
	private final LayerManager layMan_Menu;
	private Sprite[] spr_UI_menu = new Sprite[2];

	private Image splash_Menu;
	private Image ui_HUDcontrols;
	private Image ui_Main;

	private String[] sz_MenuLabels;
	private int menu_OptionSelectedIndex = 0;

	public MainMenuScene() {
		super();
		this.g = super.getGraphics();
		layMan_Menu = new LayerManager();
	}

	public void init() {
		Log.i(TAG, "Loading main menu resources... ");
		try {
			this.splash_Menu = Image.createImage("/legacy/splash_menu.png");
			this.ui_HUDcontrols = Image.createImage("/legacy/ui_controls.png");
			this.ui_Main = Image.createImage("/legacy/ui_main.png");

			// sets up the icons in the main menu
			spr_UI_menu = new Sprite[2];
			for (int i = 0; i < 2; i++) {
				spr_UI_menu[i] = new Sprite(ui_HUDcontrols, 32, 32);
				layMan_Menu.append(spr_UI_menu[i]);
			}
			// places and sets the main menu icons
			spr_UI_menu[0].setPosition(screen.getCenterX() - 16,
					screen.getCenterY() - 18);
			spr_UI_menu[1].setPosition(screen.getCenterX() - 16,
					screen.getCenterY() + 18);
			spr_UI_menu[0].nextFrame();
			spr_UI_menu[0].nextFrame();
			spr_UI_menu[0].nextFrame();
			spr_UI_menu[1].nextFrame();
			spr_UI_menu[1].nextFrame();
			spr_UI_menu[1].nextFrame();
			spr_UI_menu[1].nextFrame();

			sz_MenuLabels = new String[2];
			sz_MenuLabels[0] = new String("Begin the defense");
			sz_MenuLabels[1] = new String("Abandon ship");
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update(final long delta) {
		// TODO Auto-generated method stub
		this.getInput();
	}

	public void render(final long delta) {
		// TODO Auto-generated method stub
		this.g.drawImage(this.splash_Menu, screen.getCenterX(),
				screen.getCenterY(), Graphics.VCENTER | Graphics.HCENTER);
		this.g.drawImage(this.ui_Main, screen.getCenterX(), 24,
				Graphics.TOP | Graphics.HCENTER);
		this.g.setColor(255, 255, 255);
		this.g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
				Font.SIZE_MEDIUM));
		this.g.drawString(this.sz_MenuLabels[this.menu_OptionSelectedIndex],
				screen.getCenterX(), screen.getCenterY() - 48,
				Graphics.TOP | Graphics.HCENTER);
		this.g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,
				Font.SIZE_MEDIUM));
		this.g.drawString("Created by Laszlo Lukacs, 2010",
				screen.getCenterX(), screen.getHeight() - 12,
				Graphics.BOTTOM | Graphics.HCENTER);
		this.g.drawString("v1.0", screen.getWidth(), screen.getHeight(),
				Graphics.BOTTOM | Graphics.RIGHT);
		layMan_Menu.paint(this.g, 0, 0);

		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();

		// state for the main menu
		if (System.currentTimeMillis() - lastTimeButtonPressed > 166) {
			if (((keyStates & UP_PRESSED) != 0)
					&& (menu_OptionSelectedIndex > 0)) {
				lastTimeButtonPressed = System.currentTimeMillis();
				Log.i(TAG, "UP_PRESSED");
				spr_UI_menu[menu_OptionSelectedIndex].prevFrame();
				menu_OptionSelectedIndex = 0;
				spr_UI_menu[menu_OptionSelectedIndex].nextFrame();
			} else if (((keyStates & DOWN_PRESSED) != 0)
					&& (menu_OptionSelectedIndex < 1)) {
				lastTimeButtonPressed = System.currentTimeMillis();
				Log.i(TAG, "DOWN_PRESSED");
				spr_UI_menu[menu_OptionSelectedIndex].prevFrame();
				menu_OptionSelectedIndex = 1;
				spr_UI_menu[menu_OptionSelectedIndex].nextFrame();
			} else if (((keyStates & FIRE_PRESSED) != 0)
					&& (System.currentTimeMillis()
							- lastTimeButtonPressed > 300)) {
				lastTimeButtonPressed = System.currentTimeMillis();
				switch (menu_OptionSelectedIndex) {
				case 0:
					lastTimeButtonPressed = System.currentTimeMillis();
					Log.i(TAG, "FIRE_PRESSED! - Start game selected");
					// isTheatre = true;
					Scene nextScene = SceneFactory
							.createSceneByKey(SceneFactory.GAME_SCENE);
					Game.setScene(nextScene);
					break;
				case 1:
					Log.i(TAG, "FIRE_PRESSED! - Exit selected");
					Game.quit();
					break;
				}
			}
		}
	}
}
