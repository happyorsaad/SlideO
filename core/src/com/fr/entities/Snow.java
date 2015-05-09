package com.fr.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.fr.utils.Assets;

public class Snow {

	public class Particle {
		public float x;
		public float y;
		public float r;
		public float d;
	}

	ArrayList<Particle> particles;

	int numParticles = 10;
	float angle = 0;

	float width;
	float height;

	private OrthographicCamera camera;

	public Snow() {
		this.numParticles = 150;
		particles = new ArrayList<Snow.Particle>();
		for (int i = 0; i < numParticles; i += 1) {
			particles.add(new Particle());
		}
		this.width = 480;
		this.height = 800;
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(true, width, height);
		this.camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0);
		this.camera.update();
		loadParticles();
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public void loadParticles() {
		for (int i = 0; i < numParticles; i += 1) {
			Particle particle = particles.get(i);
			particle.x = (float) (width * Math.random());
			particle.y = (float) (height * Math.random());
			particle.r = (float) Math.random() * 18;
			particle.d = (float) (Math.random() * 10f);
		}
		angle = 0;
	}

	public void update() {
		angle += 0.01f;
		for (int i = 0; i < numParticles; i += 1) {
			Particle particle = particles.get(i);
			particle.x += (MathUtils.sin(angle));
			particle.y += (MathUtils.cos(angle + particle.d) + 1 + particle.r / 100f);
			if (particle.x > width * 1.1f || particle.x < -width * 0.1f
					|| particle.y > height) {
				if (i % 3 > 0) // 66.67% of the flakes
				{
					particle.x = (float) (Math.random() * width);
					particle.y = -height * 0.1f;
				} else {
					if (Math.sin(angle) > 0) {
						particle.x = -width * 0.1f;
						particle.y = (float) (Math.random() * height);
					} else {
						particle.x = width * 1.1f;
						particle.y = (float) (Math.random() * height);
					}
				}
			}
		}
	}

	public void render(SpriteBatch batcher) {
		Sprite snowFlake = Assets.snowflake;
		Matrix4 temp = batcher.getProjectionMatrix();
		batcher.setProjectionMatrix(camera.combined);
		for (Particle particle : particles) {
			snowFlake.setPosition(particle.x, particle.y);
			snowFlake.setSize(particle.r, particle.r);
			snowFlake.draw(batcher);
		}
		batcher.setProjectionMatrix(temp);
	}

	public void reset() {
		loadParticles();
	}
}
