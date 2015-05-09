package com.fr.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;
import com.fr.utils.Settings;

public class SettingScreen implements Screen {
	private SkiFall gameRef;
	private SpriteBatch batcher;
	private OrthographicCamera camera;
	private Rectangle signInButton;
	private Rectangle backButton;
	private Rectangle soundButton;
	private Rectangle rateUsButton;

	private ShapeRenderer shapeRenderer;

	private Vector3 touchPoint;

	private int numberBacks;

	public SettingScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.camera = new OrthographicCamera(480, 800);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		this.batcher = gameRef.batch;
		this.backButton = new Rectangle(20, 25, 70, 70);
		this.signInButton = new Rectangle(240 - 300 / 2, 290 - 75 / 2 + 80,
				300, 75);
		this.rateUsButton = new Rectangle(260 - 300 / 2, 90 - 75 / 2 + 120,
				250, 75);
		this.soundButton = new Rectangle(370 - 30, 600 - 30, 60, 60);
		this.touchPoint = new Vector3();
		this.shapeRenderer = new ShapeRenderer();
		this.numberBacks = 1;
	}

	@Override
	public void render(float delta) {
		updateScreen(delta);
		renderScreen(delta);
	}

	private void updateScreen(float delta) {
		if (gameRef.actionResolver.isConnecting()) {
			return;
		}
		if (Gdx.input.justTouched()) {
			this.camera.unproject(touchPoint.set(Gdx.input.getX(),
					Gdx.input.getY(), 0));
			if (backButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				numberBacks = (numberBacks + 1) % 2;
				if (numberBacks == 0) {
					gameRef.actionResolver.showOrLoadInterstital();
				}
				gameRef.setScreen(SkiFall.mainScreen);
			} else if (signInButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (!gameRef.actionResolver.getSignedInGPGS()) {
					gameRef.actionResolver.loginGPGS();
				} else {
					gameRef.actionResolver.signOutGPGS();
				}
			} else if (soundButton.contains(touchPoint.x, touchPoint.y)) {
				Settings.toggleSound();
				Assets.playSound(Assets.clickSound);
			} else if (rateUsButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				SkiFall.rateUsScreen.onDoneSwitch = SkiFall.settingScreen;
				gameRef.setScreen(SkiFall.rateUsScreen);
			}
		}

	}

	private void renderScreen(float delta) {
		GL20 gl = Gdx.gl;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batcher.begin();
		batcher.setProjectionMatrix(camera.combined);

		Sprite snow = Assets.snowBackground;
		snow.setPosition(0, 0);
		snow.setSize(480, 800);
		snow.draw(batcher);

		Sprite background = Assets.backgroundTint;
		background.setPosition(0, 0);
		background.setSize(480, 800);
		background.draw(batcher);

		String message = "SETTINGS";
		BitmapFont font = Assets.scoreFont;
		TextBounds bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				770 - bounds.height / 2);

		message = "SOUND";
		font = Assets.messageFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 150 - bounds.width / 2,
				630 - bounds.height / 2);

		if (!gameRef.actionResolver.getSignedInGPGS()) {
			message = "SIGN INTO GOOGLE";// TO SAVE SCORES";
			font = Assets.headingFont;
			bounds = font.getBounds(message);
			font.draw(batcher, message, 240 - bounds.width / 2,
					415 - bounds.height / 2 + 100);

			message = "PLAY TO SAVE SCORES";// "GOOGLE PLAY";
			font = Assets.headingFont;
			bounds = font.getBounds(message);
			font.draw(batcher, message, 240 - bounds.width / 2,
					375 - bounds.height / 2 + 100);

			// message = "TO SAVE SCORES";
			// font = Assets.messageFont;
			// bounds = font.getBounds(message);
			// font.draw(batcher, message, 240 - bounds.width / 2,
			// 335 - bounds.height / 2 + 100);

			Sprite signIn = Assets.signInButton;
			signIn.setPosition(signInButton.x, signInButton.y);
			signIn.setSize(signInButton.width, signInButton.height);
			signIn.draw(batcher);
			signIn.draw(batcher);

		} else {
			message = "SIGN OUT OF GOOGLE";
			font = Assets.headingFont;
			bounds = font.getBounds(message);
			font.draw(batcher, message, 240 - bounds.width / 2,
					415 - bounds.height / 2 + 100);

			message = "PLAY";
			font = Assets.headingFont;
			bounds = font.getBounds(message);
			font.draw(batcher, message, 240 - bounds.width / 2,
					365 - bounds.height / 2 + 100);

			Sprite signOut = Assets.signOutButton;
			signOut.setPosition(signInButton.x, signInButton.y);
			signOut.setSize(signInButton.width, signInButton.height);
			signOut.draw(batcher);
			signOut.draw(batcher);

		}

		if (!gameRef.actionResolver.hasRated()) {
			Sprite rateUs = Assets.rateUsButton;
			rateUs.setPosition(rateUsButton.x, rateUsButton.y);
			rateUs.setSize(rateUsButton.width, rateUsButton.height);
			rateUs.draw(batcher);
			rateUs.draw(batcher);
		}

		Sprite back = Assets.backButton;
		back.setPosition(backButton.x, backButton.y);
		back.setSize(backButton.width, backButton.height);
		back.draw(batcher);
		back.draw(batcher);

		Sprite sound = Settings.soundOn ? Assets.soundOnLogo
				: Assets.soundOffLogo;
		sound.setPosition(soundButton.x, soundButton.y);
		sound.setSize(soundButton.width, soundButton.height);
		sound.draw(batcher);
		sound.draw(batcher);

		batcher.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rectLine(20, 650, 460, 650, 1.0f);
		shapeRenderer.rectLine(20, 450 + 100, 460, 450 + 100, 1.0f);
		if (!gameRef.actionResolver.hasRated()) {
			shapeRenderer.rectLine(20, 280, 460, 280, 1.0f);
		}
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {
		Settings.save();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}
