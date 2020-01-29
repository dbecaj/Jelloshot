package me.dbecaj.jelloshot

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.Singleton
import me.dbecaj.jelloshot.core.GuiCam
import me.dbecaj.jelloshot.core.HudSystem
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.system.*
import java.io.FileInputStream

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
                        val entity1 = fa.body.userData as Entity
                        val entity2 = fb.body.userData as Entity

                        handleCollision(entity1, entity2)

                        if (entity1.hasComponent(PlayerComponent::class.java) &&
                                !entity2.hasComponent(PlayerComponent::class.java)) {
                            putOnGround(entity2, true)
                        } else if (entity2.hasComponent(PlayerComponent::class.java) &&
                                !entity1.hasComponent(PlayerComponent::class.java)) {
                            putOnGround(entity2, true)
                        }
                    }
                }

                override fun endContact(contact: Contact) {
                    val fa = contact.fixtureA
                    val fb = contact.fixtureB

                    if (fa.body.userData is Entity && fb.body.userData is Entity) {
                        val entity1 = fa.body.userData as Entity
                        val entity2 = fb.body.userData as Entity

                        if (entity1.hasComponent(PlayerComponent::class.java) &&
                                !entity2.hasComponent(PlayerComponent::class.java)) {
                            putOnGround(entity2, false)
                        } else if (entity2.hasComponent(PlayerComponent::class.java) &&
                                !entity1.hasComponent(PlayerComponent::class.java)) {
                            putOnGround(entity2, false)
                        }
                    }
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

                private fun putOnGround(entity: Entity, isOnGround: Boolean) {
                    if (isOnGround) {
                        entity.getComponent(PlayerComponent::class.java).groundCollision++
                    }
                    else {
                        entity.getComponent(PlayerComponent::class.java).groundCollision--
                    }

                    //println("groundCollision: ${entity.getComponent(PlayerComponent::class.java).groundCollision}")
                }

                override fun preSolve(contact: Contact, oldManifold: Manifold?) {
                    val fa = contact.fixtureA
                    val fb = contact.fixtureB

                    // Disable collision resolution with coin
                    if (fa.body.userData is Entity && fb.body.userData is Entity) {
                        val entity1 = fa.body.userData as Entity
                        val entity2 = fb.body.userData as Entity

                        if (entity1.getComponent(EntityTypeComponent::class.java) != null &&
                                entity2.getComponent(EntityTypeComponent::class.java) != null) {
                            val entity1Type = entity1.getComponent(EntityTypeComponent::class.java).entityType
                            val entity2Type = entity2.getComponent(EntityTypeComponent::class.java).entityType

                            // Set entites to ignore
                            val nonCollidingEntities = arrayOf(
                                    EntityType.COIN,
                                    EntityType.CUP,
                                    EntityType.GREEN_CAN,
                                    EntityType.RED_CAN
                            )
                            if (nonCollidingEntities.contains(entity1Type) || nonCollidingEntities.contains(entity2Type)) {
                                contact.isEnabled = false
                            }
                        }
                    }
                }

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
                //PhysicsDebugSystem::class.java,
                CollisionSystem::class.java,
                HudSystem::class.java
                //MeshRenderingSystem::class.java
                //JellyRenderingSystem::class.java
        ).map { injector.getInstance(it) }
        .forEach { engine.addSystem(it) }

        // Add listener for entity removal to remove their body from box2d world
        engine.addEntityListener(Family.one(PhysicsComponent::class.java).get(), object : EntityListener {
            override fun entityAdded(entity: Entity?) { }

            override fun entityRemoved(entity: Entity) {
                world.destroyBody(entity.physics.body)

                // If entity is player also remove the jelly bodies
                entity.tryGet(JellyComponent)?.let { jelly ->
                    jelly.bodies.forEach {
                        world.destroyBody(it)
                    }
                }
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

    @GuiCam @Provides @Singleton
    fun guiCam(): OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false)
    }

    @Provides @Singleton
    fun firestore(): Firestore {
        // Credentials for firebase are stored in serviceAccount.json
        val inputStream = FileInputStream("serviceAccount.json")
        val credentials = GoogleCredentials.fromStream(inputStream)
        val firebaseOptions = FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build()
        FirebaseApp.initializeApp(firebaseOptions)

        return FirestoreClient.getFirestore()
    }
}