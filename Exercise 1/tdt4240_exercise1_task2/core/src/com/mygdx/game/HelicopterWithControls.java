package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class HelicopterWithControls extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite helicopterSprite;
	Texture helicopterTexture;
	Vector2 dragStartPosition;


	BitmapFont font;

	float heli_x, heli_y;
	float heliRotation;
	float speed = 400.0f; // Adjust the speed as needed

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
		helicopterSprite.setRotation(0f);

		// Create a BitmapFont for rendering text
		font = new BitmapFont();
		font.getData().setScale(4.0f);  // Adjust the scale factor as needed

		dragStartPosition = new Vector2();
	}

	@Override
	public void render() {

		// Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start render
		batch.begin();

		// Handle drag input
		handleInput();

		// Draw the helicopter with rotation
		helicopterSprite.draw(batch);

		// Draw the position text in the upper-left corner
		font.draw(batch, "Click and drag the helicopter to move it!", 30, Gdx.graphics.getHeight() - 500);

		// Draw the position text in the upper-left corner
		font.draw(batch, "Position: " + heli_x + ", " + heli_y, 20, Gdx.graphics.getHeight() - 20);

		batch.end();
	}

	private void handleInput() {
		if (Gdx.input.justTouched()) {
			// Save the starting position when clicked
			dragStartPosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		}

		if (Gdx.input.isTouched()) {
			// Move the helicopter based on the drag
			moveHelicopter();
		}
	}

	private void moveHelicopter() {
		float touchX = Gdx.input.getX();
		float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y-axis

		// Calculate the direction vector from the helicopter center to the touch position
		Vector2 direction = new Vector2(touchX - (heli_x + helicopterSprite.getWidth() / 2),
				touchY - (heli_y + helicopterSprite.getHeight() / 2));

		// Normalize the direction vector
		direction.nor();

		// Move the helicopter based on the normalized direction vector
		float deltaTime = Gdx.graphics.getDeltaTime();
		float deltaX = direction.x * speed * deltaTime;
		float deltaY = direction.y * speed * deltaTime;

		// Save the current position before attempting to move
		float prevX = heli_x;
		float prevY = heli_y;

		// Update the helicopter position
		heli_x += deltaX;
		heli_y += deltaY;

		// Calculate the angle between the joystick direction and the positive x-axis
		float angle = direction.angleDeg();

		// Check for collisions with the screen edges
		if (heli_x < 0 || heli_x + helicopterSprite.getWidth() > Gdx.graphics.getWidth() ||
				heli_y < 0 || heli_y + helicopterSprite.getHeight() > Gdx.graphics.getHeight()) {
			// If there is a collision, revert to the previous position
			heli_x = prevX;
			heli_y = prevY;
		}

		else {
			// Update the helicopter sprite position and angle
			helicopterSprite.setRotation(angle);
			helicopterSprite.setPosition(heli_x, heli_y);
		}

		System.out.println("Angle: " + angle);
	}

	@Override
	public void dispose() {
		batch.dispose();
		helicopterSprite.getTexture().dispose();
	}
}
