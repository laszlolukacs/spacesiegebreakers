package hu.laszlolukacs.spacesiegebreakers.scenes;

import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDletStateChangeException;

import hu.laszlolukacs.spacesiegebreakers.Game;
import hu.laszlolukacs.spacesiegebreakers.utils.Log;

public class SplashScreenScene extends GameCanvas implements Scene {
	public static final String TAG = "SplashScreenScene";

	private long m_timeButtonLastPressed = 0;

	private int m_screenWidth, m_screenHeight, m_centerHorizontal,
			m_centerVertical, m_cornerX, m_cornerY;
	private Graphics g;

	private Image logoImage;
	private Image backgroundImage;

	public SplashScreenScene() {
		super(true);
		this.g = super.getGraphics();
		this.m_screenWidth = super.getWidth(); // width of the LCD
		this.m_screenHeight = super.getHeight(); // height of the LCD
		this.m_centerHorizontal = m_screenWidth / 2; // center of the LCD
		this.m_centerVertical = m_screenHeight / 2;
		this.m_cornerX = (m_screenWidth - 160) / 2; // corner of the Game Canvas
		this.m_cornerY = (m_screenHeight - 180) / 2;
	}

	public void init() {
		Log.i(TAG, "Loading splash screen resources... ");
		try {
			this.logoImage = Image.createImage("/legacy/splash_logo.png");
			this.backgroundImage = Image
					.createImage("/legacy/splash_startup.png");
		} catch (IOException ioex) {
			if (Log.getEnabled()) {
				Log.e(TAG, "Failed to load the resources, reason: "
						+ ioex.getMessage());
				ioex.printStackTrace();
			}
		}
	}

	public void update() {
		this.getInput();
	}

	public void render() {
		this.g.drawImage(backgroundImage, m_centerHorizontal, m_centerVertical,
				Graphics.VCENTER | Graphics.HCENTER);
		this.g.drawImage(logoImage, m_centerHorizontal, 24,
				Graphics.TOP | Graphics.HCENTER);
		this.g.setColor(255, 255, 255);
		this.g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD,
				Font.SIZE_LARGE));
		this.g.drawString("Press 'FIRE' to begin", m_centerHorizontal,
				m_screenHeight - 48, Graphics.BOTTOM | Graphics.HCENTER);
		this.g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,
				Font.SIZE_MEDIUM));
		this.g.drawString("Created by Laszlo Lukacs, 2010", m_centerHorizontal,
				m_screenHeight - 12, Graphics.BOTTOM | Graphics.HCENTER);
		
		super.flushGraphics();
	}

	private void getInput() {
		int keyStates = super.getKeyStates();

		// startup splash screen
		if ((keyStates & FIRE_PRESSED) != 0 && (System.currentTimeMillis()
				- m_timeButtonLastPressed > 300)) {
			m_timeButtonLastPressed = System.currentTimeMillis();

			// TODO: switch to MainMenuScene
			Log.i(TAG, "FIRE_PRESSED! - Start game selected");
			Game.setScene(new MainMenuScene());
		}
	}
}
