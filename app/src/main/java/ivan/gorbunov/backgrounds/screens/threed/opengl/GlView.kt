package ivan.gorbunov.backgrounds.screens.threed.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.runtime.Immutable
import ivan.gorbunov.backgrounds.pojo.Layer

@Immutable
class GlView(context: Context) : GLSurfaceView(context) {
    private var renderer: MyRenderer? = null
    var isStarted = false


    fun init(list: List<Layer>) {
        // Set version
        setEGLContextClientVersion(2)
        // Set renderer
        renderer = MyRenderer(context, list)
        setRenderer(renderer)
    }

    fun start(){
        renderer!!.start()
        isStarted = true
    }

    fun stop() {
        renderer!!.stop()
        isStarted = false
    }
}