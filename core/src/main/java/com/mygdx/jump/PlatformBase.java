package com.mygdx.jump;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface PlatformBase {
    boolean isVanished();
    void onStep();
    void render(SpriteBatch batch);
    float getX();
    float getY();
    float getWidth();
    float getHeight();
}
