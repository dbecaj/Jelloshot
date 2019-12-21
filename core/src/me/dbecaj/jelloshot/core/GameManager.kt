package me.dbecaj.jelloshot.core

import com.google.inject.Inject
import com.google.inject.Singleton

enum class GameState {
    RUNNING,
    PAUSED
}

@Singleton
class GameManager @Inject() constructor() {
    var score = 0
    var state = GameState.RUNNING
}