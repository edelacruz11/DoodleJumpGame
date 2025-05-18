// Main.java
package com.mygdx.jump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image, background;
    private BitmapFont font;
    private Ground ground;
    private Player player;
    private PlatformManager platformManager;

    private OrthographicCamera camera;
    private Viewport viewport;
    private float worldWidth, worldHeight;

    private enum Estado { INICIO, JUEGO, GAME_OVER }
    private Estado estadoActual;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        worldWidth  = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        background = new Texture("background/background.jpg");
        font = new BitmapFont();
        font.getData().setScale(2f);

        ground = new Ground();
        crearJuego();

        estadoActual = Estado.INICIO;
    }

    private void crearJuego() {
        float scalePlat = 1.2f;
        Platform.loadTextures(scalePlat);
        float rowH = Platform.getMiddleHeight(scalePlat) * 2f;

        platformManager = new PlatformManager(worldWidth, scalePlat, rowH, 0, worldHeight);

        player = new Player("beige", ground.getHeight());
        player.setScale(2f);
        player.setPosition(
            worldWidth * .5f - player.getWidth() * .5f,
            ground.getHeight()
        );

        camera.position.y = worldHeight / 2f;
        camera.update();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, camera.position.y, 0);
        camera.update();
        platformManager.setWorldWidth(worldWidth);
        platformManager.setViewHeight(worldHeight);
    }

    @Override
    public void render() {
        ScreenUtils.clear(.15f, .15f, .2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();

        if (estadoActual == Estado.JUEGO) {
            if (Gdx.input.isTouched()) {
                int tx = Gdx.input.getX();
                if (tx < Gdx.graphics.getWidth() * .5f) player.moveLeft();
                else                                   player.moveRight();
            } else {
                player.stop();
            }
            player.update(delta);

            // Colisiones con plataformas
            Rectangle feet = player.getFeetBounds();
            for (Platform p : platformManager.getPlatforms()) {
                if (player.getVelocityY() <= 0) {
                    Rectangle topPlat = new Rectangle(p.x, p.y + p.height, p.width, 1f);
                    if (feet.overlaps(topPlat)) {
                        player.jump();
                        break;
                    }
                }
            }

            // Mover cámara si el jugador sube
            if (player.getY() > camera.position.y) {
                camera.position.y = player.getY();
            }

            // Verificar si el jugador cayó al vacío
            float bottomCameraY = camera.position.y - worldHeight * .5f;
            if (player.getY() + player.getHeight() < bottomCameraY) {
                estadoActual = Estado.GAME_OVER;
            }

            camera.update();
            platformManager.update(bottomCameraY);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (estadoActual == Estado.INICIO) {
            batch.draw(image, worldWidth * .175f, worldHeight * .65f);
            font.draw(batch, "Toca para empezar", worldWidth * .15f, worldHeight * .4f);
            if (Gdx.input.justTouched()) estadoActual = Estado.JUEGO;
        } else if (estadoActual == Estado.JUEGO) {
            float bgY = camera.position.y - worldHeight * .5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);
            platformManager.render(batch);
            ground.render(batch);
            player.render(batch);
        } else if (estadoActual == Estado.GAME_OVER) {
            float bgY = camera.position.y - worldHeight * .5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);
            font.draw(batch, "Game Over", worldWidth * .4f, camera.position.y);
            font.draw(batch, "Pulsa para reiniciar", worldWidth * .3f, camera.position.y - 40);

            if (Gdx.input.justTouched()) {
                crearJuego();
                estadoActual = Estado.INICIO;
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        background.dispose();
        font.dispose();
        ground.dispose();
        player.dispose();
        platformManager.dispose();
    }
}
