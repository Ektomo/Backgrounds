package ivan.gorbunov.backgrounds.pojo

data class Preview4K (
    var preview_url: String,
    var url: String
)

data class Backgrounds4K (
    var link: String,
    var nameCategory: String,
    var urlPhoto: String,
    var array: List<Preview4K>
)
