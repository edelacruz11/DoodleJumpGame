// Main.java
package com.mygdx.jump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private SpriteBatch     batch;
    private Texture         image, background;
    private BitmapFont      font;
    private Ground          ground;
    private Player          player;
    private PlatformManager platformManager;

    private OrthographicCamera camera;
    private Viewport           viewport;
    private float              worldWidth, worldHeight;

    private enum Estado { INICIO, JUEGO }
    private Estado estadoActual;

    @Override
    public void create() {
        // 1) Cámara + viewport
        camera   = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        worldWidth  = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        // 2) CARGA primero las texturas estáticas de Plataforma
        float scalePlat = 1.2f;
        Platform.loadTextures(scalePlat);

        // 3) Ahora ya puedes calcular rowHeight y visibleRows SIN null
        float rowH        = Platform.getMiddleHeight(scalePlat) * 2f;
        int visibleRows   = (int)(worldHeight / rowH) + 1;
        int initialRows   = visibleRows + 1;                // UNA FILA MÁS que visible
        platformManager   = new PlatformManager(
            worldWidth,
            scalePlat,
            rowH,
            initialRows,
            visibleRows
        );


        // 5) Resto de tu inicialización: batch, image, background, font...
        batch      = new SpriteBatch();
        image      = new Texture("libgdx.png");
        background = new Texture("background/background.jpg");
        font       = new BitmapFont();
        font.getData().setScale(2f);

        // 6) Ground y Player
        ground = new Ground();
        player = new Player("beige", ground.getHeight());
        player.setScale(2f);
        player.setPosition(
            worldWidth * 0.5f - player.getWidth() * 0.5f,
            ground.getHeight()
        );

        estadoActual = Estado.INICIO;
    }


    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        worldWidth  = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        // Importante: actualizamos el worldWidth en el manager
        platformManager.setWorldWidth(worldWidth);
    }


    @Override
    public void render() {
        ScreenUtils.clear(.15f, .15f, .2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();

        if (estadoActual == Estado.JUEGO) {
            // Input lateral
            if (Gdx.input.isTouched()) {
                int tx = Gdx.input.getX();
                if (tx < Gdx.graphics.getWidth() * .5f)
                    player.moveLeft();
                else
                    player.moveRight();
            } else {
                player.stop();
            }

            player.update(delta);

            // Detección de colisión en pies para salto sobre plataformas
            for (Platform p : platformManager.getPlatforms()) {
                if (player.getVelocityY() <= 0) {
                    float feetY     = player.getY();
                    float topY      = p.y + p.height;
                    boolean yHit    = feetY <= topY && feetY >= p.y;
                    boolean xOverlap = player.getX() + player.getWidth() > p.x
                        && player.getX() < p.x + p.width;
                    if (yHit && xOverlap) {
                        player.jump();
                        break;
                    }
                }
            }

            // Cámara: solo sube con el jugador
            if (player.getY() > camera.position.y) {
                camera.position.y = player.getY();
            }
            camera.update();

            // Generar y limpiar plataformas
            float camBottomY = camera.position.y - worldHeight * .5f;
            platformManager.update(camBottomY);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (estadoActual == Estado.INICIO) {
            batch.draw(image, worldWidth * .175f, worldHeight * .65f);
            font.draw(batch, "Toca para empezar",
                worldWidth * .15f, worldHeight * .4f);
            if (Gdx.input.justTouched()) estadoActual = Estado.JUEGO;

        } else {
            // Dibuja fondo centrado en cámara
            float bgY = camera.position.y - worldHeight * .5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);

            platformManager.render(batch);
            ground.render(batch);
            player.render(batch);
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
