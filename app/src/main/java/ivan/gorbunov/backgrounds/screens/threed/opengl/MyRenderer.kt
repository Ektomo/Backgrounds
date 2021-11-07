package ivan.gorbunov.backgrounds.screens.threed.opengl

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
import ivan.gorbunov.backgrounds.R
import ivan.gorbunov.backgrounds.pojo.Layer
import java.io.File
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs

const val PREF_BACKGROUND_DEFAULT = "fallback"

class MyRenderer(val context: Context, var layerList: List<Layer>) : GLSurfaceView.Renderer {

    private val TAG = "Renderer"

    private var prefWallpaperId: String = ""

    // Screen
    private var orientation = 0
    private var deltaXMax = 0f
    private var deltaYMax = 0f

    // Values
    private var deltaInit = false
    private var deltaArrayNew: Array<FloatArray> = arrayOf()
    private var deltaArrayOld: Array<FloatArray> = arrayOf()

//    private ? = null

    // Preferences
    private var prefSensor = true
    private var prefScroll = true
    private var prefLimit = true
    private var prefDepth = 0.08
    private var prefScrollAmount = 0.35
    private var prefZoom = 0.9f
    private var prefDim = 0

    // External
    private var offset = 0.0

    // Internal
    private var loadedWallpaperId: String? = null
    private var hasOverlay = false
    private var isFallback = false


    private var glLayer: GLLayer? = null
    private val MVPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private var textures: IntArray = intArrayOf()


    var parallax: Parallax = Parallax(context)
//    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val isPreview = false

    init {
        start()
    }

    fun start() {
        reloadSettings()
        deltaInit = false

        // Get current screen orientation
        orientation = context.resources.configuration.orientation
        parallax.start()
    }

    fun stop() { parallax.stop()}

