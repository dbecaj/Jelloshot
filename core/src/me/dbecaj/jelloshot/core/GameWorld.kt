package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import me.dbecaj.jelloshot.PhysicsComponent
import me.dbecaj.jelloshot.TransformComponent
import me.dbecaj.jelloshot.transform

class GameWorld @Inject constructor(private val world: World) {

    private fun createBox(pos: Vector2): Entity {
        return Entity().apply {
            add(TransformComponent(Vector2(5F, 5F)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })
            body.createFixture(PolygonShape().apply {
                setAsBox(20F, 20F)
            }, 1.0F)
            body.setTransform(transform.position, 0F)
            add(PhysicsComponent(body))
        }
    }
}