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

	Texture joystickTexture;
	Sprite joystickSprite;
	Vector2 joystickPosition;
	Vector2 joystickCenter; // Center of the joystick

	BitmapFont font;

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
		helicopterSprite.setRotation(0f);

		// Set joystick size (adjust as needed)
		float joystickSize = 400.0f;
		joystickTexture = new Texture("joystick.jpg");
		joystickSprite = new Sprite(joystickTexture);

		joystickSprite.setSize(joystickSize, joystickSize);

		// Position the joystick base at the bottom left corner
		joystickSprite.setPosition(20, 20);

		// Initialize the joystick position at the center of the base
		joystickCenter = new Vector2(joystickSprite.getX() + joystickSprite.getWidth() / 2,
				joystickSprite.getY() + joystickSprite.getHeight() / 2);
		joystickPosition = new Vector2(joystickCenter);

		// Create a BitmapFont for rendering text
		font = new BitmapFont();
		font.getData().setScale(4.0f);  // Adjust the scale factor as needed

	}

	@Override
	public void render() {

		// Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start render
		batch.begin();

		// Update joystick position
		updateJoystick();

		// Draw the joystick base
		joystickSprite.draw(batch);

		// Draw the helicopter with rotation
		helicopterSprite.draw(batch);

		// Draw the position text in the upper-left corner
		font.draw(batch, "Position: " + heli_x + ", " + heli_y, 20, Gdx.graphics.getHeight() - 20);

		batch.end();
	}

	private void updateJoystick() {
		// Update joystick position based on touch input
		if (Gdx.input.isTouched()) {
			float touchX = Gdx.input.getX();
			float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y-axis

			// Check if the touch is within the joystick base
			if (touchX >= joystickSprite.getX() && touchX <= joystickSprite.getX() + joystickSprite.getWidth() &&
					touchY >= joystickSprite.getY() && touchY <= joystickSprite.getY() + joystickSprite.getHeight()) {
				joystickPosition.set(touchX, touchY);
				// Move the helicopter based on joystick position
				moveHelicopter();
			} else {
				// If no input, reset joystick position to the center
				joystickPosition.set(joystickCenter);
			}
		}
	}

	private void moveHelicopter() {
		// Calculate the direction vector from the joystick center to the joystick position
		Vector2 direction = new Vector2(joystickPosition.x - joystickCenter.x, joystickPosition.y - joystickCenter.y);

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
		joystickSprite.getTexture().dispose();
	}
}
