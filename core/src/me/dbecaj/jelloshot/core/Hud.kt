package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject

class Hud @Inject() constructor(
        private val spriteBatch: SpriteBatch,
        private @GuiCam val guiCam: OrthographicCamera,
        private val assetManager: GameAssetManager,
        private val gameManager: GameManager
) {

    private val viewport = FitViewport(guiCam.viewportWidth, guiCam.viewportHeight, guiCam)
    public val stage = Stage(viewport, spriteBatch)
    private val table: Table
    private val scoreLabel: Label

    init {
        scoreLabel = Label("Score: 0", assetManager.uiSkin())
        table = Table(assetManager.uiSkin()).apply {
            setFillParent(true)
            add(scoreLabel).pad(16F)

            left().top()
        }

        stage.addActor(table)
    }

    fun update(deltaTime: Float) {
        scoreLabel.setText("Score: ${gameManager.score}")

        stage.act(deltaTime)
    }

    fun draw() {
        stage.draw();
    }
}

class HudSystem @Inject() constructor(
        private val hud: Hud
) : EntitySystem() {

    override fun update(deltaTime: Float) {
        hud.update(deltaTime)
        hud.draw()
    }
}