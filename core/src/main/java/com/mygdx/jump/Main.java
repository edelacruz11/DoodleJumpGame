package com.mygdx.jump;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Ground ground;
    private Player player;
    private PlatformManager platformManager;
    private StartScreen startScreen;
    private GameOverScreen gameOverScreen;
    private ScoreManager scoreManager;

    private OrthographicCamera camera;
    private Viewport viewport;
    private float worldWidth, worldHeight;
    private enum Estado { INICIO, JUEGO, GAME_OVER }
    private Estado estadoActual;
    private Texture[] numbers;
    private static final float DIGIT_SCALE = 2.0f;
    private static final float HIGH_SCALE = 1.5f;
    private static final float DIGIT_SPACING = 0.6f;
    private static final float TOP_MARGIN = 30f;
    private static final float SESSION_Y_OFFSET = -20f;
    private static final float HIGH_Y_OFFSET = -10f;
    private int sessionMaxRow;

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
        background = new Texture("background/background2.png");
        ground = new Ground();
        startScreen = new StartScreen();
        gameOverScreen = new GameOverScreen();
        scoreManager = new ScoreManager();

        numbers = new Texture[10];
        for (int i = 0; i < 10; i++) {
            numbers[i] = new Texture("extras/numbers/" + i + ".png");
        }

        // Inicia en pantalla de inicio sin juego creado
        estadoActual = Estado.INICIO;
    }

    // Crea un nuevo juego usando el color de jugador seleccionado
    private void crearJuego(String color) {
        // Reinicia cámara
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        // Plataformas
        float scalePlat = 1.2f;
        Platform.loadTextures(scalePlat);
        float rowH = Platform.getMiddleHeight(scalePlat)*2f;
        platformManager = new PlatformManager(
            worldWidth, scalePlat, rowH,
            /*cloudStartRow=*/100,
            /*fullCloudRow=*/200,
            /*initialRows=*/0,
            worldHeight
        );

        // Jugador con el color elegido
        player = new Player(color, ground.getHeight());
        player.setScale(2f);
        player.setPosition(
            worldWidth*0.5f - player.getWidth()*0.5f,
            ground.getHeight()
        );

        // Reset de puntuación
        sessionMaxRow = 0;
        camera.update();
    }

    private void crearJuego() {
        crearJuego(startScreen.getSelectedColor());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        camera.position.set(worldWidth/2f, camera.position.y, 0);
        camera.update();

        if (platformManager != null) {
            platformManager.setWorldWidth(worldWidth);
            platformManager.setViewHeight(worldHeight);
        }
        startScreen.resize(width, height);
        gameOverScreen.resize(width, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(.15f,.15f,.2f,1f);

        if (estadoActual == Estado.INICIO) {
            // Si pulsa Play, crea juego con color seleccionado
            if (startScreen.updateAndDraw(batch)) {
                crearJuego(startScreen.getSelectedColor());
                estadoActual = Estado.JUEGO;
            }

        } else if (estadoActual == Estado.JUEGO) {
            float delta = Gdx.graphics.getDeltaTime();

            /*
            // Input horizontal por taps
            if (Gdx.input.isTouched()) {
                int tx = Gdx.input.getX();
                if (tx < Gdx.graphics.getWidth()*0.5f) player.moveLeft();
                else player.moveRight();
            } else {
                player.stop();
            }
             */

            // Input horizontal por acelerometro
            float accelX = Gdx.input.getAccelerometerX();
            float maxSpeed = player.getSpeed();
            // Factor de sensibilidad
            float factor = 250f;
            float vx = MathUtils.clamp(-accelX * factor, -maxSpeed, maxSpeed);
            player.setVelocityX(vx);


            // Lógica de jugador y colisiones
            player.update(delta);

            // Wrap-around horizontal
            float px = player.getX();
            float pw = player.getWidth();
            if (px + pw < 0) {
                // salio por la izquierda, entra por la derecha
                player.setPosition(worldWidth, player.getY());
            } else if (px > worldWidth) {
                // salio por la derecha, entra por la izquierda
                player.setPosition(-pw, player.getY());
            }

            Rectangle feet = player.getFeetBounds();
            for (PlatformBase b : platformManager.getAllPlatforms()) {
                if (!b.isVanished() && player.getVelocityY() <= 0) {
                    Rectangle top = new Rectangle(
                        b.getX(), b.getY()+b.getHeight(),
                        b.getWidth(), 1f
                    );
                    if (feet.overlaps(top)) {
                        player.jump();
                        b.onStep();
                        break;
                    }
                }
            }

            // Actualiza sessionMaxRow
            int currentRow = (int)(player.getY() / (Platform.getMiddleHeight(1.2f)*2f));
            if (currentRow > sessionMaxRow) sessionMaxRow = currentRow;

            // Mueve cámara al subir
            if (player.getY() > camera.position.y) camera.position.y = player.getY();
            float bottom = camera.position.y - worldHeight*0.5f;

            // Game over si cae
            if (player.getY()+player.getHeight() < bottom) {
                scoreManager.updateHighScore(sessionMaxRow);
                estadoActual = Estado.GAME_OVER;
            }

            // Actualiza mundo y dibuja
            platformManager.update(bottom);
            camera.update();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            float bgY = camera.position.y - worldHeight*0.5f;
            batch.draw(background, 0, bgY, worldWidth, worldHeight);
            platformManager.render(batch);
            ground.render(batch, worldWidth);
            player.render(batch);

            //Dibuja session score
            String txt = Integer.toString(sessionMaxRow);
            float dW = numbers[0].getWidth() * DIGIT_SCALE;
            float dH = numbers[0].getHeight() * DIGIT_SCALE;
            float slot = dW * DIGIT_SPACING;
            float totalW = dW + (txt.length() - 1) * slot;
            float sx = camera.position.x - totalW * 0.5f;
            float sy = camera.position.y + worldHeight * 0.5f - TOP_MARGIN - dH * 1.5f + SESSION_Y_OFFSET;
            for (int i = 0; i < txt.length(); i++) {
                int d = txt.charAt(i) - '0';
                batch.draw(numbers[d], sx + i * slot, sy, dW, dH);
            }

            //Dibuja high score
            String hs = Integer.toString(scoreManager.getHigh());
            float hW = numbers[0].getWidth() * HIGH_SCALE;
            float hH = numbers[0].getHeight() * HIGH_SCALE;
            float hSlot = hW * DIGIT_SPACING;
            float totalHW = hW + (hs.length() - 1) * hSlot;
            float hx = camera.position.x - totalHW * 0.5f;
            float hy = sy + dH + (TOP_MARGIN * 0.5f) + HIGH_Y_OFFSET;
            for (int i = 0; i < hs.length(); i++) {
                int d = hs.charAt(i) - '0';
                batch.draw(numbers[d], hx + i * hSlot, hy, hW, hH);
            }

            batch.end();

        } else { // GAME_OVER
            if (gameOverScreen.updateAndDraw(batch)) {
                crearJuego();
                estadoActual = Estado.JUEGO;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        ground.dispose();
        player.dispose();
        platformManager.dispose();
        startScreen.dispose();
        gameOverScreen.dispose();
        scoreManager.dispose();
        for (Texture t : numbers) t.dispose();
    }
}
