package me.dbecaj.jelloshot

import com.badlogic.gdx.Game
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.inject.Guice
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.screen.ScreenEnum
import me.dbecaj.jelloshot.core.screen.ScreenManager
import java.io.FileInputStream

class JelloShotGame : Game() {

    companion object {
        lateinit var instance: JelloShotGame
            private set
    }

    override fun create() {
        instance = this

        // Initialize injector
        val injector = Guice.createInjector(GameModule())

        val screenManager = injector.getInstance(ScreenManager::class.java)
        screenManager.showScreen(ScreenEnum.MAIN_MENU)
    }

    override fun dispose() {
        GamePreferences.save()
    }
}
