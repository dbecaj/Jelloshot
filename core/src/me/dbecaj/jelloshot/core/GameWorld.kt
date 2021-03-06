package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*
import java.lang.Math.cos
import java.lang.Math.sin

@Singleton
class GameWorld @Inject constructor(
        private val engine: Engine,
        private val world: World,
        private val assetManager: GameAssetManager
) {

    fun createPlayer(position: Vector2): Entity {
        val bodies = mutableListOf<Body>()
        val entity = Entity().apply {
            add(PlayerAnimationComponent(assetManager.playerAnimBlinking(), assetManager.playerAgitatedSprite()))
            add(TextureRegionComponent(assetManager.playerAgitatedSprite()))
            add(TransformComponent(position, 0F, 3F))
            add(PlayerComponent())

            val NUM_SEGMENTS = 30
            val RADIUS = 80F

            val center = Vector2(position)
            //println("Center: ${center.x}, ${center.y}")
            val fixtureDef = FixtureDef().apply {
                shape = CircleShape().apply { radius = 0.3F }
                density = 0.1F
                restitution = 1F
                friction = 1F
            }

            // Create outer circles
            val deltaAngle = (2F * Math.PI) / NUM_SEGMENTS
            for (i in 0 until NUM_SEGMENTS) {
                // Calculate angle
                val theta = deltaAngle * i

                // Calculate position
                val x = RADIUS * cos(theta)
                val y = RADIUS * sin(theta)
                // Convert to box2d coords
                val circlePosition = Vector2(x.toFloat().pixelsToMeters, y.toFloat().pixelsToMeters)

                // Create body and fixture
                val body = world.createBody(BodyDef().apply {
                    type = BodyDef.BodyType.DynamicBody
                    this.position.set(circlePosition.add(center))
                    //println("Outer circle ${i}: ${this.position.x}, ${this.position.y}")
                })
                body.createFixture(fixtureDef)

                body.userData = this
                bodies.add(body)
            }

            // Create circle at the center
            val innerCircleBody = world.createBody(BodyDef().apply {
                this.position.set(center)
                type = BodyDef.BodyType.DynamicBody
            })
            innerCircleBody.createFixture(fixtureDef.apply {
                shape = CircleShape().apply {
                    radius = 0.8F
                    density = 0.5F
                }
            })
            innerCircleBody.userData = this

            // Connect the joints
            for (i in 0 until NUM_SEGMENTS) {
                val neighborIndex = (i + 1) % NUM_SEGMENTS

                // Get current body and neighbor body
                val currentBody = bodies[i]
                val neighborBody = bodies[neighborIndex]

                // Connect the outer circles to each other
                world.createJoint(DistanceJointDef().apply {
                    this.initialize(currentBody, neighborBody, currentBody.worldCenter, neighborBody.worldCenter)
                    frequencyHz = 0F
                    dampingRatio = 0F
                    collideConnected = true
                })

                // Connect the center circle with other circles
                world.createJoint(DistanceJointDef().apply {
                    this.initialize(currentBody, innerCircleBody, currentBody.worldCenter, center)
                    frequencyHz = 3.0F
                    dampingRatio = 0.5F
                    collideConnected = true
                })
            }

            innerCircleBody.setTransform(transform.position, 0F)
            add(PhysicsComponent(innerCircleBody))

            add(EntityTypeComponent(EntityType.PLAYER))
            add(CollisionComponent(null))
            add(JellyComponent(bodies))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createGreenPlatform(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.greenPlatformSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(4.2F, 0.7F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.GREEN_PLATFORM))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createRedPlatform(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.redPlatformSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(4.2F, 0.7F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.RED_PLATFORM))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createGreenMovingPlatform(position: Vector2, endPos: Vector2, speed: Float): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.greenPlatformSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.KinematicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(4.2F, 0.7F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.GREEN_PLATFORM))
            add(CollisionComponent(null))
            add(MovingPlatformComponent(Vector2(position), endPos, speed))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createRedMovingPlatform(position: Vector2, endPos: Vector2, speed: Float): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.redPlatformSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.KinematicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(4.2F, 0.7F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.RED_PLATFORM))
            add(CollisionComponent(null))
            add(MovingPlatformComponent(Vector2(position), endPos, speed))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createCoin(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.coinSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.COIN))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createGround(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.groundSprite()))
            add(TransformComponent(Vector2(position), 0F, 2F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 1.0F)

            body.userData = this

            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
            add(EntityTypeComponent(EntityType.GROUND))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createDirt(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.dirtSprite()))
            add(TransformComponent(Vector2(position), 0F, 2F))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createCup(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.cupSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.CUP))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createGreenCan(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.greenCanSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.GREEN_CAN))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createRedCan(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(assetManager.redCanSprite()))
            add(TransformComponent(position, 0F, 3F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(1F, 1F)
            }, 0F)
            body.setTransform(transform.position, 0F)
            body.userData = this
            add(PhysicsComponent(body))

            add(EntityTypeComponent(EntityType.RED_CAN))
            add(CollisionComponent(null))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createTriangleMesh(x: Float, y: Float, width: Float, height: Float): Entity {
        val entity = Entity().apply {
            add(TransformComponent(Vector2(x, y), 0F, 3F))

            add(MeshComponent(x, y, width, height))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createBox(position: Vector2, size: Float = 1F): Entity {
        val entity = Entity().apply {
            add(TransformComponent(Vector2(position)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(size, size)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
        }

        engine.addEntity(entity)
        return entity
    }
}