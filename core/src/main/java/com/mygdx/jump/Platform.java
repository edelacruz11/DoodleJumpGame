package com.mygdx.jump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Platform implements PlatformBase {
    private static Texture texLeft, texMiddle, texRight;
    private float x, y, width, height, scale;
    private boolean vanished;

    public static void loadTextures(float scale) {
        texLeft = new Texture("tiles/platforms/left.png");
        texMiddle = new Texture("tiles/platforms/middle.png");
        texRight  = new Texture("tiles/platforms/right.png");
    }

    public static void disposeTextures() {
        texLeft.dispose();
        texMiddle.dispose();
        texRight.dispose();
    }

    public static float getMiddleWidth(float scale) {
        return texMiddle.getWidth() * scale;
    }

    public static float getMiddleHeight(float scale) {
        return texMiddle.getHeight() * scale;
    }

    public Platform(float x, float y, float scale) {
        this.x = x; this.y = y; this.scale = scale;
        this.width = (texLeft.getWidth() + texMiddle.getWidth() + texRight.getWidth()) * scale;
        this.height = texMiddle.getHeight() * scale;
    }

    @Override public boolean isVanished() { return vanished; }
    @Override public void onStep() { }

    @Override
    public void render(SpriteBatch batch) {
        if (vanished) return;
        float wL = texLeft.getWidth() * scale;
        float wM = texMiddle.getWidth() * scale;
        float h = texMiddle.getHeight()* scale;
        batch.draw(texLeft, x, y, wL, h);
        batch.draw(texMiddle, x+wL, y, wM, h);
        batch.draw(texRight, x+wL+wM, y, texRight.getWidth()*scale, h);
    }

    @Override public float getX() { return x; }
    @Override public float getY() { return y; }
    @Override public float getWidth() { return width; }
    @Override public float getHeight() { return height; }
}
