package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import me.dbecaj.jelloshot.PhysicsComponent
import me.dbecaj.jelloshot.TransformComponent
import me.dbecaj.jelloshot.physics
import me.dbecaj.jelloshot.transform

class PhysicsSystem @Inject constructor(private val world: World): EntitySystem() {
    private var accumulator = 0f

    // Simulate Box2D physics
    override fun update(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25F)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            accumulator -= TIME_STEP
        }
    }

    companion object {
        // Magic numbers
        private const val TIME_STEP = 1.0f / 300f
        private const val VELOCITY_ITERATIONS = 6
        private const val POSITION_ITERATIONS = 2
    }
}

// Used for syncing texture position with physics body position
class PhysicsSynchronizationSystem @Inject constructor():
        IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.physics.body
        entity.transform.position.set(body.position)
        entity.transform.angleRadian = body.angle
    }
}

class PhysicsDebugSystem @Inject constructor(private val world: World,
                                             private val camera: OrthographicCamera) : EntitySystem() {
    private val renderer = Box2DDebugRenderer()

    override fun update(deltaTime: Float) {
        renderer.isDrawBodies = false
        renderer.isDrawInactiveBodies = false
        renderer.isDrawAABBs = false
        renderer.render(world, camera.combined)
    }
}
