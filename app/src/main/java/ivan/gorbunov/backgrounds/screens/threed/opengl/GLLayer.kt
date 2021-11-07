package ivan.gorbunov.backgrounds.screens.threed.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GLLayer {



    private val vertices = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)

    private val textureVertices = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)

    private var verticesBuffer: FloatBuffer? = null
    private var textureBuffer: FloatBuffer? = null

    private var vertexShader = 0
    private var fragmentShader = 0

    private var program = 0



    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aPosition;" +
            "attribute vec2 aTexPosition;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "  vTexPosition = aTexPosition;" +
            "}"

    private val fragmentShaderCode = "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
            "}"

    init {
        initializeBuffers()
        initializeProgram()
    }

    private fun initializeBuffers() {
        var buff = ByteBuffer.allocateDirect(vertices.size * 4)
        buff.order(ByteOrder.nativeOrder())
        verticesBuffer = buff.asFloatBuffer()
        verticesBuffer!!.put(vertices)
        verticesBuffer!!.position(0)
        buff = ByteBuffer.allocateDirect(textureVertices.size * 4)
        buff.order(ByteOrder.nativeOrder())
        textureBuffer = buff.asFloatBuffer()
        textureBuffer!!.put(textureVertices)
        textureBuffer!!.position(0)
    }

    private fun initializeProgram() {
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexShaderCode)
        GLES20.glCompileShader(vertexShader)
        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
        GLES20.glCompileShader(fragmentShader)
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(texture: Int, mvpMatrix: FloatArray?) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glUseProgram(program)
        GLES20.glEnable(GLES20.GL_BLEND)

        // Note: android uses premultiplied alpha for bitmaps!
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        val positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        val texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexPosition")
        val mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glVertexAttribPointer(
            texturePositionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(texturePositionHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glUniform1i(textureHandle, 0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisable(GLES20.GL_BLEND)
    }

}