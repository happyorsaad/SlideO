package com.fr.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;

public class RateUsScreen implements Screen {
	private SkiFall gameRef;
	private SpriteBatch batcher;
	private OrthographicCamera camera;
	private Rectangle signInButton;
	private Rectangle skipButton;

	private Vector3 touchPoint;

	public Screen onDoneSwitch;

	public RateUsScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.camera = new OrthographicCamera(480, 800);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		this.batcher = gameRef.batch;
		this.signInButton = new Rectangle(240 - 290 / 2, 350 - 75 / 2, 290, 75);
		this.skipButton = new Rectangle(240 - 290 / 2, 250 - 75 / 2, 290, 75);
		this.touchPoint = new Vector3();
	}

	@Override
	public void render(float delta) {
		updateScreen(delta);
		renderScreen(delta);
	}

	private void updateScreen(float delta) {
		if (Gdx.input.justTouched()) {
			this.camera.unproject(touchPoint.set(Gdx.input.getX(),
					Gdx.input.getY(), 0));
			if (signInButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				gameRef.actionResolver.setRated();
				gameRef.setScreen(onDoneSwitch);
				Gdx.net.openURI("");
			} else if (skipButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				gameRef.setScreen(onDoneSwitch);
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

		String message = "RATE THE GAME";
		BitmapFont font = Assets.messageFont;
		TextBounds bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				700 - bounds.height / 2 - 100);

		message = "ON GOOGLE PLAY";
		font = Assets.messageFont;
		bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				650 - bounds.height / 2 - 100);

		Sprite signin = Assets.sureButton;
		signin.setPosition(signInButton.x, signInButton.y);
		signin.setSize(signInButton.width, signInButton.height);
		signin.draw(batcher);

		Sprite skip = Assets.notNowButton;
		skip.setPosition(skipButton.x, skipButton.y);
		skip.setSize(skipButton.width, skipButton.height);
		skip.draw(batcher);

		batcher.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

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
