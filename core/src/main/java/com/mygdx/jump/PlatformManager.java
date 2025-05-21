package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
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
        this.cloudStartRow= cloudStartRow;
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
        PlatformBase p;

        if (row % 2 != 0) {
            advanceRow();
            return;
        }

        boolean makeCloud = row >= cloudStartRow
            && (row < fullCloudRow
            ? MathUtils.randomBoolean()
            : true);

        if (makeCloud) {
            Cloud c = randomCloud(y);

            // Mueve 50% de las nubes a partir de 300, y todas a partir de 400
            if (row >= 300 && (row < 400 ? MathUtils.randomBoolean(0.5f) : true)) {
                //velocidad de las nubes
                float speed = MathUtils.random(100f, 150f);
                if (MathUtils.randomBoolean()) speed = -speed;
                c.startMoving(speed);
            }
            p = c;
        } else {
            p = randomPlatform(y);
        }

        items.add(p);
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
        float w = (Cloud.texLeft.getWidth() + Cloud.texMiddle.getWidth() + Cloud.texRight.getWidth()) * scale;
        float x = MathUtils.random(0f, worldWidth - w);
        return new Cloud(x, y, scale);
    }

    public void update(float cameraY) {
        float limit = cameraY + viewHeight;
        while (nextSpawnY <= limit) {
            spawnRow();
        }

        // Actualiza y elimina
        for (int i = items.size - 1; i >= 0; i--) {
            PlatformBase b = items.get(i);
            if (b.isVanished() ||
                b.getY() + b.getHeight() < cameraY - rowHeight) {
                items.removeIndex(i);
            } else if (b instanceof Cloud) {
                // mueve la nube si tiene velocidad
                ((Cloud)b).update(Gdx.graphics.getDeltaTime(), worldWidth);
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
