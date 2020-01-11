package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.GameAssetManager
import me.dbecaj.jelloshot.core.GameManager
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.GameState

@Singleton
class CollisionSystem @Inject() constructor(
        private val gameManager: GameManager,
        private val assetManager: GameAssetManager,
        private val gamePreferences: GamePreferences
) : IteratingSystem(Family.all(
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
                        gameManager.score += 10
                        engine.removeEntity(cc)
                        assetManager.coinPickupSound().play(gamePreferences.soundVolume)
                    }
                    EntityType.RED_PLATFORM -> {
                        if (gameManager.state != GameState.LOSE) {
                            assetManager.deathSound().play(gamePreferences.soundVolume)
                            gameManager.lose()
                        }
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
                    EntityType.CUP -> {
                        engine.removeEntity(cc)
                        gameManager.win()
                    }
                }
            }

            // Reset the collision entity
            entity.collision.collisionEntity = null
        }
    }
}