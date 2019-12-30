package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
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
        private val injector: Injector
) {
    var score = 0
    var state = GameState.RUNNING

    val inputMultiplexer: InputMultiplexer

    init {
        inputMultiplexer = InputMultiplexer(playerControllerSystem.playerInputAdapter)
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
        score = 0
        injector.getInstance(LevelBuilder::class.java).reinitialize()

        state = GameState.RESTART
        unpause()
    }

    fun lose() {
        pause()

        state = GameState.LOSE
    }

    fun exit() {
        Gdx.app.exit()
    }
}