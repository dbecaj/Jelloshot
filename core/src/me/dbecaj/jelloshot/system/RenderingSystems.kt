package me.dbecaj.jelloshot.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.google.inject.Singleton
import me.dbecaj.jelloshot.*
import me.dbecaj.jelloshot.core.metersToPixels
import me.dbecaj.jelloshot.core.pixelsToMeters
import me.dbecaj.jelloshot.core.toDegrees


@Singleton
class RenderingSystem @Inject constructor(private val batch: SpriteBatch,
                                          private val camera: OrthographicCamera) :
        IteratingSystem(Family.all(TransformComponent::class.java).
                one(TextureComponent::class.java, TextureRegionComponent::class.java, JellyComponent::class.java,
                        PlayerAnimationComponent::class.java).get()) {

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    var stateTime = 0.0F
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
            renderTextureRegion(entity, position, textureRegionComponent.textureRegion)
        }

        entity.tryGet(PlayerAnimationComponent)?.let { playerAnimationComponent ->
            entity.tryGet(PlayerComponent)?.let { playerComponent ->
                stateTime += deltaTime
                val frameIndex = playerAnimationComponent.blinkingAnim.getKeyFrameIndex(stateTime)
                val texture = playerAnimationComponent.blinkingAnim.getKeyFrame(stateTime)

                renderTextureRegion(entity, position, TextureRegion(texture))
            }
        }
    }

    fun renderTextureRegion(entity: Entity, position: Vector2, textureRegion: TextureRegion) {
        val texture = textureRegion
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

class JellyRenderingSystem @Inject() constructor(
        private val cam: OrthographicCamera
) :
        IteratingSystem(Family.all(TransformComponent::class.java,
                                    JellyComponent::class.java,
                                    PlayerComponent::class.java).get()) {

    var vertexShader = "attribute vec2 a_position;\n" +
            "\n" +
            "uniform mat4 u_projectionViewMatrix;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =  u_projectionViewMatrix * vec4(a_position.xy, 0.0, 1.0f);\n" +
            "} "
    var fragmentShader = "void main()\n" +
            "{\n" +
            "    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}"

    var shaderProgram = ShaderProgram(vertexShader, fragmentShader)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val jellyRendererComponent = entity.getComponent(JellyComponent::class.java)
        val NUM_SEGMENTS = jellyRendererComponent.bodies.size

        val triangleFanPos = arrayOfNulls<Vector2>(NUM_SEGMENTS + 2)

        //val textCoords = arrayOfNulls<Vector2>(NUM_SEGMENTS + 2)

        triangleFanPos[0] = Vector2(entity.physics.body.position.x.metersToPixels,
                                    entity.physics.body.position.y.metersToPixels)
        for (i in 0 until NUM_SEGMENTS) {
            val currentBody = jellyRendererComponent.bodies[i]
            triangleFanPos[i + 1] = Vector2(currentBody.position.x.metersToPixels,
                                            currentBody.position.y.metersToPixels)
        }

        // Loop back to close off the triangle fan
        triangleFanPos[NUM_SEGMENTS + 1] = triangleFanPos[1]

        /*val deltaAngle = (2F * Math.PI) / NUM_SEGMENTS
        textCoords[0] = Vector2(0.5f, 0.5f);
        for (i in 0 until NUM_SEGMENTS) {
            val theta = Math.PI + (deltaAngle * i)

            textCoords[i + 1] = Vector2((0.5F + Math.cos(theta) * 0.5F).toFloat(),
                                        (0.5F + Math.sin(theta) * 0.5F).toFloat())
        }
        // Close it off.
        textCoords[NUM_SEGMENTS+1] = textCoords[1];*/

        val mesh = Mesh(true, triangleFanPos.size, 0,
                VertexAttribute(VertexAttributes.Usage.Position, triangleFanPos.size, "a_position"))

        val coordinates = triangleFanPos.map {
            arrayOf<Float>(it!!.x, it.y)
        }.flatMap {
            listOf(it[0], it[1])
        }
        println(coordinates)
        println()
        mesh.setVertices(coordinates.toFloatArray())
        /*val shortArray = (0 until NUM_SEGMENTS).map {
            it.toShort()
        }
        mesh.setIndices(shortArray.toShortArray())*/

        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (shaderProgram.isCompiled) {
            shaderProgram.begin()
            shaderProgram.setUniformMatrix("u_projectionViewMatrix", cam.combined)
            mesh.render(shaderProgram, GL20.GL_TRIANGLE_FAN)
            shaderProgram.end()
        }
        else {
            println("Something is busted yo!")
        }
    }
}

class MeshRenderingSystem @Inject() constructor(
        private val cam: OrthographicCamera
) :
        IteratingSystem(Family.all(TransformComponent::class.java, MeshComponent::class.java).get()) {

    var vertexShader = "attribute vec2 a_position;\n" +
            "\n" +
            "uniform mat4 u_projectionViewMatrix;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =  u_projectionViewMatrix * vec4(a_position.xy, 0.0, 1.0f);\n" +
            "} "
    var fragmentShader = "void main()\n" +
            "{\n" +
            "    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}"

    val POSITION_COMPONENTS = 2

    //Color attribute - (r, g, b, a)
    //val COLOR_COMPONENTS = 4

    //Total number of components for all attributes
    val NUM_COMPONENTS = POSITION_COMPONENTS //+ COLOR_COMPONENTS

    //The "size" (total number of floats) for a single triangle
    val PRIMITIVE_SIZE = 3 * NUM_COMPONENTS

    //The maximum number of triangles our mesh will hold
    val MAX_TRIS = 1

    //The maximum number of vertices our mesh will hold
    val MAX_VERTS = MAX_TRIS * 3

    var verts = FloatArray(MAX_VERTS * NUM_COMPONENTS)

    var shaderProgram = ShaderProgram(vertexShader, fragmentShader)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val meshComponent = entity.getComponent(MeshComponent::class.java)

        val mesh = Mesh(true, MAX_VERTS, 0,
                    VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"))

        verts[0] = meshComponent.x 			//Position(x, y)
        verts[1] = meshComponent.y
        //top left vertex
        verts[2] = meshComponent.x 			//Position(x, y)
        verts[3] = meshComponent.y + meshComponent.height

        //bottom right vertex
        verts[4] = meshComponent.x + meshComponent.width	 //Position(x, y)
        verts[5] = meshComponent.y

        if (shaderProgram.isCompiled) {
            mesh.setVertices(verts)

            //no need for depth...
            Gdx.gl.glDepthMask(false);

            //enable blending, for alpha
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shaderProgram.begin()
            shaderProgram.setUniformMatrix("u_projectionViewMatrix", cam.combined)
            //render the mesh
            mesh.render(shaderProgram, GL20.GL_TRIANGLES, 0, NUM_COMPONENTS);
            shaderProgram.end()

            Gdx.gl.glDepthMask(true);
        }
        else {
            println("Something is busted yo!")
        }
    }
}
