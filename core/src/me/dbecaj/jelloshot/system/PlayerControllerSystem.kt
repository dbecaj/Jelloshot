package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.PlayerComponent
import me.dbecaj.jelloshot.core.GameAssetManager
import me.dbecaj.jelloshot.core.GamePreferences
import me.dbecaj.jelloshot.core.toVector2
import me.dbecaj.jelloshot.physics

@Singleton
class PlayerControllerSystem @Inject constructor(
        private val world: World,
        private val camera: OrthographicCamera,
        private val assetManager: GameAssetManager,
        private val gamePreferences: GamePreferences
) : IteratingSystem(Family.all(PlayerComponent::class.java).get()) {

    private var startDragPos: Vector2 = Vector2()
    private var endDragPos: Vector2 = Vector2()
    private var move: Boolean = false

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerComponent = entity.getComponent(PlayerComponent::class.java)
        if (move) { //&& playerComponent.isOnGround) {
            assetManager.jumpSound().play(gamePreferences.soundVolume)
            val movementVector = startDragPos.sub(endDragPos).scl(800F)
            entity.physics.body.applyForceToCenter(movementVector, true)
        }

        move = false

        // Camera follows player
        val playerPos = entity.physics.body.position

        camera.position.set(Vector3(playerPos.x, playerPos.y, 0F))
        camera.update()
    }

    public val playerInputAdapter = object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            startDragPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F)).toVector2

            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            endDragPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F)).toVector2
            move = true

            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            val worldPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))

            return true
        }
    }
}