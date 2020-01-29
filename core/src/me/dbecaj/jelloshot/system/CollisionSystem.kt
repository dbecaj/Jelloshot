package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.GameAssetManager
import me.dbecaj.jelloshot.core.GameManager
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.GameState
import java.util.*

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
        if (cc != null && gameManager.state == GameState.RUNNING) {
            // Check if entity has type component
            if (cc.getComponent(EntityTypeComponent::class.java) != null) {
                // Process the collision
                when (cc.entityType.entityType) {
                    EntityType.COIN -> {
                        gameManager.totalScore += 10
                        gameManager.currentLevelScore += 10
                        engine.removeEntity(cc)
                        assetManager.coinPickupSound().play(gamePreferences.soundVolume)
                    }
                    EntityType.RED_PLATFORM -> {
                        if (gameManager.state != GameState.LOSE) {
                            assetManager.deathSound().play(gamePreferences.soundVolume)
                            gameManager.lose()
                        }
                    }
                    EntityType.GREEN_CAN -> {
                        assetManager.powerUpSound().play(gamePreferences.soundVolume)
                        gameManager.launchPower = 3F
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.SECOND, 30)
                        gameManager.launchPowerChangeDate = calendar.time
                        engine.removeEntity(cc)
                    }
                    EntityType.RED_CAN -> {
                        assetManager.powerUpSound().play(gamePreferences.soundVolume)
                        gameManager.launchPower = 4F
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.SECOND, 30)
                        gameManager.launchPowerChangeDate = calendar.time
                        engine.removeEntity(cc)
                    }
                    EntityType.CUP -> {
                        assetManager.cupSound().play(gamePreferences.soundVolume)
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