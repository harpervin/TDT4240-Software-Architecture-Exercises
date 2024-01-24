package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Exercise1 extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite helicopterSprite;

	Texture helicopterTexture;
	float heli_x, heli_y;

	float heliRotation;
	float speed = 200.0f; // Adjust the speed as needed

	@Override
	public void create() {
		batch = new SpriteBatch();
		helicopterTexture = new Texture("helicopter.png"); // Replace with your texture file
		helicopterSprite = new Sprite(helicopterTexture);

		// Initialize helicopter position at the center of the screen
		heli_x = Gdx.graphics.getWidth() / 2.0f - helicopterTexture.getWidth() / 2.0f;
		heli_y = Gdx.graphics.getHeight() / 2.0f - helicopterTexture.getHeight() / 2.0f;
		heliRotation = 0;

		// Set initial position
		helicopterSprite.setPosition(heli_x, heli_y);

		// Set initial rotation (e.g., facing to the right)
		helicopterSprite.flip(true, false);
		helicopterSprite.setRotation(45f);
	}

	@Override
	public void render() {



		// Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start render
		batch.begin();

		System.out.println("Phone height: " + Gdx.graphics.getHeight());
		System.out.println("Phone width: " + Gdx.graphics.getWidth());

		System.out.println("Heli height: " + helicopterTexture.getHeight());
		System.out.println("Heli width: " + helicopterTexture.getWidth());

		// Draw the helicopter with rotation
		helicopterSprite.draw(batch);
		moveHelicopter();
		batch.end();
	}

	private void moveHelicopter() {
		// Move the helicopter based on its current rotation
		helicopterSprite.setX(helicopterSprite.getX() +
				MathUtils.cosDeg(helicopterSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime());
		helicopterSprite.setY(helicopterSprite.getY() +
				MathUtils.sinDeg(helicopterSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime());


		// Check if the helicopter is about to leave the screen
		bounceOffEdges();
	}

	private void bounceOffEdges() {
		// Check if the helicopter is about to leave the screen
		if (helicopterSprite.getX() < 0 ||
				helicopterSprite.getX() + helicopterSprite.getWidth() > Gdx.graphics.getWidth()) {
			// Bounce off the left or right edge
			System.out.println("Hit x border");
			helicopterSprite.flip(false, true);
			helicopterSprite.setRotation(180 - helicopterSprite.getRotation());
		}

		if (helicopterSprite.getY() < 0 ||
				helicopterSprite.getY() + helicopterSprite.getHeight() > Gdx.graphics.getHeight()) {
			// Bounce off the top or bottom edge
			System.out.println("Hit y border");
			helicopterSprite.flip(false, false);
			helicopterSprite.setRotation(-helicopterSprite.getRotation());
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		helicopterSprite.getTexture().dispose();
	}
}
