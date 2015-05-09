package com.fr.entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.fr.entities.GameWorld.WorldState;
import com.fr.entities.Spike.Side;
import com.fr.utils.Assets;

public class GameRenderer {
	GameWorld world;
	Box2DDebugRenderer renderer;
	OrthographicCamera camera;
	SpriteBatch batcher;
	float time = 0, bloodTime = 0;
	int index = 0;
	private float ANIMATION_RATE = 0.10f;
	private float ENVIRONMENT_REFRESH = 1.0f;
	private float LOCATION_SAMPLE_RATE = 0.15f;
	ShapeRenderer shapeRenderer;

	float treeRefreshTime = 0;
	float propY = 50;
	int propIndex = 0;
	float locationSampleTime = 0;
	private int NUM_PROPS = 250;

	enum PropType {
		TREE_ONE(2), TREE_TWO(0.5f), TREE_THREE(2f), TREE_FOUR(0.5f), TREE_FIVE(
				0.5f), TREE_SIX(0.5f), ROCK_ONE(1f), SHRUB_ONE(0.8f), SNOW_MAN(
				0f), LOG(1.5f), PILLAR(1f);
		private final float radius;

		private PropType(float radius) {
			this.radius = radius;
		}

		public float getRadius() {
			return radius;
		}
	}

	private PropType getType() {
		float p = (float) Math.random();
		if (p > 0.0f && p <= 0.45f) {
			return PropType.TREE_ONE;
		} else if (p > 0.45f && p <= 0.65f) {
			return PropType.ROCK_ONE;
		} else if (p > 0.65f && p < 0.95) {
			return PropType.SHRUB_ONE;
		} else if (p > 0.95f) {
			return PropType.LOG;
		}
		return PropType.TREE_ONE;
	}

	class Prop {
		public PropType type;
		public Vector2 location;
	}

	ArrayList<Prop> propLocations;
	Array<Vector2> yetiLocations;

	Random randomGenarator;
	private OrthographicCamera hudCam;

	public GameRenderer(GameWorld world) {
		this.world = world;
		this.renderer = new Box2DDebugRenderer(true, false, false, false, true,
				true);
		this.camera = new OrthographicCamera(GameWorld.WORLD_WIDTH,
				GameWorld.WORLD_HEIGHT);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		this.batcher = world.gameRef.batch;
		this.propLocations = new ArrayList<Prop>(NUM_PROPS);
		this.randomGenarator = new Random();
		this.yetiLocations = new Array<Vector2>();
		for (int i = 0; i < 20; i += 1) {
			yetiLocations.add(new Vector2(-1, 0));
		}
		shapeRenderer = new ShapeRenderer();
		loadPropLocations();
		this.hudCam = new OrthographicCamera(480, 800);
		this.hudCam.position.set(this.hudCam.viewportWidth / 2f,
				this.hudCam.viewportHeight / 2f, 0);
		this.hudCam.update();
	}

	private void loadPropLocations() {
		for (int i = 0; i < NUM_PROPS; i += 1) {
			Prop prop = new Prop();
			prop.type = getType();
			Vector2 location = new Vector2();
			float radius = prop.type.getRadius();
			float p = (float) Math.random();
			if (p < 0.5f) {
				location.x = (float) (-radius * 0.70f);
			} else {
				location.x = (float) (GameWorld.WORLD_WIDTH - radius * 0.30f);
			}
			location.y = propY;
			propY -= 1.5f * radius;
			prop.location = location;
			propLocations.add(prop);
		}
	}

	private void resetPropLocations() {
		for (int i = 0; i < propLocations.size(); i += 1) {
			Prop prop = propLocations.get(i);
			prop.type = getType();
			float radius = prop.type.getRadius();
			float p = (float) Math.random();
			if (p < 0.5f) {
				prop.location.x = (float) (-radius * 0.70f);
			} else {
				prop.location.x = (float) (GameWorld.WORLD_WIDTH - radius * 0.30f);
			}
			prop.location.y = propY;
			propY -= 1.5f * radius;
		}
	}

	public void render(float delta) {
		camera.position.set(camera.viewportWidth / 2f,
				world.yeti.getPosition().y - (camera.viewportHeight / 6f), 0);
		camera.update();
		time += delta;

		batcher.setProjectionMatrix(hudCam.combined);
		batcher.begin();
		Sprite snow = Assets.snowBackground;
		snow.setPosition(0, 0);
		snow.setSize(480, 800);
		snow.draw(batcher);

		Sprite background = Assets.backgroundTint;
		background.setPosition(0, 0);
		background.setSize(480, 800);
		background.draw(batcher);

		batcher.end();
		renderSpikes(delta);
		renderEnvironment(delta);
		renderTrail(delta);
		renderYeti(delta);
//		 renderer.render(world.world, camera.combined);
	}

