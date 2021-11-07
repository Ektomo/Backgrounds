package ivan.gorbunov.backgrounds.screens.threed.opengl.dsp

class RollingAverageFilter(private val width: Int, private val size: Int) {
    private var samples = Array(size) { FloatArray(width) }
    private var index = 0
    private val total = mutableListOf<Float>()

//    val a = Array(size) { FloatArray(width) }

    init {
        for (i in 0 until size) {
            for (j in 0 until width) {
                samples[i][j] = 0f
            }
        }
    }

    fun add(x: FloatArray){
        for (j in 0 until width) total[j] -= samples[index][j]
        samples[index] = x.clone()

        for (j in 0 until width) total[j] += x[j]

        if (++index == size) index = 0
    }

    fun getAverage(): FloatArray {
        val output = FloatArray(width)
        for (j in 0 until width) output[j] = total[j] / size
        return output
    }
}