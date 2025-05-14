package com.mygdx.jump;

import com.badlogic.gdx.assets.AssetManager;

public class Assets {
    public final AssetManager manager = new AssetManager();

    public void loadAll() {
        // Cargar sprites: gui/drop-on.png, drop-off.png
        // Cargar player/front.png, jump.png, duck.png
        // Cargar platforms/*.png
        // Cargar sounds/*.wav
        // manager.finishLoading() o carga asíncrona
    }

    public void dispose() {
        manager.dispose();
    }
}
