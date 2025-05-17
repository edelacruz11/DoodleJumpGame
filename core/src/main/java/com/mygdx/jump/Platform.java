// Platform.java
package com.mygdx.jump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Platform {
    private static Texture texLeft;
    private static Texture texMiddle;
    private static Texture texRight;

    public float x, y, width, height;
    private float scale;

    public static void loadTextures(float scale) {
        texLeft   = new Texture("tiles/platforms/left.png");
        texMiddle = new Texture("tiles/platforms/middle.png");
        texRight  = new Texture("tiles/platforms/right.png");
        // (filtros u otros ajustes aquí)
    }

    public static void disposeTextures() {
        texLeft.dispose();
        texMiddle.dispose();
        texRight.dispose();
    }

    /** Devuelve el ancho de la pieza “middle” escalada */
    public static float getMiddleWidth(float scale) {
        return texMiddle.getWidth() * scale;
    }

    /** Devuelve la altura de la pieza “middle” escalada */
    public static float getMiddleHeight(float scale) {
        return texMiddle.getHeight() * scale;
    }

    public Platform(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        // ancho total = left + middle + right
        this.width  = (texLeft.getWidth() + texMiddle.getWidth() + texRight.getWidth()) * scale;
        this.height = texMiddle.getHeight() * scale;
    }

    public void render(SpriteBatch batch) {
        float wL = texLeft.getWidth()   * scale;
        float wM = texMiddle.getWidth() * scale;
        float h  = texMiddle.getHeight()* scale;

        batch.draw(texLeft,   x,     y, wL, h);
        batch.draw(texMiddle, x + wL,y, wM, h);
        batch.draw(texRight,  x + wL + wM, y, texRight.getWidth()*scale, h);
    }
}