    private fun reloadSettings() {
        // If preview render use provided id, else load it from settings
//        if (!isPreview) {
//            prefWallpaperId = sharedPreferences.getString(PREF_BACKGROUND, PREF_BACKGROUND_DEFAULT) ?: PREF_BACKGROUND_DEFAULT
//        }
//        prefSensor = sharedPreferences.getBoolean(
//            context.getString(R.string.pref_sensor_key),
//            context.resources.getBoolean(R.bool.pref_sensor_default)
//        )
//        prefLimit = sharedPreferences.getBoolean(
//            context.getString(R.string.pref_limit_key),
//            context.resources.getBoolean(R.bool.pref_limit_default)
//        )
//        val depthString = sharedPreferences.getString(
//            context.getString(R.string.pref_depth_key),
//            context.getString(R.string.pref_depth_default)
//        )
//        prefDepth = DEPTH_MIN + depthString!!.toDouble() * (DEPTH_MAX / 100.0)
//        val sensitivityString = sharedPreferences.getString(
//            context.getString(R.string.pref_sensitivity_key),
//            context.getString(R.string.pref_sensitivity_default)
//        )
        val sensitivity: Double =
            0.1 + 0.5 * (0.2 / 100.0)
//        val fallbackString = sharedPreferences.getString(
//            context.getString(R.string.pref_fallback_key),
//            context.getString(R.string.pref_fallback_default)
//        )
        val fallback: Double =  0.5 * (0.05 / 100.0)
//        val zoomString = sharedPreferences.getString(
//            context.getString(R.string.pref_zoom_key),
//            context.getString(R.string.pref_zoom_default)
//        )
//        prefZoom = (ZOOM_MIN + (100 - zoomString!!.toDouble()) * ((ZOOM_MAX - ZOOM_MIN) / 100.0))
//        prefScroll = sharedPreferences.getBoolean(
//            context.getString(R.string.pref_scroll_key),
//            context.resources.getBoolean(R.bool.pref_scroll_default)
//        )
//        val scrollAmountString = sharedPreferences.getString(
//            context.getString(R.string.pref_scroll_amount_key),
//            context.getString(R.string.pref_scroll_amount_default)
//        )
//        prefScrollAmount =
//            SCROLL_AMOUNT_MIN + scrollAmountString!!.toDouble() * (SCROLL_AMOUNT_MAX / 100.0)
//        val dimString = sharedPreferences.getString(
//            context.getString(R.string.pref_dim_key),
//            context.getString(R.string.pref_dim_default)
//        )
//        prefDim = (dimString!!.toDouble() * (DIM_MAX / 100.0)) as Int

        // Set parallax settings
        parallax.setFallback(fallback)
        parallax.setSensitivity(sensitivity)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onDrawFrame(gl: GL10) {
        // Redraw background color
        // Set the camera position (View matrix)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        // Calculate the projection and view transformation
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Redraw background color

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Initialize arrays

        // Initialize arrays
        if (!deltaInit) {
            deltaArrayNew = Array(textures.size) { FloatArray(2) }
            deltaArrayOld = Array(textures.size) { FloatArray(2) }
            deltaInit = true
        }

        // Compute deltas

        // Compute deltas
        for (i in 0 until textures.size - 1) {
            // Get layer z
            val z: Double = if (!isFallback) {
                layerList[i].z.toDouble()
            } else {
                0.0
            }

            // Compute the launcher page offset
            val scrollOffset: Double = if (prefScroll && z != 0.0) {
                offset / (prefScrollAmount * z)
            } else {
                0.0
            }

            // Compute the x-y offset
            val deltaX = (-(scrollOffset + parallax.getDegX() / 180.0 * (prefDepth * z))).toFloat()
            val deltaY = (parallax.getDegY() / 180.0 * (prefDepth * z)).toFloat()

            // Limit max offset
            if ((abs(deltaX) > deltaXMax || abs(deltaY) > deltaYMax) && prefLimit) {
                deltaArrayNew = deltaArrayOld.clone()
                break
            }
            deltaArrayOld = deltaArrayNew.clone()
            deltaArrayNew[i][0] = deltaX
            deltaArrayNew[i][1] = deltaY
        }

        val layerCount: Int = if (hasOverlay) {
            textures.size - 1
        } else {
            textures.size
        }

        // Draw layers

        // Draw layers
        for (i in 0 until layerCount) {
            val layerMatrix = MVPMatrix.clone()
            Matrix.translateM(
                layerMatrix, 0,
                deltaArrayNew[i][0], deltaArrayNew[i][1], 0f
            )
            glLayer!!.draw(textures[i], layerMatrix)
        }

        // Overlay

        // Overlay
        if (hasOverlay) {
            // Has an overlay
            val layerMatrix = MVPMatrix.clone()
            glLayer!!.draw(textures[textures.size - 1], layerMatrix)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Refit wallpaper to match screen orientation

        // Refit wallpaper to match screen orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val ratio = width.toFloat() / height
            deltaXMax = 0.5f * ratio / prefZoom
            deltaYMax = 1 - prefZoom
            Matrix.frustumM(
                projectionMatrix,
                0,
                -ratio * prefZoom,
                ratio * prefZoom,
                -prefZoom,
                prefZoom,
                3f,
                7f
            )
        } else {
            val ratio = height.toFloat() / width
            deltaXMax = 1 - prefZoom
            deltaYMax = 0.5f * ratio / prefZoom
            Matrix.frustumM(
                projectionMatrix,
                0,
                -prefZoom,
                prefZoom,
                -ratio * prefZoom,
                ratio * prefZoom,
                3f,
                7f
            )
        }

        // Create layers only if wallpaper has changed

        // Create layers only if wallpaper has changed
        if (prefWallpaperId != loadedWallpaperId) {
            generateLayers()
        }
    }

    private fun generateLayers() {
        // Clean old textures (if any) before loading the new ones
        clearTextures()

        // Assume that the layer is fallback
        val layerCount: Int
        isFallback = false
        hasOverlay = true
        if (prefWallpaperId != PREF_BACKGROUND_DEFAULT) {
            // If the wallpaper is not the fallback one
//            layerList = BackgroundHelper.loadFromFile(prefWallpaperId, context)
            if (layerList.isNotEmpty()) {
                // Layer loaded correctly
                prefWallpaperId = PREF_BACKGROUND_DEFAULT
                isFallback = false
                layerCount = layerList!!.size
            } else {
                deployFallbackWallpaper()
                return
            }
        } else {
            deployFallbackWallpaper()
            return
        }

        // Useful info
        var width = 0
        var height = 0

        // Create glTexture array
        textures = IntArray(layerCount + 1)
        GLES20.glGenTextures(layerCount + 1, textures, 0) // Layer + Overlay
        var tempBitmap: Bitmap
        for (i in textures.indices) {
            if (i < textures.size - 1) {
                // Load bitmap
                val bitmapFile: File = layerList!![i].file
                tempBitmap = decodeScaledFromFile(bitmapFile)
                tempBitmap.width
                width = tempBitmap.width
                height = tempBitmap.height
            } else {
                // Generate overlay
                tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                tempBitmap.eraseColor(Color.argb(prefDim, 0, 0, 0))
            }
            if (i == 0) {
                // Solid black background
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i])
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )
            try {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, tempBitmap, 0)
            } catch (e: NullPointerException) {
                Log.e(TAG, "Null pointer wile genrating layers", e)
                deployFallbackWallpaper()
                return
            }

            // Free memory
            tempBitmap.recycle()
        }
        glLayer = GLLayer()

        // Set the loaded wallpaper id
        loadedWallpaperId = prefWallpaperId
    }

    fun setOffset(offset: Float) {
        this.offset = offset.toDouble()
    }

    private fun deployFallbackWallpaper() {
        clearTextures()
        isFallback = true
        hasOverlay = false
        val fallbackBitmap =
            decodeScaledFromRes(context.resources, R.drawable.fallback)
        textures = IntArray(1)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, fallbackBitmap, 0)
        glLayer = GLLayer()
        loadedWallpaperId = PREF_BACKGROUND_DEFAULT
    }

    private fun clearTextures() {
        if (textures.isNotEmpty()) {
            GLES20.glDeleteTextures(textures.size, textures, 0)
        }
    }

    fun decodeScaledFromFile(file: File): Bitmap {
        // Get the size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.path, options)
    }

    fun decodeScaledFromRes(res: Resources?, id: Int): Bitmap {
        // Get the size
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, id, options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, id, options)
    }
}