package me.dbecaj.jelloshot

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body

fun <T : Component?> Entity.hasComponent(componentClass: Class<T>?): Boolean {
    return this.getComponent(componentClass) != null
}

class TextureComponent(val texture: Texture) : Component {
    companion object : ComponentResolver<TextureComponent>(TextureComponent::class.java)
}
val Entity.texture: TextureComponent
    get() = TextureComponent[this]

class TextureRegionComponent(val textureRegion: TextureRegion) : Component {
    companion object : ComponentResolver<TextureRegionComponent>(TextureRegionComponent::class.java)
}
val Entity.textureRegion: TextureRegionComponent
    get() = TextureRegionComponent[this]

class TransformComponent(val position: Vector2, var angleRadian: Float, var scale: Float): Component {
    constructor(position: Vector2) : this(position, 0F, 1F)

    companion object : ComponentResolver<TransformComponent>(TransformComponent::class.java)
}
val Entity.transform: TransformComponent
    get() = TransformComponent[this]

class PhysicsComponent(val body: Body) : Component {
    companion object : ComponentResolver<PhysicsComponent>(PhysicsComponent::class.java)
}
val Entity.physics: PhysicsComponent
    get() = PhysicsComponent[this]

class PlayerComponent(): Component {
    var groundCollision = 0
        set(value) {
            field = value
            if (field < 0) field = 0
        }

    var isOnGround = false
        get() = groundCollision > 0

    companion object : ComponentResolver<PlayerComponent>(PlayerComponent::class.java)
}

enum class EntityType {
    PLAYER,
    COIN,
    GREEN_PLATFORM,
    RED_PLATFORM,
    GROUND
}

class EntityTypeComponent(val entityType: EntityType) : Component {
    companion object : ComponentResolver<EntityTypeComponent>(EntityTypeComponent::class.java)
}

val Entity.entityType: EntityTypeComponent
    get() = EntityTypeComponent[this]

class CollisionComponent(var collisionEntity: Entity?): Component {
    companion object : ComponentResolver<CollisionComponent>(CollisionComponent::class.java)
}

val Entity.collision: CollisionComponent
    get() = CollisionComponent[this]

class JellyComponent(val bodies: MutableList<Body>): Component {
    companion object : ComponentResolver<JellyComponent>(JellyComponent::class.java)
}

class MovingPlatformComponent(val startPos: Vector2, val endPos: Vector2, var speed: Float): Component {
    var forward = true
    companion object : ComponentResolver<MovingPlatformComponent>(MovingPlatformComponent::class.java)
}

open class ComponentResolver<T: Component>(componentClass: Class<T>) {
    val MAPPER = ComponentMapper.getFor(componentClass)
    operator fun get(entity: Entity) = MAPPER.get(entity)
}

fun <T: Component> Entity.tryGet(componentResolver: ComponentResolver<T>): T? {
    return componentResolver.MAPPER.get(this);
}