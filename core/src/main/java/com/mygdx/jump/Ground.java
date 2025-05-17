package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Ground {
    private Texture groundLeft;
    private Texture groundMiddle;
    private Texture groundRight;

    private float scale = 1.2f;

    public Ground() {
        groundLeft = new Texture("tiles/ground/left.png");
        groundMiddle = new Texture("tiles/ground/middle.png");
        groundRight = new Texture("tiles/ground/right.png");
    }

    public void render(SpriteBatch batch) {
        float tileLeftWidth = groundLeft.getWidth() * scale;
        float tileRightWidth = groundRight.getWidth() * scale;
        float tileMiddleWidth = groundMiddle.getWidth() * scale;
        float tileHeight = groundMiddle.getHeight() * scale;

        float y = 0;
        float screenWidth = Gdx.graphics.getWidth();

        // Dibuja izquierda
        batch.draw(groundLeft, 0, y, tileLeftWidth, tileHeight);

        // Dibuja derecha (al borde derecho exacto)
        float rightX = screenWidth - tileRightWidth;
        batch.draw(groundRight, rightX, y, tileRightWidth, tileHeight);

        // Dibuja middle entre left y right
        float middleStartX = tileLeftWidth;
        float middleEndX = rightX;
        float currentX = middleStartX;

        while (currentX + tileMiddleWidth <= middleEndX) {
            batch.draw(groundMiddle, currentX, y, tileMiddleWidth, tileHeight);
            currentX += tileMiddleWidth;
        }

        // Si hay un hueco muy pequeño entre la última middle y right, lo ignoramos
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
