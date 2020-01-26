package me.dbecaj.jelloshot

import com.badlogic.gdx.Game
import com.google.cloud.firestore.Firestore
import com.google.inject.Guice
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.screen.ScreenEnum
import me.dbecaj.jelloshot.core.screen.ScreenManager

class JelloShotGame : Game() {

    companion object {
        lateinit var instance: JelloShotGame
            private set
    }

    override fun create() {
        instance = this

        // Initialize injector
        val injector = Guice.createInjector(GameModule())

        // Connect to firebase (by getting the instance we call DI guice init)
        injector.getInstance(Firestore::class.java)

        val screenManager = injector.getInstance(ScreenManager::class.java)
        screenManager.showScreen(ScreenEnum.MAIN_MENU)
    }

    override fun dispose() {
        GamePreferences.save()
    }
}
