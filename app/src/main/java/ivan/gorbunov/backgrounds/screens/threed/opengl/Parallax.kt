package ivan.gorbunov.backgrounds.screens.threed.opengl

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import ivan.gorbunov.backgrounds.screens.threed.opengl.dsp.LowPassFilter
import ivan.gorbunov.backgrounds.screens.threed.opengl.sensors.AccelerationParser
import ivan.gorbunov.backgrounds.screens.threed.opengl.sensors.GenericParser
import ivan.gorbunov.backgrounds.screens.threed.opengl.sensors.GravityParser
import ivan.gorbunov.backgrounds.screens.threed.opengl.sensors.RotationParser


class Parallax(val context: Context): SensorEventListener {
    private val TAG = javaClass.simpleName

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val pars = getParser()
    var filtersInit = false

    private val sensitivityFilter: LowPassFilter = LowPassFilter(2)
    private val fallbackFilter: LowPassFilter = LowPassFilter(2)
    private var resetDeg = DoubleArray(2)

    private var degX = 0.0
    private var degY = 0.0

    fun getDegX(): Double {
        return degX
    }

    fun getDegY(): Double {
        return degY
    }

    fun setFallback(fallback: Double) {
        fallbackFilter.setFactor(fallback)
    }

    fun setSensitivity(sensitivity: Double) {
        sensitivityFilter.setFactor(sensitivity)
    }

    fun start() {
        if (pars != null) {
            for (sensor in pars.getSensors()) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
        }
        Log.d(TAG, "Sensor listener started!")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Sensor listener stopped!")
    }


    override fun onSensorChanged(event: SensorEvent) {
        var newDeg: DoubleArray = pars!!.parse(event)

        // Set the initial value of the filters to current val

        // Set the initial value of the filters to current val
        if (!filtersInit) {
            sensitivityFilter.setLast(newDeg)
            fallbackFilter.setLast(newDeg)
            filtersInit = true
        }

        // Apply filter

        // Apply filter
        newDeg = sensitivityFilter.filter(newDeg)

        degY = newDeg[0] - resetDeg[0]
        degX = newDeg[1] - resetDeg[1]

        resetDeg = fallbackFilter.filter(newDeg)

        if (degX > 180) {
            resetDeg[1] += degX - 180
            degX = 180.0
        }

        if (degX < -180) {
            resetDeg[1] += degX + 180
            degX = -180.0
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun getParser(): GenericParser? {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            Log.d(TAG, "Using rotation vector")
            return RotationParser(context)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            Log.d(TAG, "Using gravity")
            return GravityParser(context)
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD
            ) != null
        ) {
            Log.d(TAG, "Using accelerometer+magnetometer")
            return AccelerationParser(context)
        }
        return null
    }
}