package ivan.gorbunov.backgrounds.pojo

data class Preview3D (
    var layers: List<String>,
    var preview_url: String
)

data class Backgrounds3D (
    var link: String,
    var nameCategory: String,
    var array: List<Preview3D>
)