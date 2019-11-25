package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
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
                }
            }

            // Reset the collision entity
            entity.collision.collisionEntity = null
        }
    }
}