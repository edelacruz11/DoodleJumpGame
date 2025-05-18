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

    private Texture[] numbers = new Texture[10];
    private int score, lastRowReached;
    private float rowHeight;
    private final float MARGIN = 20f;
    private final float DIGIT_SCALE   = 2.5f;
    private final float DIGIT_SPACING = 0.5f;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        background = new Texture("background/background2.png");
        font = new BitmapFont();
        font.getData().setScale(2f);

        ground = new Ground();
        for (int i = 0; i < 10; i++) {
            numbers[i] = new Texture("extras/numbers/" + i + ".png");
        }

        crearJuego();
        estadoActual = Estado.INICIO;
    }

    private void crearJuego() {
        float scalePlat = 1.2f;
        Platform.loadTextures(scalePlat);
        rowHeight = Platform.getMiddleHeight(scalePlat) * 2f;

        platformManager = new PlatformManager(
            worldWidth,
            scalePlat,
            rowHeight,
            /*cloudStartRow=*/100,
            /*fullCloudRow=*/200,
            /*initialRows=*/0,
            worldHeight
        );

        player = new Player("beige", ground.getHeight());
        player.setScale(2f);
        player.setPosition(
            worldWidth * 0.5f - player.getWidth() * 0.5f,
            ground.getHeight()
        );

        camera.position.y = worldHeight / 2f;
        camera.update();

        score = 0;
        lastRowReached = -1;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
            // Input izquierdo/derecho
            if (Gdx.input.isTouched()) {
                int tx = Gdx.input.getX();
                if (tx < Gdx.graphics.getWidth() * 0.5f) {
                    player.moveLeft();
                } else {
                    player.moveRight();
                }
            } else {
                player.stop();
            }

            player.update(delta);

            // Colisiones con plataformas y nubes
            Rectangle feet = player.getFeetBounds();
            for (PlatformBase b : platformManager.getAllPlatforms()) {
                if (!b.isVanished() && player.getVelocityY() <= 0) {
                    Rectangle top = new Rectangle(
                        b.getX(),
                        b.getY() + b.getHeight(),
                        b.getWidth(),
                        1f
                    );
                    if (feet.overlaps(top)) {
                        player.jump();
                        b.onStep();
                        break;
                    }
                }
            }

            // Mover cámara si sube
            if (player.getY() > camera.position.y) {
                camera.position.y = player.getY();
            }

            // Game over si cae por debajo
            float bottom = camera.position.y - worldHeight * 0.5f;
            if (player.getY() + player.getHeight() < bottom) {
                estadoActual = Estado.GAME_OVER;
            }

            // Actualizar puntuación por filas superadas
            int currentRow = (int)(player.getY() / rowHeight);
            if (currentRow > lastRowReached) {
                score += currentRow - lastRowReached;
                lastRowReached = currentRow;
            }

            camera.update();
            platformManager.update(bottom);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (estadoActual == Estado.INICIO) {
            batch.draw(image, worldWidth * 0.175f, worldHeight * 0.65f);
            font.draw(batch,
                "Toca para empezar",
                worldWidth * 0.15f,
                worldHeight * 0.4f);
            if (Gdx.input.justTouched()) {
                estadoActual = Estado.JUEGO;
            }

        } else if (estadoActual == Estado.JUEGO) {
            float bgY = camera.position.y - worldHeight * 0.5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);
            platformManager.render(batch);
            ground.render(batch);
            player.render(batch);

            // Dibuja puntuación
            float dW = numbers[0].getWidth() * DIGIT_SCALE;
            float dH = numbers[0].getHeight() * DIGIT_SCALE;
            float spacing = dW * DIGIT_SPACING;
            float startX = camera.position.x - worldWidth * 0.5f + MARGIN;
            float startY = camera.position.y + worldHeight * 0.5f - MARGIN - dH;
            String txt = Integer.toString(score);
            for (int i = 0; i < txt.length(); i++) {
                int d = txt.charAt(i) - '0';
                batch.draw(numbers[d],
                    startX + i * spacing,
                    startY,
                    dW,
                    dH);
            }

        } else { // GAME_OVER
            float bgY = camera.position.y - worldHeight * 0.5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);
            font.draw(batch,
                "Game Over",
                camera.position.x - 60,
                camera.position.y + 20);
            font.draw(batch,
                "Pulsa para reiniciar",
                camera.position.x - 100,
                camera.position.y - 20);
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
        for (Texture t : numbers) t.dispose();
    }
}
