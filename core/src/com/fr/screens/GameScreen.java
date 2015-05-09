package com.fr.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.fr.entities.GameRenderer;
import com.fr.entities.GameWorld;
import com.fr.entities.GameWorld.WorldState;
import com.fr.entities.Spike;
import com.fr.entities.Spike.Side;
import com.fr.entities.Yeti.YETI_STEER;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;
import com.fr.utils.Settings;
import com.fr.utils.WorldUtils;

public class GameScreen implements Screen {
	private enum GameState {
		WAITING, RUNNING, PAUSED, GAMEOVER
	}

	SkiFall gameRef;
	GameWorld world;
	GameRenderer renderer;
	GameState state;
	SpriteBatch batcher;

	private Vector3 touchPoint;
	private Rectangle playButton;
	private Rectangle soundButton;
	private Rectangle pauseButton;
	private Rectangle replayButton;
	private Rectangle backButton;
	private Rectangle exitButton;
	private Rectangle leaderBoardButton;

	private OrthographicCamera hudCam;

	private float bloodTime = 0;

	private float SWITCH_FREQUENCY;
	private float SAMPLE_FREQUENCY;

	private float switchTime, sampleTime;

	private float switch_time[] = { 0.5f, 1.0f, 1.20f, 1.35f, 1.25f, 1.45f,
			1.5f, 1.75f, 2.0f, 2.5f, 3.0f, 0.75f, 1.25f, 1.75f, 1.85f, 1.90f,
			2.00f, 2.25f, 2.35f, 2.50f, 2.40f, 2.65f };
	private float sample_time[] = { 1.40f, 1.45f, 1.5f, 1.7f, 1.65f, 1.40f,
			1.6f, 1.45f, 1.8f, 1.5f, 1.75f, 2.0f };

	private FileHandle handle;

	private Random random;

	int num;
	int numLoc = 0;

	public GameScreen(SkiFall game) {
		this.gameRef = game;
		this.numberReplays = 1;
		this.numberBacks = 1;
	}

	Vector2 lastLocation;

	private int numberReplays;
	private int numberBacks;

	private int getRandomIndex(int len) {
		int num = 1 + random.nextInt(4);
		int avg = 0;
		for (int i = 0; i < num; i += 1) {
			avg += random.nextInt(len);
		}
		return avg / num;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateScreen(delta);
		this.renderer.render(delta);
		renderScreen(delta);
	}

	private void updateScreen(float delta) {
		switch (state) {
		case GAMEOVER:
			updateGameover(delta);
			break;
		case RUNNING:
			updateRunning(delta);
			break;
		case PAUSED:
			updatePaused(delta);
			break;
		case WAITING:
			updateWaiting(delta);
			break;
		}
	}

	private void updateGameover(float delta) {
		if (Gdx.input.justTouched()) {
			this.hudCam.unproject(touchPoint.set(Gdx.input.getX(),
					Gdx.input.getY(), 0));
			if (backButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				numberBacks = (numberReplays + 1) % 2;
				if (numberBacks == 0) {
					gameRef.actionResolver.showOrLoadInterstital();
				}
				gameRef.setScreen(SkiFall.mainScreen);
			} else if (replayButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				numberReplays = (numberReplays + 1) % 101;
				System.out.println(numberReplays);
				if (numberReplays % 7 == 0) {
					gameRef.actionResolver.showOrLoadInterstital();
				}
				if (numberReplays % 10 == 0 && numberReplays % 7 != 0
						&& !gameRef.actionResolver.hasRated()
						&& gameRef.actionResolver.isNetConnected()) {
					SkiFall.rateUsScreen.onDoneSwitch = SkiFall.gameScreen;
					gameRef.setScreen(SkiFall.rateUsScreen);
					return;
				}
				resetGame();
			} else if (leaderBoardButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (!gameRef.actionResolver.getSignedInGPGS()) {
					SkiFall.highScoresScreen.onDoneScreen = SkiFall.gameScreen;
					gameRef.setScreen(SkiFall.highScoresScreen);
				} else {
					gameRef.actionResolver.getLeaderboardGPGS();
				}
			}
		}
	}

	private void resetGame() {
		world.reset();
		renderer.reset();
		bloodTime = 0;
		state = GameState.WAITING;
	}

