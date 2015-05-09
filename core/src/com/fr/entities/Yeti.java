package com.fr.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.fr.utils.Assets;

public class Yeti {
	public final float YETI_RADIUS = 0.75f;
	public final float YETI_DENSITY = 5f;
	private boolean isDead = false;
	int numberSwitch = 0;
	private YETI_STEER currentSteer = YETI_STEER.MOVE_RIGHT;

	private final float FORCE_MAGNITUDE = 40f;

	private final Vector2 southEastVector = new Vector2(1, -0.1f).nor();
	private final Vector2 southWestVector = new Vector2(-1, -0.1f).nor();
	private final Vector2 eastVector = new Vector2(1, -0.1f).nor();
	private final Vector2 westVector = new Vector2(-1, -0.1f).nor();
	private final Vector2 initialVector = new Vector2(1, -1f).nor();

	private Vector2 southEastForce;
	private Vector2 southWestForce;
	private Vector2 southEastForceHalf;
	private Vector2 southWestForceHalf;
	private Vector2 initialForce;

	Body yetiBody;

	Vector2 originalLocation;

	public enum YETI_STEER {
		MOVE_LEFT, MOVE_RIGHT
	}

	private long numUpdates = 0;
	private float clampLimit = 5.30f;

	public Yeti(World world, Vector2 location) {
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(location);
		// Create our body in the world using our body definition
		yetiBody = world.createBody(bodyDef);
		// set User Pointer to the Current Object for Rendering Purposes
		yetiBody.setUserData(this);
		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(YETI_RADIUS);
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = YETI_DENSITY;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		// Create our fixture and attach it to the body
		Fixture fixture = yetiBody.createFixture(fixtureDef);
		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();
		yetiBody.setTransform(yetiBody.getPosition().x,
				yetiBody.getPosition().y, (float) (Math.toRadians(-15)));
		this.originalLocation = location;
		/*
		 * Calculate The forces that will be used for streering purposes
		 */

		southEastForce = new Vector2(eastVector.x * FORCE_MAGNITUDE,
				eastVector.y * FORCE_MAGNITUDE);
		southWestForce = new Vector2(westVector.x * FORCE_MAGNITUDE,
				westVector.y * FORCE_MAGNITUDE);

		southEastForceHalf = new Vector2(southEastVector.x * FORCE_MAGNITUDE
				* 0.25f, southEastVector.y * FORCE_MAGNITUDE * 0.25f);
		southWestForceHalf = new Vector2(southWestVector.x * FORCE_MAGNITUDE
				* 0.25f, southWestVector.y * FORCE_MAGNITUDE * 0.25f);
		initialForce = new Vector2(initialVector.x * FORCE_MAGNITUDE * 0.75f,
				initialVector.y * FORCE_MAGNITUDE * 0.75f);

		numberSwitch = 0;
	}

	/*
	 * Check if the Yeti is alive or not Used while checking for GameOver
	 */
	public boolean isDead() {
		return isDead;
	}

	public void switchMove() {
		numberSwitch += 1;
		if (currentSteer == YETI_STEER.MOVE_RIGHT)
			currentSteer = YETI_STEER.MOVE_LEFT;
		else
			currentSteer = YETI_STEER.MOVE_RIGHT;
	}

	public void update(float delta) {
		numUpdates += 1;
		if (numUpdates > Integer.MAX_VALUE) {
			clampLimit = Math.min(6, clampLimit * 1.1f);
			numUpdates = 0;
		}
		if (!this.isDead()) {
			Vector2 linearVelocity = yetiBody.getLinearVelocity();
			float angle = linearVelocity.angle() % 360;
			switch (currentSteer) {
			case MOVE_LEFT:
				if (angle >= 200f && angle <= 250f) {
					yetiBody.applyForceToCenter(southWestForceHalf, false);
				} else {
					yetiBody.applyForceToCenter(southWestForce, false);
				}
				break;
			case MOVE_RIGHT:
				if (numberSwitch == 0) {
					yetiBody.applyForceToCenter(initialForce, false);
				}
				if (angle > 290f && angle < 340f) {
					yetiBody.applyForceToCenter(southEastForceHalf, false);
				} else {
					yetiBody.applyForceToCenter(southEastForce, false);
				}
				break;
			}
			yetiBody.setTransform(yetiBody.getPosition().x,
					yetiBody.getPosition().y, (float) (Math.toRadians(angle)));
			linearVelocity.clamp(0, clampLimit);
			yetiBody.setLinearVelocity(linearVelocity.x, linearVelocity.y);
		}
	}

	public Vector2 getPosition() {
		return yetiBody.getPosition();
	}

	public float getAngle() {
		return (float) Math.toDegrees(yetiBody.getAngle());
	}

	public void collideWithSpike() {
		isDead = true;
	}

	public void reset() {
		isDead = false;
		yetiBody.setTransform(originalLocation, 0);
		currentSteer = YETI_STEER.MOVE_RIGHT;
		yetiBody.setLinearVelocity(Vector2.Zero);
		numberSwitch = 0;
		yetiBody.setTransform(yetiBody.getPosition().x,
				yetiBody.getPosition().y, (float) (Math.toRadians(-15)));
	}

	public YETI_STEER getCurrentSteer() {
		return currentSteer;
	}
}
