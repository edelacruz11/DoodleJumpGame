package com.mygdx.jump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Ground {
    private Texture groundLeft, groundMiddle, groundRight;
    private float scale = 1.2f;

    public Ground() {
        groundLeft = new Texture("tiles/ground/left.png");
        groundMiddle = new Texture("tiles/ground/middle.png");
        groundRight = new Texture("tiles/ground/right.png");
    }

    // Dibuja suelo completo, adaptado a worldWidth
    public void render(SpriteBatch batch, float worldWidth) {
        float leftW = groundLeft.getWidth() * scale;
        float midW = groundMiddle.getWidth() * scale;
        float rightW = groundRight.getWidth() * scale;
        float height = groundMiddle.getHeight() * scale;
        // Lado izquierdo
        batch.draw(groundLeft, 0, 0, leftW, height);
        // Lado derecho
        float rightX = worldWidth - rightW;
        batch.draw(groundRight, rightX, 0, rightW, height);
        // Piezas middle
        float x = leftW;
        while (x + midW <= rightX) {
            batch.draw(groundMiddle, x, 0, midW, height);
            x += midW;
        }
    }

    public float getHeight() {
        return groundMiddle.getHeight() * scale;
    }

    public void dispose() {
        groundLeft.dispose();
        groundMiddle.dispose();
        groundRight.dispose();
    }
}
