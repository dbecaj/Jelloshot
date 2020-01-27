package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.google.cloud.firestore.Firestore
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
    WIN,
    RESTART
}

@Singleton
class GameManager @Inject() constructor(
        private val playerControllerSystem: PlayerControllerSystem,
        private val gamePreferences: GamePreferences,
        private val injector: Injector
) {
    var score: Long = 0
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
        if (state == GameState.RUNNING || state == GameState.LOSE || state == GameState.WIN) return

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

    fun win() {
        pause()

        state = GameState.WIN
    }

    fun quit() {
        if (score > gamePreferences.highscore) {
            // Save score locally
            gamePreferences.highscore = score
        }

        // Save score to cloud
        val db = injector.getInstance(Firestore::class.java)
        db.collection("scoreboard").whereEqualTo("username", gamePreferences.username).get().apply {
            addListener({}, {
                if (isDone) {
                    val result = this.get()
                    // If user doesn't exist create new user
                    if (result.documents.isEmpty()) {
                        db.collection("scoreboard").add(mapOf(
                                "username" to gamePreferences.username,
                                "score" to gamePreferences.highscore
                        ))
                    }
                    else {
                        // Update the user score with the current highscore
                        if (score > gamePreferences.highscore) {
                            db.collection("scoreboard").document(result.documents[0].id).set(mapOf(
                                    "score" to gamePreferences.highscore
                            ))
                        }
                    }
                }
            })
        }

        score = 0
        state = GameState.RESTART
        injector.getInstance(Hud::class.java).hidePauseMenu()

        injector.getInstance(ScreenManager::class.java).showScreen(ScreenEnum.MAIN_MENU)
    }
}