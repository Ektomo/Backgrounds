package ivan.gorbunov.backgrounds.screens.threed.opengl.dsp

import java.lang.RuntimeException

class LowPassFilter(private val width: Int) {
    private var last = DoubleArray(width)
    private var factor: Double? = null

    fun setFactor(factor: Double){
        this.factor = factor
    }

    fun setLast(array: DoubleArray){
        this.last = array.clone()
    }

    fun filter(input: DoubleArray): DoubleArray{
        if(factor != null){
            val output = DoubleArray(width)

             input.forEachIndexed {i ,el ->
                output[i] = factor!! * el + (1 - factor!!) * last[i]!!
            }
            last = output.clone()
            return output
        }else{
            throw RuntimeException("Пустой фактор")
        }
    }

}