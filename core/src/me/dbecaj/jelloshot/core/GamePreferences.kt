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
        get() = pref.getInteger("${username}.highscore", 0);
        set(value) {
            pref.putInteger("${username}.highscore", value)
            field = value
        }

    var soundVolume = -1f
        get() = pref.getFloat("${username}.soundVolume", 0.5f);
        set(value) {
            pref.putFloat("${username}.soundVolume", value)
            field = value
        }

    var username = ""

    companion object {
        fun save() {
            Gdx.app.getPreferences("JelloShotPrefs").flush()
        }
    }
}