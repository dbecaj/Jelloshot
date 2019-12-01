package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.MovingPlatformComponent
import me.dbecaj.jelloshot.physics
import kotlin.math.absoluteValue

@Singleton
class TranslationSystem @Inject constructor() :
        IteratingSystem(Family.all(MovingPlatformComponent::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.physics.body
        val moving = entity.getComponent(MovingPlatformComponent::class.java)
        val towards = Vector2(moving.endPos).sub(moving.startPos)
        body.linearVelocity = towards.scl(moving.speed)

        if (body.position.epsilonEquals(moving.endPos, 0.1F) && moving.speed > 0F) {
            moving.speed = moving.speed * -1F
        }
        if (body.position.epsilonEquals(moving.startPos, 0.1F) && moving.speed < 0F) {
            moving.speed = moving.speed.absoluteValue
        }
    }
}