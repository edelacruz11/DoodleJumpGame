package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen {
    private final Texture background, gameOver, menu;
    private final Rectangle menuBounds;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float worldWidth, worldHeight;
    private static final float GAME_MENU_SCALE = 7.5f;
    private static final float GAME_MENU_Y     = 0.45f;

    public GameOverScreen() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        background = new Texture("background/background2.png");
        gameOver = new Texture("gui/game_over.png");
        menu = new Texture("gui/menu.png");

        float mw = menu.getWidth() * GAME_MENU_SCALE;
        float mh = menu.getHeight() * GAME_MENU_SCALE;
        float mx = (worldWidth - mw) * 0.5f;
        float my = worldHeight * GAME_MENU_Y - mh * 0.5f;
        menuBounds = new Rectangle(mx, my, mw, mh);
    }

    // Ajusta viewport al cambiar tamaño
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        float mw = menu.getWidth() * GAME_MENU_SCALE;
        float mh = menu.getHeight() * GAME_MENU_SCALE;
        float mx = (worldWidth - mw) * 0.5f;
        float my = worldHeight * GAME_MENU_Y - mh * 0.5f;
        menuBounds.set(mx, my, mw, mh);
    }

    // Devuelve true si se pulsa play
    public boolean updateAndDraw(SpriteBatch batch) {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if (menuBounds.contains(touch.x, touch.y)) {
                if (touch.y > menuBounds.y + menuBounds.height/2f) return true;
                else Gdx.app.exit();
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,
            0, camera.position.y - worldHeight/2f,
            worldWidth, worldHeight);
        // game over label
        float gow = gameOver.getWidth();
        float goh = gameOver.getHeight();
        batch.draw(gameOver,
            worldWidth*0.5f - gow*0.5f,
            camera.position.y + worldHeight*0.2f);
        // menú
        batch.draw(menu, menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height);
        batch.end();

        return false;
    }

    public void dispose() {
        background.dispose();
        gameOver.dispose();
        menu.dispose();
    }
}
