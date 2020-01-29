package me.dbecaj.jelloshot.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.google.inject.Inject
import com.google.inject.Singleton
import java.io.File
import java.io.FileFilter
import com.badlogic.gdx.utils.Array as GdxArray


@Singleton
class GameAssetManager @Inject constructor() {

    companion object {
        val levelList = Gdx.files.internal("core/assets/levels").list(FileFilter { file -> file.extension == "tmx" })
                .map { it.file() }.sortedBy { it.nameWithoutExtension }
        private lateinit var loadedLevel: TiledMap
        var loadedLevelPath = "" // This is used to check if we already have the level loaded in loadLevel()

        private const val atlasFile = "atlas.png"

        private const val skinFile = "skins/uiskin.json"
        private const val coinPickupFile = "sounds/coin_pickup.mp3"
        private const val jumpFile = "sounds/jump.mp3"
        private const val deathFile = "sounds/death.mp3"
        private const val cupFile = "sounds/cup.mp3"
        private const val powerUpFile = "sounds/powerup.mp3"
    }

    private val assetManager: AssetManager = AssetManager()

    init {
        assetManager.load(atlasFile, Texture::class.java)
        assetManager.load(skinFile, Skin::class.java)
        assetManager.load(coinPickupFile, Sound::class.java)
        assetManager.load(jumpFile, Sound::class.java)
        assetManager.load(deathFile, Sound::class.java)
        assetManager.load(cupFile, Sound::class.java)
        assetManager.load(powerUpFile, Sound::class.java)

        assetManager.finishLoading()
    }

    fun groundSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(0, 0, 32, 32)
        }
    }

    fun playerAnimBlinking(): GdxArray<TextureRegion> {
        var array: GdxArray<TextureRegion> = GdxArray<TextureRegion>()
        array.add(
            TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
                setRegion(0, 32, 32, 32) // Default keyframe
            },
            TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
                setRegion(0, 32 * 2, 32, 32) // Blink keyframe
            }
        )

        return array
    }

    fun playerAgitatedSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(32, 32 * 2, 32, 32) // Agitated eyes
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

    fun cupSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(2 * 32, 2* 32, 32, 32)
        }
    }

    fun greenCanSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(3 * 32, 2 * 32, 32, 32)
        }
    }

    fun redCanSprite(): TextureRegion {
        return TextureRegion(assetManager.get(atlasFile, Texture::class.java)).apply {
            setRegion(4 * 32, 2 * 32, 32, 32)
        }
    }

    fun loadLevel(file: File) {
        // Check if the level is already loaded
        if (loadedLevelPath == file.path) return

        loadedLevel = TmxMapLoader().load(file.path)
        loadedLevelPath = file.path
    }

    fun level(): TiledMap {
        return loadedLevel
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

    fun cupSound(): Sound {
        return assetManager.get(cupFile, Sound::class.java)
    }

    fun powerUpSound(): Sound {
        return assetManager.get(powerUpFile, Sound::class.java)
    }


    fun dispose() {
        assetManager.dispose()
    }
}