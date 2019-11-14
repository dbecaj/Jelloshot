package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*

@Singleton
class GameWorld @Inject constructor(
        private val engine: Engine,
        private val world: World,
        private val assetManager: GameAssetManager
) {

    fun createPlayer(position: Vector2): Entity {
        val entity = Entity().apply {
            add(TextureRegionComponent(TextureRegion(assetManager.playerSprite())))
            add(TransformComponent(position, 0F, 0.25F))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(CircleShape().apply {
                radius = 1F
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
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