	private void renderEnvironment(float delta) {
		treeRefreshTime += delta;
		if (treeRefreshTime > ENVIRONMENT_REFRESH
				&& world.state == WorldState.RUNNING) {
			treeRefreshTime -= ENVIRONMENT_REFRESH;
			for (int i = 1; i <= 4; i += 1) {
				Prop prop = propLocations.get(propIndex);
				Vector2 location = prop.location;
				Vector2 yetiLocation = world.yeti.getPosition();
				if (location.y > yetiLocation.y + GameWorld.WORLD_HEIGHT) {
					float radius = prop.type.getRadius();
					float p = (float) Math.random();
					if (p < 0.5f) {
						location.x = (float) (-radius * 0.70f);
					} else {
						location.x = (float) (GameWorld.WORLD_WIDTH - radius * 0.30f);
					}
					location.y = propY;
					propY -= 1.5f * radius;
					propIndex = (propIndex + 1) % propLocations.size();
				}
			}
		}

		batcher.begin();
		batcher.setProjectionMatrix(camera.combined);

		for (int i = 0; i < propLocations.size(); i += 1) {
			Prop prop = propLocations.get(i);
			Vector2 location = prop.location;
			Sprite propSprite = null;
			propSprite = Assets.tree;
			float radius = prop.type.getRadius();
			if (Math.abs(radius) < GameWorld.WORLD_WIDTH * 0.2f) {
				propSprite.setSize(radius, radius);
				propSprite.setPosition(location.x, location.y);
				propSprite.draw(batcher);
			}
		}
		batcher.end();
	}

	private void renderSpikes(float delta) {
		batcher.begin();
		batcher.setProjectionMatrix(camera.combined);
		Sprite spikeSprite = Assets.spike;
		for (int i = 0; i < world.spikes.size; i += 1) {
			Spike spike = world.spikes.get(i);
			float width = GameWorld.WORLD_WIDTH * 0.85f;
			if (spike.side == Side.LEFT) {
				spikeSprite.setPosition(spike.x - width, spike.y - 0.1f);
			} else {
				spikeSprite.setPosition(spike.x, spike.y - 0.1f);
			}
			spikeSprite.setSize(width, 0.35f);
			spikeSprite.draw(batcher);
		}
		batcher.end();
	}

	private void renderTrail(float delta) {
		locationSampleTime += delta;
		if (locationSampleTime > LOCATION_SAMPLE_RATE) {
			locationSampleTime -= LOCATION_SAMPLE_RATE;
			for (int i = 0; i < yetiLocations.size - 1; i += 1) {
				yetiLocations.get(i).x = yetiLocations.get(i + 1).x;
				yetiLocations.get(i).y = yetiLocations.get(i + 1).y;
			}

			yetiLocations.get(yetiLocations.size - 1).x = world.yeti
					.getPosition().x;
			yetiLocations.get(yetiLocations.size - 1).y = world.yeti
					.getPosition().y;
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(camera.combined);

		for (int i = 0; i < yetiLocations.size - 1; i += 1) {
			float prevX = yetiLocations.get(i).x;
			float prevY = yetiLocations.get(i).y;
			float X = yetiLocations.get(i + 1).x;
			float Y = yetiLocations.get(i + 1).y;
			if (prevX > 0 && X > 0) {
				shapeRenderer.begin(ShapeType.Line);
				Gdx.gl.glLineWidth(15);
				shapeRenderer.setColor(35f / 255f, 206f / 255f, 250f / 255f, i
						* 0.3f / yetiLocations.size);
				shapeRenderer.line(prevX, prevY, X, Y);
				shapeRenderer.end();
				shapeRenderer.begin(ShapeType.Line);
				Gdx.gl.glLineWidth(3);
				shapeRenderer.line(prevX, prevY, X, Y,
						new Color(1, 1, 1, 0.6f), new Color(1, 1, 1, 0.6f));
				shapeRenderer.end();
			}
		}
	}

	private void renderYeti(float delta) {
		batcher.begin();
		batcher.setProjectionMatrix(camera.combined);

		if (time > ANIMATION_RATE) {
			index = (index + 1) % 11;
			time -= ANIMATION_RATE;
		}

		if (world.yeti.isDead()) {
			bloodTime += delta;
			Sprite blood = Assets.blood;
			float bloodWidth = Math.min(world.yeti.YETI_RADIUS,
					world.yeti.YETI_RADIUS * bloodTime);
			blood.setPosition(world.yeti.getPosition().x,
					world.yeti.getPosition().y);
			blood.setSize(bloodWidth, bloodWidth);
			blood.draw(batcher);
		}

		Sprite yeti = world.yeti.isDead() ? Assets.yetiDead
				: Assets.yetiHappy[index];
		yeti.setRotation(90);
		yeti.setSize(2 * world.yeti.YETI_RADIUS, 2 * world.yeti.YETI_RADIUS);
		yeti.setOrigin(yeti.getWidth() / 2, yeti.getHeight() / 2);
		yeti.rotate(world.yeti.getAngle());
		yeti.setPosition(world.yeti.getPosition().x - world.yeti.YETI_RADIUS,
				world.yeti.getPosition().y - world.yeti.YETI_RADIUS);
		yeti.draw(batcher);

		batcher.end();
	}

	public void reset() {
		propIndex = 0;
		propY = 50;
		resetPropLocations();
		for (Vector2 loc : yetiLocations) {
			loc.x = -1;
		}
	}
}
