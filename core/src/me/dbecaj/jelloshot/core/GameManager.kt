package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.screen.ScreenEnum
import me.dbecaj.jelloshot.core.screen.ScreenManager
import me.dbecaj.jelloshot.system.PlayerControllerSystem

enum class GameState {
    RUNNING,
    PAUSED,
    LOSE,
    RESTART
}

@Singleton
class GameManager @Inject() constructor(
        private val playerControllerSystem: PlayerControllerSystem,
        private val gamePreferences: GamePreferences,
        private val injector: Injector
) {
    var score = 0
    var state = GameState.RUNNING

    var inputMultiplexer: InputMultiplexer = InputMultiplexer()

    fun init() {
        inputMultiplexer = InputMultiplexer(injector.getInstance(Hud::class.java).stage,
                                            playerControllerSystem.playerInputAdapter)
        Gdx.input.inputProcessor = inputMultiplexer
    }

    fun pause() {
        if (state == GameState.PAUSED) return

        injector.getInstance(Hud::class.java).showPauseMenu()
        inputMultiplexer.removeProcessor(playerControllerSystem.playerInputAdapter)

        state = GameState.PAUSED
    }

    fun unpause() {
        if (state == GameState.RUNNING || state == GameState.LOSE) return

        injector.getInstance(Hud::class.java).hidePauseMenu()
        inputMultiplexer.addProcessor(playerControllerSystem.playerInputAdapter)

        state = GameState.RUNNING
    }

    fun restart() {
        if (score > gamePreferences.highscore) {
            gamePreferences.highscore = score
        }
        score = 0
        injector.getInstance(LevelBuilder::class.java).reinitialize()

        state = GameState.RESTART
        unpause()
    }

    fun lose() {
        pause()

        state = GameState.LOSE
    }

    fun quit() {
        if (score > gamePreferences.highscore) {
            gamePreferences.highscore = score
        }
        score = 0
        state = GameState.RESTART
        injector.getInstance(Hud::class.java).hidePauseMenu()

        injector.getInstance(ScreenManager::class.java).showScreen(ScreenEnum.MAIN_MENU)
    }
}