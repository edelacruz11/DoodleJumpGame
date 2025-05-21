package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture jumpTex, duckTex;
    private float x, y;
    private float width, height;
    private float scale = 2f;
    private float velocityX = 0f;
    private float speed = 1800f;
    private float velocityY = 0f;
    private float gravity = -1800f;
    private float jumpSpeed = 2000f;
    private float groundHeight;
    private String color;
    private boolean facingRight = true;
    private boolean onGround = true;
    private Sound jumpSound;

    // Hitbox de pies
    private static final float FEET_W_RATIO = 0.25f;
    private static final float FEET_H_RATIO = 0.1f;
    private Rectangle boundsFeet;

    public Player(String color, float groundHeight) {
        this.color = color;
        this.groundHeight = groundHeight;

        jumpTex = new Texture("player/" + color + "/jump.png");
        duckTex = new Texture("player/" + color + "/duck.png");

        width = jumpTex.getWidth();
        height = jumpTex.getHeight();

        x = 100;
        y = groundHeight;

        float w = width * scale * FEET_W_RATIO;
        float h = height * scale * FEET_H_RATIO;
        boundsFeet = new Rectangle(x + (width*scale - w)/2f, y, w, h);

        FileHandle soundFile = Gdx.files.internal("sounds/jump.ogg");
        jumpSound = Gdx.audio.newSound(soundFile);
    }

    public void update(float delta) {
        // Movimiento horizontal y dirección
        if (velocityX > 0) facingRight = true;
        else if (velocityX < 0) facingRight = false;
        x += velocityX * delta;

        // Física vertical
        velocityY += gravity * delta;
        y += velocityY * delta;

        // Rebote en el suelo
        if (y <= groundHeight) {
            y = groundHeight;
            velocityY = jumpSpeed;
            onGround = true;
        } else {
            onGround = false;
        }

        // Actualiza hitbox pies
        float w = width * scale * FEET_W_RATIO;
        float h = height * scale * FEET_H_RATIO;
        boundsFeet.set(x + (width*scale - w)/2f, y, w, h);
    }

    public void moveLeft() { velocityX = -speed; }
    public void moveRight() { velocityX = speed; }
    public void stop() { velocityX = 0; }

    public void render(SpriteBatch batch) {
        Texture toDraw;

        if (onGround) {
            toDraw = jumpTex;
        } else if (velocityY < 0) {
            toDraw = duckTex;
        } else {
            toDraw = jumpTex;
        }

        float drawX = x;
        float drawW = width * scale;
        if (!facingRight) {
            drawX += drawW;
            drawW = -drawW;
        }

        batch.draw(toDraw, drawX, y, drawW, height * scale);
    }

    public void dispose() {
        jumpTex.dispose();
        duckTex.dispose();
        if (jumpSound != null) jumpSound.dispose();
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setScale(float s) { this.scale = s; }
    public void setSpeed(float s) { this.speed = s; }
    public void setJumpSpeed(float js) { this.jumpSpeed = js; }
    public void setGravity(float g) { this.gravity = g; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width * scale;}
    public float getHeight() { return height * scale;}
    public float getVelocityY() { return velocityY; }
    public void setVelocityX(float vx) {
        this.velocityX = vx;
    }
    public float getSpeed() {
        return speed;
    }
    public Rectangle getFeetBounds() { return boundsFeet;  }
    public void jump() {
        this.velocityY = jumpSpeed;
        if (jumpSound != null) {
            jumpSound.play();
        }
    }
}
