package ivan.gorbunov.backgrounds.pojo

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable

@Serializable
data class Preview4K (
    var preview_url: String,
    var url: String
)

@Serializable
data class Backgrounds4K (
    var link: String,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<Preview4K>
)

@Serializable
data class Preview4KBackGrounds(
    var id: Int,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<Preview4K>
)
