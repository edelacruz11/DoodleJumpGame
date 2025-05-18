package com.mygdx.jump;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlatformManager {
    private Array<Platform> platforms;
    private float nextSpawnY;
    private float rowHeight;
    private float worldWidth;
    private float viewHeight;
    private float scale;
    private int nextRowIndex;

    public PlatformManager(float worldWidth,
                           float scale,
                           float rowHeight,
                           int initialRows,
                           float viewHeight) {
        this.worldWidth = worldWidth;
        this.scale = scale;
        this.rowHeight = rowHeight;
        this.viewHeight = viewHeight;
        platforms = new Array<>();

        nextSpawnY = 0;
        nextRowIndex = 0;

        Platform.loadTextures(scale);

        for (int i = 0; i < initialRows; i++) {
            spawnNextRow();
        }
    }

    private void spawnNextRow() {
        float y = nextRowIndex * rowHeight;

        // una plataforma en filas pares
        if (nextRowIndex % 2 == 0) {
            float pieceW = Platform.getMiddleWidth(scale);
            float platformW = pieceW * 3;

            float x = MathUtils.random(0f, worldWidth - platformW);
            platforms.add(new Platform(x, y, scale));
        }

        nextRowIndex++;
        nextSpawnY = nextRowIndex * rowHeight;
    }

    public void update(float cameraY) {
        float spawnLimitY = cameraY + viewHeight;
        while (nextSpawnY <= spawnLimitY) {
            spawnNextRow();
        }

        // Elimina plataformas que queden por debajo
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

    public void setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
    }
}
