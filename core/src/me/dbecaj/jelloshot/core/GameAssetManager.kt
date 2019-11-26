package me.dbecaj.jelloshot.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class GameAssetManager @Inject constructor() {

    companion object {
        private const val atlasFile = "atlas.png"
    }

    private val assetManager: AssetManager = AssetManager()

    init {
        assetManager.load(atlasFile, Texture::class.java)

        assetManager.finishLoading()
    }

    fun playerSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(0, 32, 32, 32)
        }
    }

    fun platformSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(32, 0, 32 * 3, 32)
        }
    }

    fun coinSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(4 * 32, 0, 32, 32)
        }
    }

    fun dispose() {
        assetManager.dispose()
    }
}