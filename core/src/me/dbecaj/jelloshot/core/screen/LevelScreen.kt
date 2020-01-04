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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.GameAssetManager
import me.dbecaj.jelloshot.core.GuiCam

@Singleton
class LevelScreen @Inject() constructor(
        private val screenManager: ScreenManager,
        private val assetManager: GameAssetManager,
        private @GuiCam val guiCam: OrthographicCamera,
        private val spriteBatch: SpriteBatch
) : Screen {

    private val viewport = FitViewport(guiCam.viewportWidth, guiCam.viewportHeight, guiCam)
    private val stage: Stage = Stage(viewport, spriteBatch)
    private lateinit var table: Table

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

            // Level selector buttons
            val buttonStyle = assetManager.uiSkin().get(TextButton.TextButtonStyle::class.java)
            add(TextButton("Level1", buttonStyle).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        screenManager.showScreen(ScreenEnum.GAME)
                    }
                })
            }).center().width(100F).height(100F).pad(32F)

            // Buttons
            row()
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
        stage.dispose()
    }
}