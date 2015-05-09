package com.fr.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;

public class SplashScreen extends ScreenAdapter {

	private SkiFall gameRef;
	private float time;
	private final float SPLASH_TIME = 3f;

	private OrthographicCamera cam;
	
	SpriteBatch batcher;

	public SplashScreen(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.time = 0;
		this.cam = new OrthographicCamera(480, 800);
		this.cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		this.cam.update();
		this.batcher = gameRef.batch;
	}

	@Override
	public void render(float delta) {
		updateScreen(delta);
		renderScreen(delta);
	}

	private void updateScreen(float delta) {
		time += delta;
		if (time >= SPLASH_TIME) {
			gameRef.setScreen(new MainScreen(gameRef));
		}
	}

	private void renderScreen(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batcher.begin();
		batcher.setProjectionMatrix(cam.combined);
		Sprite logoSprite = Assets.frLogo;
		logoSprite.setPosition(cam.viewportWidth / 2 - 50,
				cam.viewportHeight / 2 - 30);
		logoSprite.setSize(100, 60);
		logoSprite.draw(batcher);
		batcher.end();
	}

}
