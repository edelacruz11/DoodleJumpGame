// Player.java
package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private Texture texture;
    private float x, y;
    private float width, height;
    private float scale = 2f;

    private float velocityX = 0f;
    private float speed     = 300f;

    private float velocityY = 0f;
    private float gravity   = -800f;
    private float jumpSpeed = 650f;

    private float groundHeight;
    private String color;

    public Player(String color, float groundHeight) {
        this.color        = color;
        this.groundHeight = groundHeight;
        loadTexture();
        x      = 100;
        y      = groundHeight;
        width  = texture.getWidth();
        height = texture.getHeight();
    }

    private void loadTexture() {
        if (texture != null) texture.dispose();
        texture = new Texture("player/" + color + "/front.png");
    }

    public void update(float delta) {
        // Movimiento lateral
        x += velocityX * delta;

        // Física vertical
        velocityY += gravity * delta;
        y         += velocityY * delta;

        // Rebote en el suelo
        if (y <= groundHeight) {
            y         = groundHeight;
            velocityY = jumpSpeed;
        }

        // Límites horizontal de pantalla (usa world coords)
        float screenW = Gdx.graphics.getWidth();
        if (x < 0)      x = 0;
        if (x + getWidth() > screenW) {
            x = screenW - getWidth();
        }
    }

    public void moveLeft()  { velocityX = -speed; }
    public void moveRight() { velocityX =  speed; }
    public void stop()      { velocityX = 0f;     }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width * scale, height * scale);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setScale(float scale)          { this.scale = scale;      }

    public float getY()           { return y;             }
    public float getX()           { return x;             }
    public float getWidth()       { return width * scale; }
    public float getHeight()      { return height * scale;}
    public float getVelocityY()   { return velocityY;     }

    /** Fuerza el salto (se invoca al pisar una plataforma) */
    public void jump() {
        this.velocityY = jumpSpeed;
    }
}
