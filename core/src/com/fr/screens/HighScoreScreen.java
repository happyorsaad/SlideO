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

public class HighScoreScreen implements Screen {
	SkiFall gameRef;
	SpriteBatch batcher;
	OrthographicCamera camera;
	Rectangle backButton;
	Rectangle leaderBoardButton;

	Vector3 touchPoint;
	private ShapeRenderer shapeRenderer;

	private boolean getLeaderBoard;
	public Screen onDoneScreen;

	private int numberBacks;

	public HighScoreScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.camera = new OrthographicCamera(480, 800);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		this.batcher = gameRef.batch;
		this.backButton = new Rectangle(20, 25, 70, 70);
		this.touchPoint = new Vector3();
		this.shapeRenderer = new ShapeRenderer();
		this.leaderBoardButton = new Rectangle(240 - 300 / 2, 170, 300, 75);
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

		if (gameRef.actionResolver.getSignedInGPGS() && getLeaderBoard) {
			getLeaderBoard = false;
			gameRef.actionResolver.getLeaderboardGPGS();
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
				gameRef.setScreen(onDoneScreen);
			} else if (leaderBoardButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (!gameRef.actionResolver.getSignedInGPGS()) {
					getLeaderBoard = true;
					gameRef.actionResolver.getLeaderboardGPGS();
				} else {
					gameRef.actionResolver.getLeaderboardGPGS();
				}
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

		String message = "HIGHSCORES*";
		BitmapFont font = Assets.messageFont;
		TextBounds bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				770 - bounds.height / 2);

		for (int i = 0, height = 650; i < Settings.highscores.length; i += 1, height -= 60) {
			String text = Settings.highscores[i] + "";
			while (text.length() <= 12) {
				text = " " + text;
			}
			String num = (i + 1) + ".";
			TextBounds textBounds = font.getBounds(text);
			font.draw(batcher, text, 400f - (textBounds.width), height
					- (textBounds.height / 2f));
			TextBounds numBounds = font.getBounds(num);
			font.draw(batcher, num, 80, height - (numBounds.height / 2f));
		}

		message = "*SCORES WONT BE ";
		font = Assets.headingFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				300 - bounds.height / 2 + 100);

		message = "SAVED UNTIL YOU LOG IN";
		font = Assets.headingFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				270 - bounds.height / 2 + 100);

		message = "CLICK TO SIGN IN";
		font = Assets.headingFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				230 - bounds.height / 2 + 100);

		message = "AND VIEW LEADERBOARD";
		font = Assets.headingFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				200 - bounds.height / 2 + 100);

		Sprite back = Assets.backButton;
		back.setPosition(backButton.x, backButton.y);
		back.setSize(backButton.width, backButton.height);
		back.draw(batcher);

		Sprite leader = Assets.leaderBoardLong;
		leader.setPosition(leaderBoardButton.x, leaderBoardButton.y);
		leader.setSize(leaderBoardButton.width, leaderBoardButton.height);
		leader.draw(batcher);

		batcher.end();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rectLine(20, 425, 460, 425, 1.0f);
		shapeRenderer.rectLine(20, 670, 460, 670, 1.0f);
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		this.getLeaderBoard = false;
	}

	@Override
	public void hide() {

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
