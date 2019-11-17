package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.PhysicsComponent
import me.dbecaj.jelloshot.TransformComponent
import me.dbecaj.jelloshot.transform

@Singleton
class LevelBuilder @Inject constructor(
        private val world: World,
        private val engine: Engine
) {

    fun initialize() {
        createGround()
    }

    fun createGround() {
        val entity = Entity().apply {
            add(TransformComponent(Vector2(0F, 0F)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(500F, 2F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)

            add(PhysicsComponent(body))
        }

        engine.addEntity(entity)
    }
}