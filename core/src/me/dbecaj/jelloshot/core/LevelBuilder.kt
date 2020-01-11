package me.dbecaj.jelloshot.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Polyline
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class LevelBuilder @Inject constructor(
        private val engine: Engine,
        private val gameWorld: GameWorld,
        private val assetManager: GameAssetManager
) {

    fun Polyline.getPoints(): List<Vector2> {
        val points = arrayListOf<Vector2>()

        for (i in 0 until this.transformedVertices.size/2) {
            points.add(Vector2(this.transformedVertices[i*2].pixelsToMeters * 1.5F, this.transformedVertices[i*2+1].pixelsToMeters * 2))
        }

        return points
    }

    fun initialize() {
        val tileLayer = assetManager.level().layers.get("Level") as TiledMapTileLayer

        for (y in 0 until tileLayer.height) {
            for (x in 0 until tileLayer.width) {
                val cell = tileLayer.getCell(x, y)
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
                        2 -> gameWorld.createGreenPlatform(position)
                        5 -> gameWorld.createCoin(Vector2(position))
                        6 -> gameWorld.createPlayer(position)
                        7 -> gameWorld.createRedPlatform(position)
                    }
                }
            }
        }

        val objectLayer = assetManager.level().layers.get("Level_Objects")

        objectLayer.objects.forEach {
            when (it.properties.get("type")) {
                "MOVING_PLATFORM_GREEN" -> {
                    val polyline = (it as PolylineMapObject).polyline
                    val points = polyline.getPoints()

                    gameWorld.createGreenMovingPlatform(points[0], points[1], 0.5F)
                }
                "MOVING_PLATFORM_RED" -> {
                    val polyline = (it as PolylineMapObject).polyline
                    val points = polyline.getPoints()

                    gameWorld.createRedMovingPlatform(points[0], points[1], 0.5F)
                }
            }
        }
    }

    fun reinitialize() {
        clear()
        initialize()
    }

    fun clear() {
        engine.removeAllEntities()
    }
}