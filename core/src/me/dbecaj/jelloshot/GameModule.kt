package me.dbecaj.jelloshot

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.system.*

class GameModule : AbstractModule() {

    override fun configure() {
        binder().apply {
            requireAtInjectOnConstructors()
            requireExactBindingAnnotations()
        }

        // We could put some listeners for physics world here
        bind(World::class.java).toInstance(World(Vector2(0F, -9.81F * 2F), true).apply {
            // Setup contact listener
            this.setContactListener(object : ContactListener {
                override fun beginContact(contact: Contact) {
                    val fa = contact.fixtureA
                    val fb = contact.fixtureB

                    if (fa.body.userData is Entity && fb.body.userData is Entity) {
                        handleCollision(fa.body.userData as Entity, fb.body.userData as Entity)
                    }

                    /*val userDataA = contact.fixtureA.userData as? String
                    val userDataB = contact.fixtureB.userData as? String
                    if (userDataA == "outerCircle" && userDataB != "outerCircle") {
                        putOnGround(contact.fixtureA, true)
                    } else if (userDataB == "outerCircle" && userDataA != "outerCircle") {
                        putOnGround(contact.fixtureB, true)
                    }*/
                }

                override fun endContact(contact: Contact) {
                    /*val userDataA = contact.fixtureA.userData as? String
                    val userDataB = contact.fixtureB.userData as? String
                    if (userDataA == "outerCircle" && userDataB != "outerCircle") {
                        putOnGround(contact.fixtureA, false)
                    } else if (userDataB == "outerCircle" && userDataA != "outerCircle") {
                        putOnGround(contact.fixtureB, false)
                    }*/
                }

                private fun handleCollision(entity1: Entity, entity2: Entity) {
                    // Assign collision entity if entity has the collision component
                    if (entity1.getComponent(CollisionComponent::class.java) != null) {
                        entity1.collision.collisionEntity = entity2
                    }
                    if (entity2.getComponent(CollisionComponent::class.java) != null) {
                        entity2.collision.collisionEntity = entity1
                    }
                }

                private fun putOnGround(fixture: Fixture, isOnGround: Boolean) {
                    val entity = fixture.body.userData as Entity
                    if (isOnGround) {
                        entity.getComponent(PlayerComponent::class.java).groundCollision++
                    }
                    else {
                        entity.getComponent(PlayerComponent::class.java).groundCollision--
                    }
                }

                override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}

                override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
            })
        })
        bind(SpriteBatch::class.java).toInstance(SpriteBatch())
    }

    @Provides @Singleton
    fun engine(injector: Injector, world: World): Engine {
        val engine = Engine()

        // We add our systems here in order we want them to process entities in
        sequenceOf(
                PlayerControllerSystem::class.java,
                TranslationSystem::class.java,
                PhysicsSystem::class.java,
                PhysicsSynchronizationSystem::class.java,
                RenderingSystem::class.java,
                PhysicsDebugSystem::class.java,
                CollisionSystem::class.java
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