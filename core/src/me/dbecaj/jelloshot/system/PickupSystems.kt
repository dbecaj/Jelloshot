package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import me.dbecaj.jelloshot.PhysicsComponent
import me.dbecaj.jelloshot.PickupComponent
import me.dbecaj.jelloshot.physics

class PickupSystem @Inject constructor(
        private val world: World
): IteratingSystem(Family.all(
        PhysicsComponent::class.java,
        PickupComponent::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.physics.body
        val fixture = body.fixtureList[0]

        world.QueryAABB({ fixture ->
            val e = fixture.body.userData as? Entity
            true
        }, body.position.x, body.position.y,
                body.position.x + fixture.shape.radius,
                body.position.y + fixture.shape.radius)
    }
}