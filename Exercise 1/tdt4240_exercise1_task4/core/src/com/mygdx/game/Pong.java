package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public class Pong extends ApplicationAdapter {
	SpriteBatch batch;
	Pixmap red_object;
	Pixmap blue_object;

	Texture redTexture;
	Texture blueTexture;
	Sprite redSprite;
	Sprite blueSprite;

	Pixmap ballPixmap;
	Texture ballTexture;
	Sprite ballSprite;

	BitmapFont redFont;
	BitmapFont blueFont;
	BitmapFont endGameFont;

	float red_x, red_y, blue_x, blue_y;
	float ballRotation;
	float speed = 500.0f; // Adjust the speed as needed
	int redScore, blueScore;

	float lastTouchX;
	float lastTouchY;

	boolean rotatedX = false;
	boolean rotatedY = false;
	Sprite draggedSprite;

	private float timer;  // Timer to track the time since the last score


	@Override
	public void create() {
		batch = new SpriteBatch();

		timer = 0.0f;


		red_object = new Pixmap(150, 40, Pixmap.Format.RGBA8888);
		red_object.setColor(Color.RED);
		red_object.fill();

		blue_object = new Pixmap(150, 40, Pixmap.Format.RGBA8888);
		blue_object.setColor(Color.BLUE);
		blue_object.fill();

		redTexture = new Texture(red_object);
		blueTexture = new Texture(blue_object);
		redSprite = new Sprite(redTexture);
		blueSprite = new Sprite(blueTexture);

		// Initialize helicopter position at the center of the screen
		red_x = Gdx.graphics.getWidth() / 2.0f - redTexture.getWidth() / 2.0f;
		red_y = Gdx.graphics.getHeight() - 50.0f;
		blue_x = Gdx.graphics.getWidth() / 2.0f - blueTexture.getWidth() / 2.0f;
		blue_y = 50.0f;

		// Set initial position
		redSprite.setPosition(red_x, red_y);
		blueSprite.setPosition(blue_x, blue_y);

		red_object.dispose();
		blue_object.dispose();

		ballPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
		ballPixmap.setColor(Color.BLACK);
		ballPixmap.fillCircle(25, 25, 25);

		ballTexture = new Texture(ballPixmap);
		ballSprite = new Sprite(ballTexture);
		resetBall();
		ballPixmap.dispose();

		redFont = new BitmapFont();
		redFont.getData().setScale(4.0f);  // Adjust the scale factor as needed

		blueFont = new BitmapFont();
		blueFont.getData().setScale(4.0f);

		redScore = 0;
		blueScore = 0;

		endGameFont = new BitmapFont();
		endGameFont.getData().setScale(8.0f);

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				// Save the initial touch/mouse coordinates
				lastTouchX = screenX;
				lastTouchY = screenY;

				// Check if the touch is within the red sprite's bounding rectangle
				if (redSprite.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight() - screenY)) {
					// Red sprite is touched
					draggedSprite = redSprite;
				}

				// Check if the touch is within the blue sprite's bounding rectangle
				if (blueSprite.getBoundingRectangle().contains(screenX, Gdx.graphics.getHeight() - screenY)) {
					// Blue sprite is touched
					draggedSprite = blueSprite;
				}

				return true;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if (draggedSprite != null) {
					// Calculate the change in X-coordinate
					float deltaX = screenX - lastTouchX;

					// Update the position of the dragged sprite
					float newSpriteX = draggedSprite.getX() + deltaX;

					// Ensure the sprite stays within the x borders
					if (newSpriteX >= 0 && newSpriteX <= Gdx.graphics.getWidth() - draggedSprite.getWidth()) {
						draggedSprite.setX(newSpriteX);
					}

					// Save the current touch/mouse coordinates for the next frame
					lastTouchX = screenX;
					lastTouchY = screenY;
				}

				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				// Reset the dragged sprite when the touch is released
				draggedSprite = null;
				return true;
			}
		});

	}

	@Override
	public void render() {
		// Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start render
		batch.begin();

		// Update the timer
		timer += Gdx.graphics.getDeltaTime();

		redSprite.draw(batch);
		blueSprite.draw(batch);
		ballSprite.draw(batch);

		// Draw the position text in the upper-left corner
		redFont.draw(batch, "Red: " + Integer.toString(redScore), 20.0f, Gdx.graphics.getHeight() - 20);

		int prevRedScore = redScore;
		int prevBlueScore = blueScore;

		moveBall();

		// Check if more than 10 seconds have passed without scoring
		if (timer > 10.0f && prevRedScore == redScore && prevBlueScore == blueScore) {
			// Reset the timer
			timer = 0.0f;

			// Generate a random angle in the specified ranges
			float randomAngle = MathUtils.randomBoolean() ?
					MathUtils.random(45, 135) :
					MathUtils.random(225, 315);

			ballSprite.setRotation(randomAngle);
		}

		checkScore();

		// Draw the position text in the bottom-left corner
		blueFont.draw(batch, "Blue: " + Integer.toString(blueScore), 20.0f, 100.0f);


		bounceOffPlayer();


		batch.end();
	}

	private void bounceOffPlayer() {
		float velocityX = MathUtils.cosDeg(ballSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();
		float velocityY = MathUtils.sinDeg(ballSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();

		// Check collision with red sprite's boundaries
		if (ballSprite.getBoundingRectangle().overlaps(redSprite.getBoundingRectangle())) {
			System.out.println("collided with blue");
			float overlapX = Math.min(ballSprite.getX() + ballSprite.getWidth(), redSprite.getX() + redSprite.getWidth())
					- Math.max(ballSprite.getX(), redSprite.getX());
			float overlapY = Math.min(ballSprite.getY() + ballSprite.getHeight(), redSprite.getY() + redSprite.getHeight())
					- Math.max(ballSprite.getY(), redSprite.getY());

			if (overlapX < overlapY) {
				// Collided horizontally
				velocityX *= -1; // Reverse X direction
			} else {
				// Collided vertically
				velocityY *= -1; // Reverse Y direction
			}
		}

		// Check collision with blue sprite's boundaries
		if (ballSprite.getBoundingRectangle().overlaps(blueSprite.getBoundingRectangle())) {
			System.out.println("collided with blue");
			float overlapX = Math.min(ballSprite.getX() + ballSprite.getWidth(), blueSprite.getX() + blueSprite.getWidth())
					- Math.max(ballSprite.getX(), blueSprite.getX());
			float overlapY = Math.min(ballSprite.getY() + ballSprite.getHeight(), blueSprite.getY() + blueSprite.getHeight())
					- Math.max(ballSprite.getY(), blueSprite.getY());

			if (overlapX < overlapY) {
				// Collided horizontally
				velocityX *= -1; // Reverse X direction
			} else {
				// Collided vertically
				velocityY *= -1; // Reverse Y direction
			}
		}
		ballSprite.setRotation(MathUtils.atan2(velocityY, velocityX) * MathUtils.radiansToDegrees);

		// Update ball's position and rotation
		ballSprite.setX(ballSprite.getX() + velocityX);
		ballSprite.setY(ballSprite.getY() + velocityY);

		// Limit the rotation angles to avoid indefinite spinning
		limitRotation(ballSprite);
		System.out.println(speed);
	}

	// Add this method to limit the rotation angles
	private void limitRotation(Sprite sprite) {
		float rotation = sprite.getRotation();

		// Limit the rotation to a reasonable range, for example, [0, 360)
		if (rotation < 0) {
			sprite.setRotation(rotation + 360);
		} else if (rotation >= 360) {
			sprite.setRotation(rotation - 360);
		}
	}

	private void moveBall() {
		float ball_x = ballSprite.getX();
		float ball_y = ballSprite.getY();
		float ball_rotation = ballSprite.getRotation();


		float new_x = ball_x + MathUtils.cosDeg(ball_rotation) * speed * Gdx.graphics.getDeltaTime();
		float new_y = ball_y + MathUtils.sinDeg(ball_rotation) * speed * Gdx.graphics.getDeltaTime();

		ballSprite.setX(new_x);
		ballSprite.setY(new_y);

		bounceOffEdges();
	}

	private void bounceOffEdges() {
		float velocityX = MathUtils.cosDeg(ballSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();
		float velocityY = MathUtils.sinDeg(ballSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();

		if (ballSprite.getX() < 0 && velocityX < 0 || ballSprite.getX() + ballSprite.getWidth() > Gdx.graphics.getWidth() && velocityX > 0) {
			if (!rotatedX) {
				if (180 < ballSprite.getRotation()) {
					float difference = ballSprite.getRotation() - 180;
					ballSprite.setRotation(360 - difference);
				}
				else {
					ballSprite.setRotation(180 - ballSprite.getRotation());
				}
				rotatedX = true;

			}
		} else {
			// Reset the flag when the helicopter is no longer at the x border
			rotatedX = false;
		}

		if (ballSprite.getY() < -ballSprite.getWidth()  && velocityY < 0)  {
			redScore += 1;
			resetBall();
		}
		else if (ballSprite.getY() >
			Gdx.graphics.getHeight() && velocityY > 0) {
			blueScore += 1;
			resetBall();
		}
	}

	public void checkScore() {
		if (redScore == 21) {
			resetBall();
			redSprite.setPosition(red_x, red_y);
			blueSprite.setPosition(blue_x, blue_y);
			endGameFont.draw(batch, "Red Wins!", 250.0f, Gdx.graphics.getHeight()/10.0f + Gdx.graphics.getHeight()/2.0f);
		}
		else if (blueScore == 21) {
			resetBall();
			redSprite.setPosition(red_x, red_y);
			blueSprite.setPosition(blue_x, blue_y);
			endGameFont.draw(batch, "Blue Wins!", 250.0f, Gdx.graphics.getHeight()/10.0f + Gdx.graphics.getHeight()/2.0f);
		}
	}

	public void resetBall() {
		ballSprite.setPosition(Gdx.graphics.getWidth()/2.0f - ballSprite.getWidth()/2.0f,
				Gdx.graphics.getHeight()/2.0f - ballSprite.getHeight()/2.0f);
		ballSprite.setRotation(MathUtils.random(360.0f));
	}

	@Override
	public void dispose() {
		batch.dispose();
		redSprite.getTexture().dispose();
		blueSprite.getTexture().dispose();
	}
}
