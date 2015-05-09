package com.fr.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;
import com.fr.utils.Settings;

public class MainScreen extends ScreenAdapter {
	SkiFall gameRef;
	OrthographicCamera cam;
	SpriteBatch batcher;
	Rectangle letsSlide;
	Rectangle highScores;
	Rectangle settingsButton;
	Rectangle helpButton;
	Vector3 touchPoint;

	public MainScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.batcher = gameRef.batch;
		this.cam = new OrthographicCamera(480, 800);
		this.cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		this.cam.update();
		this.touchPoint = new Vector3();
		this.letsSlide = new Rectangle(90, 160, 300, 75);
		this.highScores = new Rectangle(20 + 80, 25, 70, 70);
		this.settingsButton = new Rectangle(480 - 20 - 70 - 80, 25, 70, 70);
		this.helpButton = new Rectangle(240 - 35, 25, 70, 70);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		updateScreen(delta);
		renderScreen(delta);
	}

	private void updateScreen(float delta) {
		if (Gdx.input.justTouched()) {
			this.cam.unproject(touchPoint.set(Gdx.input.getX(),
					Gdx.input.getY(), 0));
			if (letsSlide.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (gameRef.actionResolver.isFirstTimeUser()
						&& !gameRef.actionResolver.getSignedInGPGS()) {
					gameRef.setScreen(new SignInScreen(gameRef));
				} else {
					gameRef.setScreen(SkiFall.gameScreen);
				}
			} else if (highScores.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (gameRef.actionResolver.getSignedInGPGS()) {
					gameRef.actionResolver.getLeaderboardGPGS();
				} else {
					SkiFall.highScoresScreen.onDoneScreen = SkiFall.mainScreen;
					gameRef.setScreen(SkiFall.highScoresScreen);
				}
			} else if (settingsButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				gameRef.setScreen(SkiFall.settingScreen);
			} else if (helpButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				SkiFall.helpScreen.onDoneScreen = SkiFall.mainScreen;
				gameRef.setScreen(SkiFall.helpScreen);
			}
		}
	}

	private void renderScreen(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batcher.begin();
		batcher.setProjectionMatrix(cam.combined);

		Sprite snow = Assets.snowBackground;
		snow.setPosition(0, 0);
		snow.setSize(480, 800);
		snow.draw(batcher);

		Sprite background = Assets.backgroundTint;
		background.setPosition(0, 0);
		background.setSize(480, 800);
		background.draw(batcher);

		Sprite logo = Assets.slidoLogo;
		logo.setPosition(15, 400);
		logo.setSize(450, 350);
		logo.draw(batcher);

		Sprite slide = Assets.letsSlide;
		slide.setPosition(letsSlide.x, letsSlide.y);
		slide.setSize(letsSlide.width, letsSlide.height);
		slide.draw(batcher);

		Sprite hs = Assets.leaderBoardGreen;
		hs.setPosition(highScores.x, highScores.y);
		hs.setSize(highScores.width, highScores.height);
		hs.draw(batcher);

		Sprite sound = Assets.settingButton;
		sound.setPosition(settingsButton.x, settingsButton.y);
		sound.setSize(settingsButton.width, settingsButton.height);
		sound.draw(batcher);

		Sprite help = Assets.helpButton;
		help.setPosition(helpButton.x, helpButton.y);
		help.setSize(helpButton.width, helpButton.height);
		help.draw(batcher);

		batcher.end();
	}
}
