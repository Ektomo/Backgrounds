package ivan.gorbunov.backgrounds.pojo

import kotlinx.serialization.Serializable

@Serializable
data class LivePreview (
    var preview_url: String,
    var url: String
)

@Serializable
data class LiveBackgrounds (
    var link: String,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<LivePreview>
)

@Serializable
data class LiveBackgroundPreview (
    val id: Int,
    val nameCategory: String,
    val urlPhoto: String,
    val array: List<LivePreview>,
)
