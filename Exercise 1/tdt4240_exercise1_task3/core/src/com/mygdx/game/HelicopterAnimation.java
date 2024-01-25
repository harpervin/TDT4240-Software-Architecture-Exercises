package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public class HelicopterAnimation extends ApplicationAdapter {
	SpriteBatch batch;
	Animation<TextureRegion> helicopterAnimation;
	TextureRegion[] helicopterFrames;
	Sprite helicopterSprite;
	Sprite helicopterSprite2;
	Sprite helicopterSprite3;
	Sprite helicopterSprite4;

	float stateTime;
	float heli_x, heli_y;
	float heliRotation;
	float speed = 600.0f; // Adjust the speed as needed

	boolean rotatedX = false;
	boolean rotatedY = false;

	List<Sprite> allSprites;
	List<Float> speeds;  // List to store individual speeds for each helicopter
	List<Boolean> rotatedXFlags;  // List to store rotatedX flags for each helicopter



	@Override
	public void create() {
		batch = new SpriteBatch();

		// Load your individual PNG frames
		Texture frame1 = new Texture("heli1.png");
		Texture frame2 = new Texture("heli2.png");
		Texture frame3 = new Texture("heli3.png");
		Texture frame4 = new Texture("heli4.png");

		helicopterFrames = new TextureRegion[4];
		helicopterFrames[0] = new TextureRegion(frame1);
		helicopterFrames[1] = new TextureRegion(frame2);
		helicopterFrames[2] = new TextureRegion(frame3);
		helicopterFrames[3] = new TextureRegion(frame4);

		// Flip all frames horizontally
		for (TextureRegion region : helicopterFrames) {
			region.flip(true, false);
		}

		helicopterAnimation = new Animation<>(0.1f, helicopterFrames); // Frame duration 0.1 seconds

		// Initialize helicopter position at the center of the screen
		heli_x = Gdx.graphics.getWidth() / 2.0f - helicopterFrames[0].getRegionWidth() / 2.0f;
		heli_y = Gdx.graphics.getHeight() / 2.0f - helicopterFrames[0].getRegionHeight() / 2.0f;
		heliRotation = 0;

		helicopterSprite = new Sprite(helicopterAnimation.getKeyFrame(0));
		helicopterSprite2 = new Sprite(helicopterAnimation.getKeyFrame(0));
		helicopterSprite3 = new Sprite(helicopterAnimation.getKeyFrame(0));
		helicopterSprite4 = new Sprite(helicopterAnimation.getKeyFrame(0));

		// Set initial position
		helicopterSprite.setPosition(heli_x, heli_y);
		setRandomPosition(helicopterSprite2);
		setRandomPosition(helicopterSprite3);
		setRandomPosition(helicopterSprite4);

		// Set initial rotation
		helicopterSprite.setRotation(MathUtils.random(360.0f));
		helicopterSprite2.setRotation(MathUtils.random(360.0f));
		helicopterSprite3.setRotation(MathUtils.random(360.0f));
		helicopterSprite4.setRotation(MathUtils.random(360.0f));

		//allSprites = Arrays.asList(helicopterSprite);

		allSprites = Arrays.asList(helicopterSprite, helicopterSprite2, helicopterSprite3, helicopterSprite4);
		// Initialize speeds and rotatedX flags
		speeds = Arrays.asList(MathUtils.random(1, 7) * 100.0f,  // Adjust these speeds as needed
				MathUtils.random(1, 35) * 20.0f,
				MathUtils.random(1, 35) * 20.0f,
				MathUtils.random(1, 35) * 20.0f);

		rotatedXFlags = Arrays.asList(false, false, false, false);
	}

	@Override
	public void render() {
		// Clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Start render
		batch.begin();

		// Draw the helicopter with animation
		stateTime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = helicopterAnimation.getKeyFrame(stateTime, true);
		helicopterSprite.setRegion(currentFrame);
		helicopterSprite2.setRegion(currentFrame);
		helicopterSprite3.setRegion(currentFrame);
		helicopterSprite4.setRegion(currentFrame);

		for (int i = 0; i < allSprites.size(); i++) {
			Sprite sprite = allSprites.get(i);
			sprite.setRegion(currentFrame);
			sprite.draw(batch);
			moveHelicopter(sprite, speeds.get(i));

		}
		handleCollision();

		batch.end();
	}

	private void handleCollision() {
		for (Sprite sprite_i : allSprites) {
			Rectangle bounds1 = sprite_i.getBoundingRectangle();

			for (Sprite sprite_j : allSprites) {
				if (sprite_i == sprite_j) {
					continue; // Skip checking collision with itself
				}

				Rectangle bounds2 = sprite_j.getBoundingRectangle();

				if (bounds1.overlaps(bounds2)) {
					bounceOff(sprite_i, sprite_j);
				}
			}
		}
	}

	private void bounceOff(Sprite sprite1, Sprite sprite2) {
		// Get the angles of both sprites
		float angle1 = sprite1.getRotation();
		float angle2 = sprite2.getRotation();

		// Calculate the average angle
		float averageAngle = (angle1 + angle2) / 2.0f;

		// Calculate the relative direction vector
		Vector2 relativeDirection = new Vector2(sprite2.getX() - sprite1.getX(), sprite2.getY() - sprite1.getY()).nor();

		// Calculate the reflection direction based on relative direction
		float reflectionAngle1 = 2 * relativeDirection.angleRad() - angle1;
		float reflectionAngle2 = 2 * relativeDirection.angleRad() - angle2;

		// Update the rotation angles of the sprites
		sprite1.setRotation(averageAngle + reflectionAngle1);
		sprite2.setRotation(averageAngle + reflectionAngle2);

		// Calculate the separation vector
		Vector2 separationVector = new Vector2(sprite1.getX() - sprite2.getX(), sprite1.getY() - sprite2.getY());

		// Normalize the separation vector
		separationVector.nor();

		// Move the sprites slightly apart to avoid repeated collisions
		float overlapDistance = 2.0f;
		sprite1.setX(sprite1.getX() + separationVector.x * overlapDistance);
		sprite1.setY(sprite1.getY() + separationVector.y * overlapDistance);

		sprite2.setX(sprite2.getX() - separationVector.x * overlapDistance);
		sprite2.setY(sprite2.getY() - separationVector.y * overlapDistance);

		// Limit the rotation angles to avoid indefinite spinning
		limitRotation(sprite1);
		limitRotation(sprite2);
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

	private void moveHelicopter(Sprite helicopterSprite, float speed) {
		float sprite_x = helicopterSprite.getX();
		float sprite_y = helicopterSprite.getY();
		float sprite_rotation = helicopterSprite.getRotation();


		float new_x = sprite_x + MathUtils.cosDeg(sprite_rotation) * speed * Gdx.graphics.getDeltaTime();
		float new_y = sprite_y + MathUtils.sinDeg(sprite_rotation) * speed * Gdx.graphics.getDeltaTime();

		helicopterSprite.setX(new_x);
		helicopterSprite.setY(new_y);

		bounceOffEdges(helicopterSprite, speed);
	}

	private void bounceOffEdges(Sprite helicopterSprite, float speed) {
		float velocityX = MathUtils.cosDeg(helicopterSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();
		float velocityY = MathUtils.sinDeg(helicopterSprite.getRotation()) * speed * Gdx.graphics.getDeltaTime();

		if (helicopterSprite.getX() < 0 && velocityX < 0 || helicopterSprite.getX() + helicopterSprite.getWidth() > Gdx.graphics.getWidth() && velocityX > 0) {
			if (!rotatedX) {
				if (180 < helicopterSprite.getRotation()) {
					float difference = helicopterSprite.getRotation() - 180;
					helicopterSprite.setRotation(360 - difference);
				}
				else {
					helicopterSprite.setRotation(180 - helicopterSprite.getRotation());
				}
				rotatedX = true;

			}
		} else {
			// Reset the flag when the helicopter is no longer at the x border
			rotatedX = false;
		}

		if (helicopterSprite.getY() < 0  && velocityY < 0 || helicopterSprite.getY() + helicopterSprite.getHeight() > Gdx.graphics.getHeight() && velocityY > 0) {
			if (!rotatedY) {
				helicopterSprite.setRotation(360 - helicopterSprite.getRotation());
				rotatedX = true;
			}
			else {
				rotatedY = false;
			}
		}
	}

	private void setRandomPosition(Sprite sprite) {
		float randomX = MathUtils.random(Gdx.graphics.getWidth() - sprite.getWidth());
		float randomY = MathUtils.random(Gdx.graphics.getHeight() - sprite.getHeight());

		sprite.setPosition(randomX, randomY);
	}

	@Override
	public void dispose() {
		batch.dispose();
		// Dispose of individual textures
		for (TextureRegion frame : helicopterFrames) {
			frame.getTexture().dispose();
		}
	}
}
