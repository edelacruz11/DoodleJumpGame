package com.mygdx.jump;

import com.badlogic.gdx.Screen;

public class PlayScreen implements Screen {
    private final Main game;
    private final Assets assets;
    private final Player player;
    private final DropButton dropButton;

    public PlayScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
        this.player = new Player();
        this.dropButton = new DropButton();
        // Inicializar cámara, batch, plataformas…
    }

    public void update(float delta) {
        // Lógica de jugador y plataformas
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Limpiar pantalla, dibujar background, tilemap, player y dropButton
    }

    // Métodos de Screen sin implementar aún:
    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