	private void updatePaused(float delta) {
		if (Gdx.input.justTouched()) {
			hudCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					0));
			if (playButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				state = GameState.RUNNING;
				return;
			} else if (exitButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				gameRef.setScreen(SkiFall.mainScreen);
			}
		}
	}

	private void updateRunning(float delta) {

		if (WorldUtils.simulate) {
			if (numLoc % 10 == 0) {
				lastLocation.x = world.yeti.getPosition().x;
				lastLocation.y = world.yeti.getPosition().y;
				numLoc = 0;
			}

			numLoc += 1;

			sampleTime += delta;
			switchTime += delta;

			if (world.yeti.isDead()) {
				return;
			}

			if ((world.yeti.getPosition().x <= 3f && world.yeti
					.getCurrentSteer() == YETI_STEER.MOVE_LEFT)
					|| (world.yeti.getPosition().x >= 12f && world.yeti
							.getCurrentSteer() == YETI_STEER.MOVE_RIGHT)
					|| switchTime > SWITCH_FREQUENCY) {
				switchTime -= SWITCH_FREQUENCY;
				world.yeti.switchMove();
				SWITCH_FREQUENCY = switch_time[getRandomIndex(switch_time.length)];
			}

			world.update(delta);

			if (sampleTime > SAMPLE_FREQUENCY
					&& world.getScore() < 10 * 10 * 10 * 10 * 10 * 10 * 10 * 10) {
				sampleTime -= SAMPLE_FREQUENCY;
				float x = world.yeti.getPosition().x;
				float y = world.yeti.getPosition().y;
				if (world.getScore() <= 5) {
					delta = 1.6f;
				} else if (world.getScore() <= 10) {
					delta = 1.5f;
				} else if (world.getScore() <= 20) {
					delta = 1.40f;
				} else if (world.getScore() <= 50) {
					delta = 1.38f;
				} else if (world.getScore() <= 150) {
					delta = 1.35f;
				} else {
					delta = 1.325f;
				}

				if (world.spikes.size > 6) {
					Spike leftSpike = world.spikes.removeIndex(0);
					Spike rightSpike = world.spikes.removeIndex(1);
					Body leftBody = leftSpike.spikeBody;
					Body rightBody = rightSpike.spikeBody;
					leftBody.setUserData(null);
					rightBody.setUserData(null);
					world.world.destroyBody(leftBody);
					world.world.destroyBody(rightBody);
				}

				world.addSpike(new Spike(world.world, x - delta, y, Side.LEFT));
				world.addSpike(new Spike(world.world, x + delta, y, Side.RIGHT));
				System.out.println(world.spikes.size);
				handle.writeString(++num + " " + (x - 1.5f) + " " + (x + 1.5f)
						+ " " + y + "\n", true);
				SAMPLE_FREQUENCY = sample_time[getRandomIndex(sample_time.length)];

				if (world.getScore() > 100) {
					SAMPLE_FREQUENCY = SAMPLE_FREQUENCY * 0.90f;
				} else if (world.getScore() > 500) {
					SAMPLE_FREQUENCY = SAMPLE_FREQUENCY * 0.85f;
				}

			}
		} else {
			if (Gdx.input.justTouched()) {
				hudCam.unproject(touchPoint.set(Gdx.input.getX(),
						Gdx.input.getY(), 0));
				if (pauseButton.contains(touchPoint.x, touchPoint.y)) {
					state = GameState.PAUSED;
					return;
				} else if (soundButton.contains(touchPoint.x, touchPoint.y)) {
					Assets.playSound(Assets.clickSound);
					Settings.toggleSound();
				} else {
					this.world.yeti.switchMove();
				}
			}
			this.world.update(delta);
			if (world.state == WorldState.GAME_OVER)
				state = GameState.GAMEOVER;
		}
	}

	private void updateWaiting(float delta) {
		if (Gdx.input.justTouched()) {
			this.hudCam.unproject(touchPoint.set(Gdx.input.getX(),
					Gdx.input.getY(), 0));
			if (soundButton.contains(touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Settings.toggleSound();
			} 
		}
		else {
			state = GameState.RUNNING;
		}
	}

	private void renderScreen(float delta) {
		switch (state) {
		case WAITING:
			renderWaiting(delta);
			break;
		case RUNNING:
			renderRunning(delta);
			break;
		case PAUSED:
			renderPaused(delta);
			break;
		case GAMEOVER:
			renderGameover(delta);
			break;
		}
	}

	private void renderGameover(float delta) {
		bloodTime += delta;
		batcher.begin();
		batcher.setProjectionMatrix(hudCam.combined);

		Sprite deadMessagge = Assets.yetiDeadMessage;
		deadMessagge.setPosition(240 - 200, 600 - 40);
		deadMessagge.setSize(400, 80);
		deadMessagge.draw(batcher);

		Sprite blood = Assets.blood;
		float bloodWidth = Math.min(160, 160 * bloodTime);
		blood.setPosition(240 - bloodWidth / 2, 460 - bloodWidth / 2);
		blood.setSize(bloodWidth, bloodWidth);
		blood.draw(batcher);

		Sprite deadAlinu = Assets.yetiDead;
		deadAlinu.setRotation(0);
		deadAlinu.setPosition(240 - 75, 460 - 75);
		deadAlinu.setSize(150, 150);
		deadAlinu.draw(batcher);

		Sprite scoreBase = Assets.scoreBackground;
		scoreBase.setPosition(240 - 175, 350 - 72);
		scoreBase.setSize(350, 80);
		scoreBase.draw(batcher);

		BitmapFont font = Assets.scoreFont;
		String message = "" + world.getScore();
		TextBounds textBound = font.getBounds(message);
		font.draw(batcher, message, 240 - (textBound.width / 2f), 348);

		font = Assets.messageFont;
		if (world.getScore() < Settings.highscores[0]) {
			message = "BEST : " + Settings.highscores[0];
		} else {
			message = "BEST SCORE !";
		}
		textBound = font.getBounds(message);
		font.draw(batcher, message, 240 - (textBound.width / 2f),
				120 - (textBound.height / 2f));

		Sprite back = Assets.backButton;
		back.setPosition(backButton.x, backButton.y);
		back.setSize(backButton.width, backButton.height);
		back.draw(batcher);

		Sprite replay = Assets.replayButton;
		replay.setPosition(replayButton.x, replayButton.y);
		replay.setSize(replayButton.width, replayButton.height);
		replay.draw(batcher);

		Sprite leaderboard = Assets.leaderboardRed;
		leaderboard.setPosition(leaderBoardButton.x, leaderBoardButton.y);
		leaderboard.setSize(leaderBoardButton.width, leaderBoardButton.height);
		leaderboard.draw(batcher);

		batcher.end();
	}

	private void renderPaused(float delta) {
		batcher.begin();
		batcher.setProjectionMatrix(hudCam.combined);

		Sprite play = Assets.playButton;
		play.setPosition(playButton.x, playButton.y);
		play.setSize(playButton.width, playButton.height);
		play.draw(batcher);

		Sprite exit = Assets.exitButton;
		exit.setPosition(exitButton.x, exitButton.y);
		exit.setSize(exitButton.width, exitButton.height);
		exit.draw(batcher);

		batcher.end();
	}

	private void renderRunning(float delta) {
		batcher.begin();
		batcher.setProjectionMatrix(hudCam.combined);

		Sprite pause = Assets.pauseButton;
		pause.setPosition(pauseButton.x, pauseButton.y);
		pause.setSize(pauseButton.width, pauseButton.height);
		pause.draw(batcher);

		BitmapFont font = Assets.scoreFont;
		String message = "" + world.getScore();
		TextBounds textBound = font.getBounds(message);
		font.draw(batcher, message, 240 - (textBound.width / 2f), 720);

		Sprite sound = Settings.soundOn ? Assets.soundOnLogo
				: Assets.soundOffLogo;
		sound.setPosition(soundButton.x, soundButton.y);
		sound.setSize(soundButton.width, soundButton.height);
		sound.draw(batcher);
		batcher.end();
	}

	private void renderWaiting(float delta) {
		batcher.begin();
		batcher.setProjectionMatrix(hudCam.combined);

		Sprite play = Assets.playButton;
		play.setPosition(playButton.x, playButton.y);
		play.setSize(playButton.width, playButton.height);
		play.draw(batcher);

		String message = "Tap To Play";
		BitmapFont font = Assets.messageFont;
		TextBounds textBound = font.getBounds(message);
		font.draw(batcher, message, 240 - (textBound.width / 2f), 300);

		Sprite sound = Settings.soundOn ? Assets.soundOnLogo
				: Assets.soundOffLogo;
		sound.setPosition(soundButton.x, soundButton.y);
		sound.setSize(soundButton.width, soundButton.height);
		sound.draw(batcher);

		batcher.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		this.world = new GameWorld(gameRef);
		this.renderer = new GameRenderer(world);
		this.state = GameState.WAITING;
		this.hudCam = new OrthographicCamera(480, 800);
		this.hudCam.position.set(this.hudCam.viewportWidth / 2f,
				this.hudCam.viewportHeight / 2f, 0);
		this.hudCam.update();
		this.playButton = new Rectangle(240 - 35, 400 - 35, 70, 70);
		this.soundButton = new Rectangle(20, 25, 70, 70);
		this.pauseButton = new Rectangle(380, 25, 70, 70);

		this.replayButton = new Rectangle(240 - 45, 160, 90, 90);
		this.leaderBoardButton = new Rectangle(240 + 45 + 20, 160 + 10, 70, 70);
		this.backButton = new Rectangle(240 - 60 - 75, 160 + 10, 70, 70);

		this.exitButton = new Rectangle(240 - 250 / 2, 100 - 75 / 2, 250, 75);
		this.touchPoint = new Vector3();
		this.batcher = gameRef.batch;
		this.handle = Gdx.files.local("data/level.txt");
		this.random = new Random();
		this.random.setSeed(2);

		SWITCH_FREQUENCY = 1.4f;
		SAMPLE_FREQUENCY = 2.0f;

		lastLocation = new Vector2();
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
