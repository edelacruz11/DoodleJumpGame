package com.mygdx.jump;

import com.badlogic.gdx.Screen;

public class LoadingScreen implements Screen {
    private final Main game;
    private final Assets assets;

    public LoadingScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
    }

    @Override
    public void show() {
        // Iniciar carga asíncrona: assets.manager.update()
    }

    @Override
    public void render(float delta) {
        // Dibujar texto “Cargando…”
        // if (assets.manager.update()) → pasar a PlayScreen
    }

    // Métodos no implementados para esta fase:
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
