package ivan.gorbunov.backgrounds.pojo

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class LiveCategory (
    var link: String,
    var nameCategory: String,
    var type: String,
    var urlPhoto: String
)

@Serializable
data class Category3d (
    var link: String,
    var nameCategory: String,
    var type: String,
    var urlPhoto: String
)

@Serializable
data class Category4k (
    var link: String,
    var nameCategory: String,
    var type: String,
    var urlPhoto: String
)

@Serializable
data class CategoriesAll (
    var link: String,
    var nameCategory: String,
    var type: String,
    var urlPhoto: String
)

@Serializable
data class MainBackgrounds (
    var live_category: LiveCategory,
    @JsonNames("3d_category")
    var category3d: Category3d,
    @JsonNames("4k_category")
    var category4k: Category4k,
    var categories_all: List<CategoriesAll>
)