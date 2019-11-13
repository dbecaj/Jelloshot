package me.dbecaj.jelloshot.world

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.pixelsToMeters

class Platform @Inject constructor(private val position: Vector2,
                                   private val world: World) : Entity() {

    private var texture: Texture = Texture("platform_green_resized.png")

    init {
        add(TextureRegionComponent(TextureRegion(texture)))
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

}