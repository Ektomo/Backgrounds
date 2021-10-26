package ivan.gorbunov.backgrounds.pojo

data class LivePreview (
    var preview_url: String,
    var url: String
)

data class LiveBackgrounds (
    var link: String,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<LivePreview>
)
