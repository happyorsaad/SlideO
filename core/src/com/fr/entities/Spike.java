package com.fr.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Spike {
	public enum Side {
		LEFT, RIGHT
	}

	public Body spikeBody;

	float width;

	float x, y;
	Side side;

	World world;

	public Spike(World world, float X, float Y, Side side) {
		this.world = world;
		this.reset(X, Y, side);
	}

	public void reset(float X, float Y, Side side) {
		float leftX, rightX;
		if (side == Side.LEFT) {
			leftX = 0;
			rightX = X;
		} else {
			leftX = X;
			rightX = GameWorld.WORLD_WIDTH;
		}

		this.x = X;
		this.y = Y;
		this.side = side;

		width = rightX - leftX;

		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic
		bodyDef.type = BodyType.StaticBody;
		// Set our body's starting position in the world
		bodyDef.position.set(leftX + width / 2, Y);
		// Create our body in the world using our body definition
		spikeBody = world.createBody(bodyDef);
		// set User Pointer to the Current Object for Rendering Purposes
		spikeBody.setUserData(this);
		// Create a circle shape and set its radius to 6
		PolygonShape box = new PolygonShape();
		box.setAsBox(width / 2, 0.03f);
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 1;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		// Create our fixture and attach it to the body
		Fixture fixture = spikeBody.createFixture(fixtureDef);
		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		box.dispose();

		spikeBody.setUserData(this);

	}

	public Vector2 getPosition() {
		return spikeBody.getPosition();
	}

	public float getAngle() {
		return spikeBody.getAngle();
	}

	public float getWidth() {
		return width;
	}
}
