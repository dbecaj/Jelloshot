package me.dbecaj.jelloshot

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.system.PhysicsDebugSystem
import me.dbecaj.jelloshot.system.PhysicsSynchronizationSystem
import me.dbecaj.jelloshot.system.PhysicsSystem
import me.dbecaj.jelloshot.system.RenderingSystem

class GameModule : AbstractModule() {

    override fun configure() {
        binder().apply {
            requireAtInjectOnConstructors()
            requireExactBindingAnnotations()
        }

        // We could put some listeners for physics world here
        bind(World::class.java).toInstance(World(Vector2(0F, -9.81F), true))
        bind(SpriteBatch::class.java).toInstance(SpriteBatch())
    }

    @Provides @Singleton
    fun engine(injector: Injector, world: World): Engine {
        val engine = Engine()

        // We add our systems here in order we want them to process entities in
        sequenceOf(
                PhysicsSystem::class.java,
                //PhysicsSynchronizationSystem::class.java,
                RenderingSystem::class.java,
                PhysicsDebugSystem::class.java
        ).map { injector.getInstance(it) }
        .forEach { engine.addSystem(it) }

        // Add listener for entity removal to remove their body from box2d world
        engine.addEntityListener(Family.one(PhysicsComponent::class.java).get(), object : EntityListener {
            override fun entityAdded(entity: Entity?) { }

            override fun entityRemoved(entity: Entity) {
                world.destroyBody(entity.physics.body)
            }
        })

        return engine;
    }

    @Provides @Singleton
    fun camera(): OrthographicCamera {
        val viewportWidth = Gdx.graphics.width.pixelsToMeters
        val viewportHeight = Gdx.graphics.height.pixelsToMeters;

        return OrthographicCamera().apply {
            setToOrtho(false, viewportWidth, viewportHeight)
            update()
        }
    }
}