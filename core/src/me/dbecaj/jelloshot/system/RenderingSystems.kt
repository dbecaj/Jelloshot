package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Inject
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.core.toDegrees

class RenderingSystem @Inject constructor(private val batch: SpriteBatch,
                                          private val camera: OrthographicCamera) :
        IteratingSystem(Family.all(TransformComponent::class.java).
                one(TextureComponent::class.java, TextureRegionComponent::class.java).get()) {

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.transform.position

        entity.tryGet(TextureComponent)?.let { textureComponent ->
            val texture = textureComponent.texture
            batch.draw(texture,
                    position.x - texture.width.pixelsToMeters / 2F,
                    position.y - texture.height.pixelsToMeters / 2F,
                    texture.width.pixelsToMeters, texture.height.pixelsToMeters)
        }

        entity.tryGet(TextureRegionComponent)?.let { textureRegionComponent ->
            val texture = textureRegionComponent.textureRegion
            val width = texture.regionWidth.pixelsToMeters
            val height = texture.regionHeight.pixelsToMeters
            val scale = entity.transform.scale

            batch.draw(texture,
                    position.x - width/2, position.y - height/2,
                    width/2F, height/2F,
                    width, height,
                    scale, scale,
                    entity.transform.angleRadian.toDegrees)
        }
    }
}