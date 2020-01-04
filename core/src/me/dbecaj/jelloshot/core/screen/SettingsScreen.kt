package me.dbecaj.jelloshot.core.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.GameAssetManager
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.GuiCam

@Singleton
class SettingsScreen @Inject() constructor(
        private val screenManager: ScreenManager,
        private val assetManager: GameAssetManager,
        private @GuiCam val guiCam: OrthographicCamera,
        private val spriteBatch: SpriteBatch,
        private val gamePreferences: GamePreferences
) : Screen {

    private val viewport = FitViewport(guiCam.viewportWidth, guiCam.viewportHeight, guiCam)
    private val stage: Stage = Stage(viewport, spriteBatch)
    private lateinit var table: Table
    private lateinit var slider: Slider

    override fun show() {
        // Setup input processor
        Gdx.input.inputProcessor = stage

        table = Table(assetManager.uiSkin()).apply {
            setFillParent(true)
            debug = false

            // Background
            val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
            bgPixmap.setColor(Color.OLIVE)
            bgPixmap.fill()
            val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
            background = textureRegionDrawableBg

            // Controls
            add(Label("Sound Volume", assetManager.uiSkin()))
            row()
            slider = Slider(0f, 1f, 0.02f, false, assetManager.uiSkin()).apply{
                value = gamePreferences.soundVolume

                addListener(object : ChangeListener() {
                    override fun changed(p0: ChangeEvent?, p1: Actor?) {
                        if (!slider.isDragging) {
                            assetManager.jumpSound().play(slider.value)
                        }
                    }
                })
            }
            add(slider).width(400F)
            row()

            // Buttons
            val buttonStyle = assetManager.uiSkin().get(TextButton.TextButtonStyle::class.java)
            add(TextButton("Back", buttonStyle).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        screenManager.showScreen(ScreenEnum.MAIN_MENU)
                    }
                })
            }).expandX().top().width(500F).height(100F).pad(16F)
        }
        stage.addActor(table)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {}

    override fun hide() {}

    override fun dispose() {
        gamePreferences.soundVolume = slider.value
        stage.dispose()
    }
}