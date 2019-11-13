package me.dbecaj.jelloshot.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.World
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.pixelsToMeters

class Player constructor(private val position: Vector2,
                         private val world: World) : Entity() {

    private var texture: Texture = Texture("ball_single.png")

    init {
        val region = TextureRegion(texture, 0, 0, 256, 256)

        add(TextureRegionComponent(region))
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

}