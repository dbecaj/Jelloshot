package me.dbecaj.jelloshot.core.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.DisposalClass
import me.dbecaj.jelloshot.core.GameManager
import me.dbecaj.jelloshot.core.LevelBuilder

@Singleton
class GameScreen @Inject() constructor(
        private val injector: Injector
) : Screen {

    private lateinit var engine: Engine

    override fun show() {
        engine = injector.getInstance(Engine::class.java)

        val levelBuilder = injector.getInstance(LevelBuilder::class.java)
        levelBuilder.initialize()
        val gameManager = injector.getInstance(GameManager::class.java)
        gameManager.init()
    }

    override fun render(delta: Float) {
        //Gdx.gl.glClearColor(0.53f, 0.81f, 235f, 0.92f)
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        engine.update(delta)

    }

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {}

    override fun hide() {}

    override fun dispose() {
        engine.removeAllEntities()
    }
}