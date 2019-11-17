package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef
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
        val entity = Entity().apply {
            //add(TextureRegionComponent(TextureRegion(assetManager.playerSprite())))
            add(TransformComponent(position, 0F, 0.25F))
            add(PlayerComponent())

            val NUM_SEGMENTS = 40
            val RADIUS = 80F

            val center = Vector2(position)
            val fixtureDef = FixtureDef().apply {
                shape = CircleShape().apply { radius = 0.1F }
                density = 0.1F
                restitution = 0.05F
                friction = 1F
            }

            val deltaAngle = (2F * Math.PI) / NUM_SEGMENTS
            val bodies = mutableListOf<Body>()
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
                })
                body.createFixture(fixtureDef)

                bodies.add(body)
            }

            // Create circle at the center
            val innerCircleBody = world.createBody(BodyDef().apply {
                this.position.set(center)
                type = BodyDef.BodyType.DynamicBody
            })
            innerCircleBody.createFixture(fixtureDef.apply {
                shape = CircleShape().apply { radius = 0.8F }
            })

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
                    frequencyHz = 2.0F
                    dampingRatio = 0.5F
                    collideConnected = true
                })
            }

            /*val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(CircleShape().apply {
                radius = 1F
            }, 1.0F)*/

            innerCircleBody.setTransform(transform.position, 0F)
            add(PhysicsComponent(innerCircleBody))
        }

        engine.addEntity(entity)
        return entity
    }

    fun createPlatform(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(TextureRegion(assetManager.platformSprite())))
            add(TransformComponent(position, 0F, 0.25F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(4F, 0.9F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
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