package com.mygdx.jump;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlatformManager {
    private Array<Platform> platforms;
    private float nextSpawnY;
    private float rowHeight;
    private float worldWidth;
    private float scale;
    private int visibleRows;

    public PlatformManager(float worldWidth,
                           float scale,
                           float rowHeight,
                           int initialRows,
                           int visibleRows) {
        this.worldWidth  = worldWidth;
        this.scale       = scale;
        this.rowHeight   = rowHeight;
        this.visibleRows = visibleRows;
        platforms       = new Array<>();
        nextSpawnY      = 0;

        Platform.loadTextures(scale);

        // Generamos inicialmente `initialRows` filas
        for (int i = 0; i < initialRows; i++) {
            spawnRow(i);
            nextSpawnY += rowHeight;
        }
    }

    private void spawnRow(int rowIndex) {
        float y = rowIndex * rowHeight;

        // Calcula el ancho real de la plataforma (3 piezas)
        float pieceW    = Platform.getMiddleWidth(scale);
        float platformW = pieceW * 3;

        // Genera un X aleatorio tal que la plataforma quepa completamente
        float x = MathUtils.random(0f, worldWidth - platformW);

        platforms.add(new Platform(x, y, scale));
    }


    public void update(float cameraY) {
        float spawnLimitY = cameraY + visibleRows * rowHeight;
        while (nextSpawnY <= spawnLimitY) {
            int rowIndex = (int)(nextSpawnY / rowHeight);
            spawnRow(rowIndex);
            nextSpawnY += rowHeight;
        }
        // Elimina las viejas
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform p = platforms.get(i);
            if (p.y + p.height < cameraY - rowHeight) {
                platforms.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Platform p : platforms) p.render(batch);
    }

    public void dispose() {
        Platform.disposeTextures();
        platforms.clear();
    }

    public Array<Platform> getPlatforms() {
        return platforms;
    }

    public void setWorldWidth(float worldWidth) {
        this.worldWidth = worldWidth;
    }

}
