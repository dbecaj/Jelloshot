package me.dbecaj.jelloshot

import com.badlogic.ashley.core.*
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.*
import me.dbecaj.jelloshot.core.GameWorld
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.entity.Coin
import me.dbecaj.jelloshot.entity.Player
import me.dbecaj.jelloshot.system.PhysicsDebugSystem
import me.dbecaj.jelloshot.system.PhysicsSynchronizationSystem
import me.dbecaj.jelloshot.system.PhysicsSystem
import me.dbecaj.jelloshot.system.RenderingSystem
import me.dbecaj.jelloshot.world.Platform

class Game : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    internal val engine = Engine()
    private lateinit var injector: Injector
    private lateinit var gameWorld: GameWorld

    companion object {
        internal lateinit var img: Texture
        internal lateinit var player: Entity
    }

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(Systems::class.java).list.map { injector.getInstance(it) }.forEach { system ->
            engine.addSystem(system)
        }
        gameWorld = GameWorld(injector.getInstance(World::class.java))
        Gdx.input.inputProcessor = injector.getInstance(MyInputAdapter::class.java)

        createEntities()
    }

    private fun createEntities() {
        val world = injector.getInstance(World::class.java)

        /*engine.addEntity(Entity().apply {
            add(TextureComponent(img))
            add(TransformComponent(Vector2(5F, 5F)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToMeters / 2F, img.height.pixelsToMeters / 2F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
        })*/

        player = Player(Vector2(5F, 5F), world)

        engine.addEntity(player)
        engine.addEntity(Coin(Vector2(10F, 10F), world))
        engine.addEntity(Platform(Vector2(5F, 3F), world))

    }

    override fun render() {
        Gdx.gl.glClearColor(0.53f, 0.81f, 235f, 0.92f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}

class GameModule(private val game: Game): Module {

    override fun configure(binder: Binder) {
        binder.requireAtInjectOnConstructors()
        binder.requireExactBindingAnnotations()
        binder.bind(SpriteBatch::class.java).toInstance(game.batch);
    }

    @Provides @Singleton
    fun engine() : Engine {
        return game.engine
    }

    @Provides @Singleton
    fun systems() : Systems{
        return Systems(listOf(
                PhysicsSystem::class.java,
                PhysicsSynchronizationSystem::class.java,
                RenderingSystem::class.java,
                PhysicsDebugSystem::class.java
        ))
    }

    @Provides @Singleton
    fun camera() : OrthographicCamera {
        val viewportWidth = Gdx.graphics.width.pixelsToMeters
        val viewportHeight = Gdx.graphics.height.pixelsToMeters
        return OrthographicCamera(viewportWidth, viewportHeight).apply {
            position.set(viewportWidth / 2F, viewportHeight / 2F, 0F)
            update()
        }
    }

    @Provides @Singleton
    fun world() : World {
        Box2D.init()
        return World(Vector2(0F, -9.81F), true)
    }
}

data class Systems(val list: List<Class<out EntitySystem>>)

class MyInputAdapter @Inject constructor(private val camera: OrthographicCamera,
                                         private val engine: Engine,
                                         private val world: World) : InputAdapter() {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        println("$screenX, $screenY")
        val worldPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))
        /*engine.addEntity(Entity().apply {
            add(TextureRegionComponent(TextureRegion(Game.img)))

            add(TransformComponent(Vector2(worldPos.x, worldPos.y), 0F, 0.25F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
        })*/

        Game.player.physics.body.setTransform(worldPos.x, worldPos.y, 0F)

        return true;
    }
}
