package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class menuScreen extends InputAdapter implements Screen {
    Stage stage;
    SpriteBatch batch;
    Texture player;
    float speed = 800.0f;
    float heli_x = Gdx.graphics.getWidth();
    float heli_y = Gdx.graphics.getHeight();
    private float heliRotation = 0;


    Sprite sprite = new Sprite(new Texture("helicopter.png"));



    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        ScreenUtils.clear(1, 1, 1, 0);

        batch.begin();
        stage.draw();

        float x = (float) Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2;
        float y = (float) Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2;
        sprite.setPosition(x, y);

        sprite.setRotation(heliRotation);

        heli_x += MathUtils.cosDeg(heliRotation) * speed * Gdx.graphics.getDeltaTime();
        heli_y += MathUtils.sinDeg(heliRotation) * speed * Gdx.graphics.getDeltaTime();

        // Check if the helicopter is about to leave the screen
        if (heli_x < 0 || heli_x + player.getWidth() > Gdx.graphics.getWidth()) {
            // Bounce off the left or right edge
            heliRotation = 180 - heliRotation;
        }

        if (heli_y < 0 || heli_y + player.getHeight() > Gdx.graphics.getHeight()) {
            // Bounce off the top or bottom edge
            heliRotation = -heliRotation;
        }

        batch.draw(player, heli_x, heli_y);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
    }
}
