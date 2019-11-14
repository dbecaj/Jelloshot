package me.dbecaj.jelloshot.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class GameAssetManager @Inject constructor() {

    companion object {
        private const val playerFile = "ball_single.png"
        private const val platformFile = "platform_green_resized.png"
        private const val coinFile = "coin.png"
    }

    private val assetManager: AssetManager = AssetManager()

    init {
        assetManager.load(playerFile, Texture::class.java)
        assetManager.load(platformFile, Texture::class.java)
        assetManager.load(coinFile, Texture::class.java)

        assetManager.finishLoading()
    }

    fun playerSprite(): Texture {
        return assetManager.get(playerFile, Texture::class.java)
    }

    fun platformSprite(): Texture {
        return assetManager.get(platformFile, Texture::class.java)
    }

    fun coinSprite(): Texture {
        return assetManager.get(coinFile, Texture::class.java)
    }

    fun dispose() {
        assetManager.dispose()
    }
}