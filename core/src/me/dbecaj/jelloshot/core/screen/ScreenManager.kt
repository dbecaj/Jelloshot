package me.dbecaj.jelloshot.core.screen

import com.badlogic.gdx.Screen
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import me.dbecaj.jelloshot.JelloShotGame

enum class ScreenEnum {
    GAME,
    MAIN_MENU,
    SETTINGS,
    LEVELS
}

@Singleton
class ScreenManager @Inject() constructor(
        private val injector: Injector
) {

    private val game: JelloShotGame = JelloShotGame.instance
    private val screens: Map<ScreenEnum, Class<out Screen>>

    init {
        screens = mapOf(
                ScreenEnum.GAME to GameScreen::class.java,
                ScreenEnum.MAIN_MENU to MainMenuScreen::class.java,
                ScreenEnum.SETTINGS to SettingsScreen::class.java,
                ScreenEnum.LEVELS to LevelScreen::class.java
        )
    }

    fun showScreen(screenEnum: ScreenEnum) {
        val currentScreen = game.screen

        game.screen = injector.getInstance(screens[screenEnum])

        currentScreen?.dispose()
    }
}