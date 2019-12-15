package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.google.inject.Inject
import com.google.inject.Singleton


@Singleton
class GameAssetManager @Inject constructor() {

    companion object {
        private const val atlasFile = "atlas.png"
        private const val level1 = "levels/sandbox.tmx"

        private const val skinFile = "skins/uiskin.json"
        private const val coinPickupFile = "sounds/coin_pickup.mp3"
        private const val jumpFile = "sounds/jump.mp3"
        private const val deathFile = "sounds/death.mp3"
    }

    private val assetManager: AssetManager = AssetManager()

    init {
        assetManager.load(atlasFile, Texture::class.java)
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        assetManager.load(level1, TiledMap::class.java)
        assetManager.load(skinFile, Skin::class.java)
        assetManager.load(coinPickupFile, Sound::class.java)
        assetManager.load(jumpFile, Sound::class.java)
        assetManager.load(deathFile, Sound::class.java)

        assetManager.finishLoading()
    }

    fun groundSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(0, 0, 32, 32)
        }
    }

    fun playerSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(0, 32, 32, 32)
        }
    }

    fun greenPlatformSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(32, 0, 32 * 3, 32)
        }
    }

    fun redPlatformSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(32, 32, 32 * 3, 32)
        }
    }

    fun coinSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(4 * 32, 0, 32, 32)
        }
    }

    fun dirtSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(4 * 32, 32, 32, 32)
        }
    }

    fun level(): TiledMap {
        return assetManager.get(level1, TiledMap::class.java)
    }

    fun uiSkin(): Skin {
        return assetManager.get(skinFile, Skin::class.java)
    }

    fun coinPickupSound(): Sound {
        return assetManager.get(coinPickupFile, Sound::class.java)
    }

    fun deathSound(): Sound {
        return assetManager.get(deathFile, Sound::class.java)
    }

    fun jumpSound(): Sound {
        return assetManager.get(jumpFile, Sound::class.java)
    }

    fun dispose() {
        assetManager.dispose()
    }
}