package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class GamePreferences @Inject() constructor() {

    private val prefFileName = "JelloShotPrefs";

    private val pref = Gdx.app.getPreferences("JelloShotPrefs");

    var highscore = -1
        get() = pref.getInteger("highscore", 0);
        set(value) {
            pref.putInteger("highscore", value)
            field = value
        }

    companion object {
        fun save() {
            Gdx.app.getPreferences("JelloShotPrefs").flush()
        }
    }
}