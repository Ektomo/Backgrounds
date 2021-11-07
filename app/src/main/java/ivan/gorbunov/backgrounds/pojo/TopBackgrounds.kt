package ivan.gorbunov.backgrounds.pojo

import kotlinx.serialization.Serializable


@Serializable
data class TopArray (
    var preview_url: String,
    var url: String
)

@Serializable
data class TopBackgrounds (
    var id : Int,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<TopArray>,
)