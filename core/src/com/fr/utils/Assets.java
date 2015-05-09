package com.fr.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Assets {
	private static TextureAtlas slidoAtlas;

	public static Sprite frLogo;
	public static Sprite backgroundTint;
	public static Sprite slidoLogo;
	public static Sprite letsSlide;
	public static Sprite highScore;
	public static Sprite soundOnLogo;
	public static Sprite soundOffLogo;
	public static Sprite playButton;
	public static Sprite pauseButton;
	public static Sprite yetiDeadMessage;
	public static Sprite yetiDead;
	public static Sprite blood;
	public static Sprite scoreBackground;
	public static Sprite backButton;
	public static Sprite replayButton;
	public static Sprite gotItButton;
	public static Sprite signInButton;
	public static Sprite signOutButton;
	public static Sprite leaderboardRed;
	public static Sprite leaderBoardGreen;
	public static Sprite skipButton;
	public static Sprite settingButton;
	public static Sprite helpButton;
	public static Sprite sureButton;
	public static Sprite notNowButton;
	public static Sprite leaderBoardLong;
	public static Sprite rateUsButton;
	
	public static Sprite[] yetiHappy = new Sprite[11];
	public static Sprite spike;
	public static Sprite backButtonLong;
	public static Sprite exitButton;
	public static Sprite tree;
	public static Sprite snowflake;
	public static Sprite snowBackground;

	public static Sound clickSound;
	public static Sound hitSound;
	public static Sound scoreUp;

	public static Music gameMusic;

	public static BitmapFont scoreFont;
	public static BitmapFont messageFont;
	public static BitmapFont headingFont;

	public static void load() {
		slidoAtlas = new TextureAtlas(
				Gdx.files.internal("graphics/spritesheet/slidoAtlas.atlas"));
		frLogo = slidoAtlas.createSprite("fr.");
		backgroundTint = slidoAtlas.createSprite("backGround");
		slidoLogo = slidoAtlas.createSprite("slidoLogo");
		highScore = slidoAtlas.createSprite("highScores");
		soundOnLogo = slidoAtlas.createSprite("soundOn");
		soundOffLogo = slidoAtlas.createSprite("soundOff");
		letsSlide = slidoAtlas.createSprite("letsSlideButton");
		playButton = slidoAtlas.createSprite("playButton");
		pauseButton = slidoAtlas.createSprite("pauseButton");
		yetiDeadMessage = slidoAtlas.createSprite("deadMsg");
		yetiDead = slidoAtlas.createSprite("deadAlinu");
		scoreBackground = slidoAtlas.createSprite("scoreBase");
		backButton = slidoAtlas.createSprite("backButton");
		replayButton = slidoAtlas.createSprite("replayButton");
		blood = slidoAtlas.createSprite("blood");
		snowflake = slidoAtlas.createSprite("snowFlakeGrey");

		leaderBoardGreen = slidoAtlas.createSprite("leaderBoardButton");
		settingButton = slidoAtlas.createSprite("settingsButton");
		gotItButton = slidoAtlas.createSprite("gotItButton");
		helpButton = slidoAtlas.createSprite("helpButton");
		signInButton = slidoAtlas.createSprite("signInButton");
		signOutButton = slidoAtlas.createSprite("signOutButton");
		skipButton = slidoAtlas.createSprite("skipButton");
		leaderboardRed = slidoAtlas.createSprite("leaderBoardButtonRed");
		leaderBoardLong = slidoAtlas.createSprite("leaderBoard");
		sureButton = slidoAtlas.createSprite("sureButton");
		notNowButton = slidoAtlas.createSprite("notNowButton");
		rateUsButton = slidoAtlas.createSprite("rateUsButton");
		
		for (int i = 0; i < 11; i += 1) {
			String num = "" + (i + 1);
			while (num.length() < 4) {
				num = "0" + num;
			}
			System.out.println(num);
			yetiHappy[i] = slidoAtlas.createSprite("happyYeti" + num);
		}

		spike = slidoAtlas.createSprite("strip2");
		backButtonLong = slidoAtlas.createSprite("backButtonLong");
		exitButton = slidoAtlas.createSprite("exitButton");
		tree = slidoAtlas.createSprite("tree");
		gameMusic = Gdx.audio.newMusic(Gdx.files
				.internal("sounds/gameMusic.mp3"));
		snowBackground = slidoAtlas.createSprite("snowBack3");
		clickSound = Gdx.audio.newSound(Gdx.files
				.internal("sounds/buttonSoft.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files
				.internal("sounds/decapitatedSoft.wav"));
		scoreUp = Gdx.audio.newSound(Gdx.files
				.internal("sounds/pickUpSoft.wav"));
		loadFonts();
		WorldUtils.loadSpikeInfo();
	}

	private static void loadFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/gothic.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 65;
		parameter.color = Color.WHITE;
		parameter.borderWidth = 2f;
		parameter.borderColor = Color.BLACK;
		parameter.borderStraight = true;
		scoreFont = generator.generateFont(parameter);

		parameter.size = 40;
		parameter.color = Color.WHITE;
		parameter.borderWidth = 2f;
		parameter.borderColor = Color.BLACK;
		messageFont = generator.generateFont(parameter);

		parameter.size = 26;
		parameter.color = Color.WHITE;
		parameter.borderWidth = 1.5f;
		parameter.borderColor = Color.BLACK;
		parameter.borderStraight = false;
		headingFont = generator.generateFont(parameter);

		generator.dispose();

	}

	public static void playSound(Sound sound) {
		if (Settings.soundOn) {
			sound.play(0.5f);
		}
	}

	public static void playMusic() {
		if (gameMusic != null && !gameMusic.isPlaying()) {
			gameMusic.setLooping(true);
			gameMusic.setVolume(0.4f);
			gameMusic.play();
		}
	}

	public static void pauseMusic() {
		if (gameMusic != null && gameMusic.isPlaying()) {
			gameMusic.pause();
		}
	}

}
