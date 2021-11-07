package ivan.gorbunov.backgrounds.pojo

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Preview3D (
    var layers: List<String>,
    var preview_url: String
)

@Serializable
data class Backgrounds3D (
    var link: String,
    var nameCategory: String,
    var array: List<Preview3D>
)


@Serializable
data class Preview3DList (
    val id: Int,
    var nameCategory: String,
    var array: List<Preview3D>,
)

data class Layer(var file: File, var z: Int)