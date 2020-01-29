package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.*
import java.util.*

@Singleton
class PlayerControllerSystem @Inject constructor(
        private val camera: OrthographicCamera,
        private val assetManager: GameAssetManager,
        private val gamePreferences: GamePreferences,
        private val injector: Injector
) : IteratingSystem(Family.all(PlayerComponent::class.java).get()) {

    private var startDragPos: Vector2 = Vector2()
    private var endDragPos: Vector2 = Vector2()
    private var move: Boolean = false
    private var isDragging = false

    var stateTime = 0F
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerComponent = entity.getComponent(PlayerComponent::class.java)
        if (move) { //&& playerComponent.isOnGround) {
            assetManager.jumpSound().play(gamePreferences.soundVolume)
            val launchPower = injector.getInstance(GameManager::class.java).launchPower
            val movementVector = startDragPos.sub(endDragPos).scl(launchPower)

            val vectorLenLimit = launchPower * 22 // 22 is a magic number I tested in game
            // Limit the vector length
            if (movementVector.len() > vectorLenLimit) {
                movementVector.scl(vectorLenLimit / movementVector.len())
            }

            entity.physics.body.linearVelocity = movementVector
        }

        move = false

        // Camera follows player
        val playerPos = entity.physics.body.position
        val levelBuilder = injector.getInstance(LevelBuilder::class.java)

        camera.position.set(Vector3(playerPos.x, playerPos.y, 0F))
        // Clip the camera to the start of the render space so we don't show the void outside
        if (camera.position.x < 21.5F) camera.position.x = 21.5F
        if (camera.position.x > levelBuilder.levelWidth-23.5F) camera.position.x = levelBuilder.levelWidth - 23.5F
        if (camera.position.y < 7F) camera.position.y  = 7F
        camera.update()

        // Check for the power up timeout
        val gameManager = injector.getInstance(GameManager::class.java)
        if (gameManager.launchPower != 2F && gameManager.launchPowerChangeDate.before(Date())) {
            gameManager.launchPower = 2F
        }

        // Process player sprite animations
        entity.tryGet(PlayerAnimationComponent)?.let { playerAnimationComponent ->
            entity.tryGet(PlayerComponent)?.let { playerComponent ->
                stateTime += deltaTime
                val texture = playerAnimationComponent.blinkingAnim.getKeyFrame(stateTime)

                val textureRegion = entity.getComponent(TextureRegionComponent::class.java)
                if (isDragging) {
                    textureRegion.textureRegion = TextureRegion(playerAnimationComponent.agitatedEyes)
                }
                else {
                    textureRegion.textureRegion = TextureRegion(texture)
                }
            }
        }

    }

    public val playerInputAdapter = object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            startDragPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F)).toVector2
            isDragging = true

            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            endDragPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F)).toVector2
            move = true
            isDragging = false

            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            val worldPos = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0F))

            return true
        }
    }
}