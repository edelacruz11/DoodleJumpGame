package com.mygdx.jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ScoreManager {
    private static final String PREF_NAME = "MyGamePrefs";
    private static final String KEY_HIGH  = "highScore";

    private int highScore;
    private final Preferences prefs;

    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        highScore = prefs.getInteger(KEY_HIGH, 0);
    }

    public void updateHighScore(int newScore) {
        if (newScore > highScore) {
            highScore = newScore;
            prefs.putInteger(KEY_HIGH, highScore);
            prefs.flush();
        }
    }

    public int getHigh() {
        return highScore;
    }

    public void dispose() { }
}
