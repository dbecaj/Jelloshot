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
    var username = ""
        set(value) {
            field = value

            // Update highscore if user exists in firebase
            db.collection("scoreboard").whereEqualTo("username", username).get().apply {
                addListener({}, {
                    if (isDone) {
                        val result = this.get()
                        // If user exists override his highscore with his online highscore
                        if (!result.documents.isEmpty()) {
                            println("Setting online highscore")
                            highscore = result.documents[0].getLong("score")!!
                        }
                    }
                })
            }
        }

    var highscore: Long = -1
        get() = pref.getLong("${username}.highscore")
        set(value) {
            pref.putLong("${username}.highscore", value)
            field = value
        }

    var soundVolume = -1f
        get() = pref.getFloat("${username}.soundVolume", 0.5f);
        set(value) {
            pref.putFloat("${username}.soundVolume", value)
            field = value
        }

    companion object {
        fun save() {
            Gdx.app.getPreferences("JelloShotPrefs").flush()
        }
    }
}