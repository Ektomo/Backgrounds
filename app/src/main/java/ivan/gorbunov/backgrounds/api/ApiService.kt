package ivan.gorbunov.backgrounds.api


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ivan.gorbunov.backgrounds.pojo.Backgrounds3D
import ivan.gorbunov.backgrounds.pojo.Backgrounds4K
import ivan.gorbunov.backgrounds.pojo.LiveBackgrounds
import ivan.gorbunov.backgrounds.pojo.MainBackgrounds
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

const val baseUrl = "http://157.230.90.200/"

interface ApiService {

    @GET("grizzly/categories-3d.json")
    suspend fun get3DBackgrounds() : List<Backgrounds3D>

    @GET("grizzly/grizzly-main.json")
    suspend fun getMainBackgrounds() : List<MainBackgrounds>

    @GET("grizzly/categories-live.json")
    suspend fun getLiveBackgrounds() : List<LiveBackgrounds>

    @GET("grizzly/categories-4k.json.json")
    suspend fun get4kBackgrounds() : List<Backgrounds4K>


    companion object {
        var apiService: ApiService? = null
        fun getInstance() : ApiService {
            val contentType = "application/json".toMediaType()
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(Json.asConverterFactory(contentType))
                    .build().create(ApiService::class.java)
            }
            return apiService!!
        }
    }

}