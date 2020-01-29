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
import java.io.File
import java.util.*

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
    var totalScore: Long = 0
    var currentLevelScore: Long = 0
    var state = GameState.RUNNING
    var launchPower = 2F // Is the scalar value we multiply the movement vector for the player
    var launchPowerChangeDate = Date()

    var inputMultiplexer: InputMultiplexer = InputMultiplexer()

    fun init() {
        inputMultiplexer = InputMultiplexer(injector.getInstance(Hud::class.java).stage,
                                            playerControllerSystem.playerInputAdapter)
        Gdx.input.inputProcessor = inputMultiplexer
        state = GameState.RUNNING
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
        totalScore -= currentLevelScore
        currentLevelScore = 0
        launchPower = 2F
        injector.getInstance(LevelBuilder::class.java).reinitialize()

        state = GameState.RESTART
        unpause()
    }

    fun lose() {
        state = GameState.LOSE

        pause()
    }

    fun win() {
        state = GameState.WIN

        if (totalScore > gamePreferences.totalScore) {
            gamePreferences.totalScore = totalScore
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
                                "score" to gamePreferences.totalScore
                        ))
                    }
                    else {
                        val userScore = GamePreferences.UserScore(result.documents[0].getString("username")!!, result.documents[0].getLong("score")!!)
                        // Update the user score with the current highscore
                        if (userScore.score < gamePreferences.totalScore) {
                            println("Updating user score ${gamePreferences.totalScore}")
                            db.collection("scoreboard").document(result.documents[0].id).set(mapOf(
                                    "username" to userScore.username,
                                    "score" to gamePreferences.totalScore
                            ))
                        }
                    }
                }
            })
        }

        pause()
    }

    // Move to the next level
    fun nextLevel(levelFile: File) {
        launchPower = 2F
        currentLevelScore = 0

        injector.getInstance(GameAssetManager::class.java).loadLevel(levelFile)
        val levelBuilder = injector.getInstance(LevelBuilder::class.java)
        levelBuilder.reinitialize()
        unpause()
    }

    fun quit() {
        totalScore = 0
        currentLevelScore = 0
        launchPower = 2F
        state = GameState.RESTART
        injector.getInstance(Hud::class.java).hidePauseMenu()

        injector.getInstance(ScreenManager::class.java).showScreen(ScreenEnum.MAIN_MENU)
    }
}