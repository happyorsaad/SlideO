package com.fr.entities;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fr.entities.Spike.Side;
import com.fr.game.SkiFall;
import com.fr.utils.Assets;
import com.fr.utils.Settings;
import com.fr.utils.WorldUtils;
import com.fr.utils.WorldUtils.SpikeInfo;

public class GameWorld {
	public static final float WORLD_WIDTH = 15;
	public static final float WORLD_HEIGHT = 25;

	public enum WorldState {
		WAITING, RUNNING, GAME_OVER
	}

	public WorldState state;

	private final float TIME_STEP = 1 / 60f;
	private final int VELOCITY_ITERATIONS = 4;
	private final int POSITION_ITERATIONS = 2;

	public World world;
	public SkiFall gameRef;
	public Yeti yeti;
	public Array<Spike> spikes;

	public float accumulator;

	private final Vector2 YETI_LOCATION = new Vector2(2, 20);

	private int currentSpike, spikeToAdd;
	private float removeTime = 0;
	private final float REMOVE_INTERVAL = 3.0f;

	public long gameScore;

	public GameWorld(SkiFall gameRef) {
		this.gameRef = gameRef;
		this.world = new World(Vector2.Zero, true);
		this.world.setContactListener(new CollisionHandler());
		this.yeti = new Yeti(world, YETI_LOCATION);
		this.spikes = new Array<Spike>();
		if (!WorldUtils.simulate) {
			this.loadDefaultSpikes();
		}
		this.accumulator = 0.0f;
		this.state = WorldState.RUNNING;
		this.gameScore = 0;
	}

	public void addSpike(Spike spike) {
		spikes.add(spike);
	}

	private void loadDefaultSpikes() {
		/*
		 * Clear any spike currently loaded into the box2D world
		 */

		Array<Body> currentBodies = new Array<Body>();
		world.getBodies(currentBodies);
		for (Body body : currentBodies) {
			Object entity = body.getUserData();
			if (entity != null && entity instanceof Spike) {
				body.setUserData(null);
				world.destroyBody(body);
			}
		}

		// world.getBodies(currentBodies);
		// System.out.println("BOdies after cleanup "+currentBodies.size);

		if (spikes.size == 0) {
			for (int i = 0; i < 100; i += 1) {
				SpikeInfo info = WorldUtils.spikeInfo.get(i);
				Spike leftSpike = new Spike(world, info.x1, info.y, Side.LEFT);
				Spike rightSpike = new Spike(world, info.x2, info.y, Side.RIGHT);
				spikes.add(leftSpike);
				spikes.add(rightSpike);
			}
		} else {
			for (int i = 0, j = 0; i < spikes.size; i += 2, j += 1) {
				SpikeInfo info = WorldUtils.spikeInfo.get(j);
				Spike leftSpike = spikes.get(i);
				Spike rightSpike = spikes.get(i + 1);
				leftSpike.reset(info.x1, info.y, Side.LEFT);
				rightSpike.reset(info.x2, info.y, Side.RIGHT);
			}
		}
		// world.getBodies(currentBodies);
		// System.out.println("BOdies after adding spikes "+currentBodies.size);

		this.spikeToAdd = 100;
		this.currentSpike = 0;

	}

	public void update(float delta) {
		if (delta > 0.25f) {
			delta = TIME_STEP;
		}
		accumulator += delta;
		while (accumulator >= TIME_STEP) {
			world.clearForces();
			yeti.update(delta);
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			accumulator -= TIME_STEP;

			if (yeti.getPosition().x < -yeti.YETI_RADIUS
					|| yeti.getPosition().x > WORLD_WIDTH + yeti.YETI_RADIUS) {
				yeti.collideWithSpike();
			}

			if (yeti.isDead()) {
				Assets.playSound(Assets.hitSound);
				state = WorldState.GAME_OVER;
				Settings.addScore(gameScore);
				Settings.save();

				if (gameRef.actionResolver.getSignedInGPGS()) {
					gameRef.actionResolver.submitScoreGPGS(gameScore);
				}

				return;
			}

			SpikeInfo current = WorldUtils.spikeInfo.get(currentSpike);
			if (current.y > (yeti.getPosition().y - yeti.YETI_RADIUS - yeti.YETI_RADIUS / 10f)) {
				Assets.playSound(Assets.scoreUp);
				if (!WorldUtils.simulate) {
					gameScore += 1;
					currentSpike = Math.min(currentSpike + 1,
							WorldUtils.spikeInfo.size - 1);
				} else {
					gameScore = Math.min(gameScore + 1, 2000);
					currentSpike = Math.min(2000, currentSpike + 1);
				}
			}
		}

		if (!WorldUtils.simulate) {
			updateSpikeBodies(delta);
		}
	}

	private void updateSpikeBodies(float delta) {
		removeTime += delta;
		if (removeTime >= REMOVE_INTERVAL && spikes.size >= 2) {
			System.out.println("Remove");
			float max = Float.MIN_VALUE;
			int maxI = -1;
			Spike leftSpike = null, rightSpike = null;

			for (int i = 0; spikes.size >= 2 && i < spikes.size - 1; i += 1) {
				if (spikes.get(i).y > max) {
					max = spikes.get(i).y;
					maxI = i;
				}
			}

			if (maxI > -1) {
				leftSpike = spikes.get(maxI);
				rightSpike = spikes.get(maxI + 1);
			}

			if (leftSpike != null && rightSpike != null
					&& leftSpike.y > (yeti.getPosition().y + 10)) {
				leftSpike = spikes.removeIndex(maxI);
				rightSpike = spikes.removeIndex(maxI + 1);
				System.out.println(leftSpike.y + " " + yeti.getPosition().y);
				Body leftBody = leftSpike.spikeBody;
				Body rightBody = rightSpike.spikeBody;
				leftBody.setUserData(null);
				rightBody.setUserData(null);
				world.destroyBody(leftBody);
				world.destroyBody(rightBody);
				SpikeInfo info = WorldUtils.spikeInfo.get(spikeToAdd++);
				leftSpike = new Spike(world, info.x1, info.y, Side.LEFT);
				rightSpike = new Spike(world, info.x2, info.y, Side.RIGHT);
				spikes.add(leftSpike);
				spikes.add(rightSpike);
				Array<Body> currentBodies = new Array<Body>();
				world.getBodies(currentBodies);
				System.out
						.println("BOdies after removal " + currentBodies.size);
				System.out.println(spikes.size);
			}
			removeTime -= REMOVE_INTERVAL;
		}
	}

	public long getScore() {
		return gameScore;
	}

	public void reset() {
		gameScore = 0;
		loadDefaultSpikes();
		state = WorldState.RUNNING;
		currentSpike = 0;
		spikeToAdd = 100;
		removeTime = 0;
		yeti.reset();
	}
}
