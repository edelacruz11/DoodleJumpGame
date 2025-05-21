package com.mygdx.jump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cloud implements PlatformBase {
    static Texture texLeft, texMiddle, texRight;
    private float x, y, width, height, scale;
    private boolean vanished;
    // velocidad horizontal
    private float vx = 0;

    // Carga las tres texturas de la nube
    public static void loadTextures(float scale) {
        texLeft = new Texture("tiles/clouds/left.png");
        texMiddle = new Texture("tiles/clouds/middle.png");
        texRight = new Texture("tiles/clouds/right.png");
    }
    public static void disposeTextures() {
        texLeft.dispose();
        texMiddle.dispose();
        texRight.dispose();
    }

    public Cloud(float x, float y, float scale) {
        this.x = x; this.y = y; this.scale = scale;
        width = (texLeft.getWidth() + texMiddle.getWidth() + texRight.getWidth()) * scale;
        height = texMiddle.getHeight() * scale;
    }

    // Inicia movimiento lateral con cierta velocidad
    public void startMoving(float speed) {
        vx = speed;
    }

    // Mueve la nube y rebota en los bordes del mundo
    public void update(float delta, float worldWidth) {
        if (vx == 0) return;
        x += vx * delta;
        if (x < 0) {
            x = 0; vx = -vx;
        } else if (x + width > worldWidth) {
            x = worldWidth - width; vx = -vx;
        }
    }

    @Override public boolean isVanished() { return vanished; }
    @Override public void onStep() { vanished = true; }

    @Override
    public void render(SpriteBatch batch) {
        if (vanished) return;
        float wL = texLeft.getWidth() * scale;
        float wM = texMiddle.getWidth() * scale;
        float h  = texMiddle.getHeight() * scale;
        batch.draw(texLeft, x, y, wL, h);
        batch.draw(texMiddle, x+wL, y, wM, h);
        batch.draw(texRight, x+wL+wM,y, texRight.getWidth()*scale, h);
    }

    @Override public float getX() { return x; }
    @Override public float getY() { return y; }
    @Override public float getWidth() { return width; }
    @Override public float getHeight() { return height; }
}
