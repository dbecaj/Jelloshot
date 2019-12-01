package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.PhysicsComponent
import me.dbecaj.jelloshot.TransformComponent
import me.dbecaj.jelloshot.transform

@Singleton
class LevelBuilder @Inject constructor(
        private val world: World,
        private val engine: Engine,
        private val gameWorld: GameWorld,
        private val assetManager: GameAssetManager
) {

    fun initialize() {
        val levelLayer = assetManager.level().layers[0] as TiledMapTileLayer

        for (y in 0 until levelLayer.height) {
            for (x in 0 until levelLayer.width) {
                val cell = levelLayer.getCell(x, y)
                if (cell != null) {
                    // We multiply the coordinates by 2 to offset the central coordinate system
                    val position = Vector2(x.toFloat() * 2F, y.toFloat() * 2F)
                    when (cell.tile.id) {
                        1 -> {
                            gameWorld.createGround(position)
                            position.y = position.y - 2
                            for (i in 0 until 5) {
                                gameWorld.createDirt(position)
                                position.y = position.y - 2
                            }
                        }
                        2 -> {
                            gameWorld.createGreenMovingPlatform(position, Vector2(position).add(0F, 10F), 0.5F)
                            //gameWorld.createGreenPlatform(position)
                        }
                        5 -> gameWorld.createCoin(Vector2(position))
                        6 -> gameWorld.createPlayer(position)
                        7 -> gameWorld.createRedPlatform(position)
                    }
                }
            }
            println()
        }
    }
}