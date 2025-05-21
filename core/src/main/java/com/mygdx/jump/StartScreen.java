package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StartScreen {
    private final Texture background, logo, menu;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float worldWidth, worldHeight;

    // Selector de personaje
    private final String[] colors = {"beige","green","pink","purple","yellow"};
    private final Texture[] helmets = new Texture[colors.length];
    private final Rectangle[] helmetBounds = new Rectangle[colors.length];
    private int selectedIndex = 0;

    // Ajustes de layout
    private static final float MENU_SCALE = 7.5f;
    private static final float MENU_Y = 0.45f;
    private static final float LOGO_SCALE = 2f;
    private static final float LOGO_Y = 0.8f;
    private static final float HELMET_SIZE = 190f;
    private static final float HELMET_SPACING = 18f;

    public StartScreen() {
        camera   = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        worldWidth  = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        background = new Texture("background/background2.png");
        logo = new Texture("gui/logo.png");
        menu = new Texture("gui/menu.png");

        // Carga assets de cascos
        for (int i = 0; i < colors.length; i++) {
            helmets[i] = new Texture("player/" + colors[i] + "/helmet.png");
        }

        // Calcula posición y del menú
        float menuH = menu.getHeight() * MENU_SCALE;
        float menuYpos = worldHeight * MENU_Y - menuH/2f;

        // Posición de cascos: justo debajo del menú
        float totalW = colors.length * HELMET_SIZE + (colors.length - 1) * HELMET_SPACING;
        float startX = worldWidth/2f - totalW/2f;
        float yHelmets = menuYpos - HELMET_SIZE - 20f;

        for (int i = 0; i < colors.length; i++) {
            float x = startX + i * (HELMET_SIZE + HELMET_SPACING);
            helmetBounds[i] = new Rectangle(x, yHelmets, HELMET_SIZE, HELMET_SIZE);
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        worldWidth  = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        camera.position.set(worldWidth/2f, worldHeight/2f, 0);
        camera.update();

        // Recalcula igual que en el constructor
        float menuH = menu.getHeight() * MENU_SCALE;
        float menuYpos = worldHeight * MENU_Y - menuH/2f;
        float totalW = colors.length * HELMET_SIZE + (colors.length - 1) * HELMET_SPACING;
        float startX = worldWidth/2f - totalW/2f;
        float yHelmets = menuYpos - HELMET_SIZE - 20f;

        for (int i = 0; i < colors.length; i++) {
            float x = startX + i * (HELMET_SIZE + HELMET_SPACING);
            helmetBounds[i].set(x, yHelmets, HELMET_SIZE, HELMET_SIZE);
        }
    }

    // Devuelve true si presionas Play
    public boolean updateAndDraw(SpriteBatch batch) {
        if (Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            // Selección de casco
            for (int i = 0; i < helmetBounds.length; i++) {
                if (helmetBounds[i].contains(touch.x, touch.y)) {
                    selectedIndex = i;
                    return false;
                }
            }

            // Botón Play / Salir
            float menuW = menu.getWidth() * MENU_SCALE;
            float menuH = menu.getHeight() * MENU_SCALE;
            Rectangle menuBounds = new Rectangle(
                (worldWidth - menuW)/2f,
                worldHeight * MENU_Y - menuH/2f,
                menuW, menuH
            );
            if (menuBounds.contains(touch.x, touch.y)) {
                if (touch.y > menuBounds.y + menuBounds.height/2f) return true;
                else Gdx.app.exit();
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fondo y logo
        batch.draw(background, 0, 0, worldWidth, worldHeight);
        float lw = logo.getWidth() * LOGO_SCALE;
        float lh = logo.getHeight() * LOGO_SCALE;
        batch.draw(logo, worldWidth/2f - lw/2f, worldHeight*LOGO_Y - lh/2f, lw, lh);

        // Dibujar menú antes de los cascos
        float mw = menu.getWidth() * MENU_SCALE;
        float mh = menu.getHeight() * MENU_SCALE;
        batch.draw(menu, (worldWidth - mw)/2f, worldHeight*MENU_Y - mh/2f, mw, mh);


        for (int i = 0; i < helmets.length; i++) {
            // atenua los cascos no seleccionados
            batch.setColor(i == selectedIndex ? 1f : 0.5f, 1f, 1f, 1f);
            batch.draw(helmets[i],
                helmetBounds[i].x,
                helmetBounds[i].y,
                HELMET_SIZE, HELMET_SIZE);
        }
        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
        return false;
    }

    public String getSelectedColor() {
        return colors[selectedIndex];
    }

    public void dispose() {
        background.dispose();
        logo.dispose();
        menu.dispose();
        for (Texture t : helmets) t.dispose();
    }
}
