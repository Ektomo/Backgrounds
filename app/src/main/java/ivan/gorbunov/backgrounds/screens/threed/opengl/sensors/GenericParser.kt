package ivan.gorbunov.backgrounds.screens.threed.opengl.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager

abstract class GenericParser(context: Context) {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    init {
        reset()
    }

    abstract fun getSensors(): Array<Sensor>

    // Parse a sensor event and returns rotation
    abstract fun parse(event: SensorEvent): DoubleArray

    // resets any internal data
    abstract fun reset()

    open fun fixOrientation(input: FloatArray, fixed: FloatArray) {
        when (display.rotation) {
            Surface.ROTATION_0 -> {
                fixed[0] = input[0]
                fixed[1] = input[1]
            }
            Surface.ROTATION_90 -> {
                fixed[0] = -input[1]
                fixed[1] = input[0]
            }
            Surface.ROTATION_180 -> {
                fixed[0] = -input[0]
                fixed[1] = -input[1]
            }
            Surface.ROTATION_270 -> {
                fixed[0] = input[1]
                fixed[1] = -input[0]
            }
        }
        fixed[2] = input[2]
        if (input.size > 3) {
            fixed[3] = input[3]
        }
    }

    fun getSensorManager(): SensorManager {
        return sensorManager
    }
}