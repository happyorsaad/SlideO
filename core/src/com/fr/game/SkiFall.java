package com.fr.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fr.entities.GameWorld;
import com.fr.entities.Snow;
import com.fr.screens.GameScreen;
import com.fr.screens.HelpScreen;
import com.fr.screens.HighScoreScreen;
import com.fr.screens.MainScreen;
import com.fr.screens.RateUsScreen;
import com.fr.screens.SettingScreen;
import com.fr.screens.SplashScreen;
import com.fr.utils.Assets;
import com.fr.utils.Settings;

public class SkiFall extends Game {

	public SpriteBatch batch;
	public OrthographicCamera cam;
	public Snow snow;

	public ActionResolver actionResolver;
	boolean firstTimeCreate = true;

	public static MainScreen mainScreen;
	public static HelpScreen helpScreen;
	public static GameScreen gameScreen;
	public static SettingScreen settingScreen;
	public static HighScoreScreen highScoresScreen;
	public static SplashScreen splashScreen;
	public static RateUsScreen rateUsScreen;

	public SkiFall(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		cam = new OrthographicCamera(GameWorld.WORLD_WIDTH,
				GameWorld.WORLD_HEIGHT);
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();
		Settings.load();
		Assets.load();
		if (Settings.soundOn) {
			Assets.playMusic();
		}

		/*
		 * if (actionResolver.getSignedInGPGS()) { long max =
		 * actionResolver.getMaxValue(); if (max < Settings.highscores[0]) {
		 * actionResolver.submitScoreGPGS(max); } }
		 */

		this.snow = new Snow();
		this.gameScreen = new GameScreen(this);
		this.mainScreen = new MainScreen(this);
		this.helpScreen = new HelpScreen(this);
		this.splashScreen = new SplashScreen(this);
		this.settingScreen = new SettingScreen(this);
		this.highScoresScreen = new HighScoreScreen(this);
		this.rateUsScreen = new RateUsScreen(this);
		
		setScreen(SkiFall.splashScreen);
	}

	@Override
	public void render() {
		super.render();
	}

}