package ivan.gorbunov.backgrounds.screens.threed.opengl.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import ivan.gorbunov.backgrounds.screens.threed.opengl.dsp.RollingAverageFilter


class AccelerationParser(context: Context) : RotationParser(context) {
    private var accValues: FloatArray? = null
    private var magValues: FloatArray? = null
    private var degHolder = doubleArrayOf(0.0, 0.0)
    private val accFilt: RollingAverageFilter = RollingAverageFilter(3, 5)
    private val magFilt: RollingAverageFilter = RollingAverageFilter(3, 5)
    override fun getSensors(): Array<Sensor> {
        return arrayOf(
            getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        )
    }

    override fun parse(event: SensorEvent): DoubleArray {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accValues = FloatArray(3)
            fixOrientation(event.values, accValues!!)
            accFilt.add(accValues!!)
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magValues = FloatArray(3)
            fixOrientation(event.values, magValues!!)
            magFilt.add(magValues!!)
        }
        if (magValues != null && accValues != null) {
            val rotationMatrix = FloatArray(9)
            if (SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accFilt.getAverage(),
                    magFilt.getAverage()
                )
            ) {
                degHolder = parseRotationMatrix(rotationMatrix)
            }
        }
        return degHolder
    }
}