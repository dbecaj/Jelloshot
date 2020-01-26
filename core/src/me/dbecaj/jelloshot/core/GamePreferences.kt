package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.google.cloud.firestore.Firestore
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class GamePreferences @Inject() constructor(
        private val db: Firestore
) {

    private val prefFileName = "JelloShotPrefs";

    private val pref = Gdx.app.getPreferences("JelloShotPrefs")
    class UserScore constructor(val username: String, val score: Long)

    var highscore = -1
        get() = pref.getInteger("highscore", 0);
        set(value) {
            pref.putInteger("highscore", value)
            field = value
        }

    var soundVolume = -1f
        get() = pref.getFloat("soundVolume", 0.5f);
        set(value) {
            pref.putFloat("soundVolume", value)
            field = value
        }

    companion object {
        fun save() {
            Gdx.app.getPreferences("JelloShotPrefs").flush()
        }
    }
}