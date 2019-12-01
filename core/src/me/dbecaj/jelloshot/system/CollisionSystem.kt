package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*

@Singleton
class CollisionSystem @Inject() constructor() : IteratingSystem(Family.all(
        PlayerComponent::class.java,
        CollisionComponent::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val cc = entity.collision.collisionEntity
        if (cc != null) {
            // Check if entity has type component
            if (cc.getComponent(EntityTypeComponent::class.java) != null) {
                // Process the collision
                when (cc.entityType.entityType) {
                    EntityType.COIN -> {
                        // Consume coin TODO: Make a game manager to hande the score
                        println("Coin picked up!")
                        engine.removeEntity(cc)
                    }
                    EntityType.RED_PLATFORM -> {
                        /*println("Touching red platfrom")
                        val centerPos = entity.physics.body.position
                        val jelly = entity.getComponent(JellyComponent::class.java)
                        for (body in jelly.bodies) {
                            // Move outer circles closer to the inner one
                            val newPos = Vector2(body.position)
                            newPos.sub(centerPos).scl(0.8F).add(centerPos)
                            body.setTransform(newPos, body.angle)

                            // Make the outer circles smaller
                            body.fixtureList[0].shape.radius = body.fixtureList[0].shape.radius * 0.99F;
                        }*/
                    }
                }
            }

            // Reset the collision entity
            entity.collision.collisionEntity = null
        }
    }
}