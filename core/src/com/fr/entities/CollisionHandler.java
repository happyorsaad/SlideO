package com.fr.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		System.out.println("collision");
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();

		Object entityA = bodyA.getUserData();
		Object entityB = bodyB.getUserData();

		if (entityA instanceof Yeti && entityB instanceof Spike) {
			Yeti yeti = (Yeti) entityA;
			yeti.collideWithSpike();
		} else if (entityA instanceof Spike && entityB instanceof Yeti) {
			Yeti yeti = (Yeti) entityB;
			yeti.collideWithSpike();
		}
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

}
