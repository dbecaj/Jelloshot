package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class Hud @Inject() constructor(
        private val spriteBatch: SpriteBatch,
        private @GuiCam val guiCam: OrthographicCamera,
        private val assetManager: GameAssetManager,
        private val gameManager: GameManager
) {

    private val viewport = FitViewport(guiCam.viewportWidth, guiCam.viewportHeight, guiCam)
    val stage = Stage(viewport, spriteBatch)
    private val table: Table
    private val pauseMenu: Table
    private val scoreLabel: Label

    init {
        // Add stage input processor
        gameManager.inputMultiplexer.addProcessor(0, stage)

        // Create pause menu
        pauseMenu = Table(assetManager.uiSkin()).apply {
            setDebug(false)
            width = 400F
            height = 500F

            left().top()
            val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
            bgPixmap.setColor(Color.LIGHT_GRAY)
            bgPixmap.fill()
            val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
            background = textureRegionDrawableBg
            setPosition(viewport.screenWidth/2F - width/2, viewport.screenHeight/2F - height/2F)

            add(TextButton("Restart", assetManager.uiSkin()).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        gameManager.restart()
                    }
                })
            }).expandX().top().center().width(350F).height(50F).pad(16F)
            row()
            add(TextButton("Exit", assetManager.uiSkin()).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        gameManager.exit()
                    }
                })
            }).expand().bottom().width(350F).height(50F).pad(16F)
        }

        scoreLabel = Label("Score: 0", assetManager.uiSkin())
        table = Table(assetManager.uiSkin()).apply {
            setFillParent(true)
            setDebug(false)

            left().top()
            add(scoreLabel).left().top().pad(16F).expandX()
            add(TextButton("Pause", assetManager.uiSkin()).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        if (gameManager.state == GameState.RUNNING) {
                            gameManager.pause()
                        }
                        else if (gameManager.state == GameState.PAUSED) {
                            gameManager.unpause()
                        }
                    }
                })
            }).pad(16F)
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

    fun showPauseMenu() {
        stage.addActor(pauseMenu)
    }

    fun hidePauseMenu() {
        pauseMenu.remove()
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