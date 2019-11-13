package me.dbecaj.jelloshot.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import me.dbecaj.jelloshot.*

class Coin @Inject constructor(private val position: Vector2,
                               private val world: World) : Entity() {

    private var texture: Texture = Texture("coin_resized.png")

    init {
        add(TextureRegionComponent(TextureRegion(texture)))
        add(TransformComponent(position, 0F, 0.35F))

        val body = world.createBody(BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
        })
        body.createFixture(PolygonShape().apply {
            setAsBox(0.7F, 0.7F)
        }, 1.0F)
        body.setTransform(transform.position, 0F)
        add(PhysicsComponent(body))

        add(PickupComponent())
    }

}