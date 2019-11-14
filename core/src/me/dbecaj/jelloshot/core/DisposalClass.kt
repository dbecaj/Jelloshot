package me.dbecaj.jelloshot.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Inject

class DisposalClass @Inject constructor(
        private val spriteBatch: SpriteBatch
) {
    fun dispose() {
        spriteBatch.dispose()
    }
}