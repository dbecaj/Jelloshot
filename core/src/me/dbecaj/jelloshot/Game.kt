package me.dbecaj.jelloshot

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Guice
import com.google.inject.Inject
import me.dbecaj.jelloshot.core.DisposalClass
import me.dbecaj.jelloshot.core.GameWorld
import me.dbecaj.jelloshot.core.toVector2

class Game : ApplicationAdapter() {
    private lateinit var engine: Engine
    private lateinit var disposal: DisposalClass

    companion object {
        lateinit var gameWorld: GameWorld
    }

    override fun create() {
        // Initialize injector
        val injector = Guice.createInjector(GameModule())
        engine = injector.getInstance(Engine::class.java)
        disposal = injector.getInstance(DisposalClass::class.java)

        Gdx.input.inputProcessor = injector.getInstance(MyInputAdapter::class.java)

        gameWorld = injector.getInstance(GameWorld::class.java)
        gameWorld.createPlatform(Vector2(5F, 3F))
        gameWorld.createPlayer(Vector2(5F, 10F))
    }

    override fun render() {
        //Gdx.gl.glClearColor(0.53f, 0.81f, 235f, 0.92f)
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        disposal.dispose()
    }
}

class MyInputAdapter @Inject constructor(
        private val camera: OrthographicCamera
) : InputAdapter() {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))
        Game.gameWorld.createBox(worldPos.toVector2)

        return true;
    }
}
