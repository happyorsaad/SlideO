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

public class HelpScreen implements Screen {
	SkiFall gameRef;
	SpriteBatch batcher;
	OrthographicCamera camera;
	Rectangle doneButton;
	Vector3 touchPoint;
	public Screen onDoneScreen;

	public HelpScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.camera = new OrthographicCamera(480, 800);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		this.batcher = gameRef.batch;
		this.doneButton = new Rectangle(240 - 250 / 2, 150 - 75 / 2, 250, 75);
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
			if (doneButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				gameRef.actionResolver.showOrLoadInterstital();
				gameRef.setScreen(onDoneScreen);
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

		String message = "HELP!";
		BitmapFont font = Assets.scoreFont;
		TextBounds bounds = font.getBounds(message);
		font.draw(batcher, message, 240 - bounds.width / 2,
				770 - bounds.height / 2);

		font = Assets.messageFont;
		String helpMessage[] = { "TAP TO SWITCH", " SLIDING DIRECTION",
				"AND MIND THE", " SPIKES" };
		for (int i = 0, height = 600; i < helpMessage.length; i += 1, height -= 80) {
			String text = helpMessage[i];
			TextBounds numBounds = font.getBounds(text);
			font.draw(batcher, text, 240 - (numBounds.width / 2f), height
					- (numBounds.height / 2f));
		}

		Sprite back = Assets.gotItButton;
		back.setPosition(doneButton.x, doneButton.y);
		back.setSize(doneButton.width, doneButton.height);
		back.draw(batcher);
		batcher.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		gameRef.actionResolver.showOrLoadInterstital();
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
