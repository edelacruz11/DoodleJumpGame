// PlatformManager.java
package com.mygdx.jump;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlatformManager {
    private Array<PlatformBase> items = new Array<>();
    private float worldWidth, rowHeight, viewHeight, scale;
    private int cloudStartRow, fullCloudRow;
    private int nextRowIndex;
    private float nextSpawnY;

    public PlatformManager(
        float worldWidth,
        float scale,
        float rowHeight,
        int cloudStartRow,
        int fullCloudRow,
        int initialRows,
        float viewHeight
    ) {
        this.worldWidth = worldWidth;
        this.scale = scale;
        this.rowHeight = rowHeight;
        this.cloudStartRow = cloudStartRow;
        this.fullCloudRow = fullCloudRow;
        this.viewHeight = viewHeight;
        this.nextRowIndex = 0;
        this.nextSpawnY = 0;

        Platform.loadTextures(scale);
        Cloud.loadTextures(scale);

        for (int i = 0; i < initialRows; i++) {
            spawnRow();
        }
    }

    private void spawnRow() {
        int row = nextRowIndex;
        float y = row * rowHeight;

        // Solo filas pares generan algo
        if (row % 2 != 0) {
            advanceRow();
            return;
        }

        if (row < cloudStartRow) {
            items.add(randomPlatform(y));
        } else if (row < fullCloudRow) {
            if (MathUtils.randomBoolean()) {
                items.add(randomCloud(y));
            } else {
                items.add(randomPlatform(y));
            }
        } else {
            items.add(randomCloud(y));
        }

        advanceRow();
    }

    private void advanceRow() {
        nextRowIndex++;
        nextSpawnY = nextRowIndex * rowHeight;
    }

    private Platform randomPlatform(float y) {
        float w = Platform.getMiddleWidth(scale) * 3;
        float x = MathUtils.random(0f, worldWidth - w);
        return new Platform(x, y, scale);
    }

    private Cloud randomCloud(float y) {
        float w = (Cloud.texLeft.getWidth()
            + Cloud.texMiddle.getWidth()
            + Cloud.texRight.getWidth()) * scale;
        float x = MathUtils.random(0f, worldWidth - w);
        return new Cloud(x, y, scale);
    }

    public void update(float cameraY) {
        float limit = cameraY + viewHeight;
        while (nextSpawnY <= limit) {
            spawnRow();
        }

        for (int i = items.size - 1; i >= 0; i--) {
            PlatformBase b = items.get(i);
            if (b.isVanished() || b.getY() + b.getHeight() < cameraY - rowHeight) {
                items.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (PlatformBase b : items) {
            b.render(batch);
        }
    }

    public Array<PlatformBase> getAllPlatforms() {
        return items;
    }

    public void dispose() {
        Platform.disposeTextures();
        Cloud.disposeTextures();
        items.clear();
    }

    public void setWorldWidth(float w) { this.worldWidth = w; }
    public void setViewHeight(float h) { this.viewHeight = h; }
}
