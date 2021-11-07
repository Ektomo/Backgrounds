package ivan.gorbunov.backgrounds.api


import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ivan.gorbunov.backgrounds.pojo.*
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

const val baseUrl = "http://157.230.90.200"

interface ApiService {

    @GET("/grizzly/categories-3d.json")
    suspend fun get3DBackgrounds(): List<Backgrounds3D>

    @ExperimentalSerializationApi
    @GET("/grizzly/grizzly-main.json")
    suspend fun getMainBackgrounds(): MainBackgrounds

    @GET("/grizzly/categories-live.json")
    suspend fun getLiveBackgrounds(): List<LiveBackgrounds>

    @GET("/grizzly/categories-4k.json")
    suspend fun get4kBackgrounds(): List<Backgrounds4K>

    @GET("/grizzly/top.json")
    suspend fun getTopBackgrounds(): TopBackgrounds

    @GET
    suspend fun get4Preview(@Url url: String): Preview4KBackGrounds

    @GET
    suspend fun getLivePreview(@Url url: String): LiveBackgroundPreview

    @GET
    suspend fun get3DPreview(@Url url: String): Preview3DList

    @Streaming
    @GET
    suspend fun loadFile(@Url url: String): Response<ResponseBody>


    @ExperimentalSerializationApi
    companion object {
        var apiService: ApiService? = null
        fun getInstance(): ApiService {
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

class FileApi {
    private val okHttpClient = OkHttpClient()

     fun hop(
        url: String,
        folderName: String,
        context: Context,
        fileName: String,
        ext: String
    ): File? {
        var file: File? = checkIfExist(context, folderName, fileName, ext)

        if (file != null){
            return file
        }

        val request = Request.Builder()
            .url("$baseUrl$url")
            .build()

        val r = okHttpClient.newCall(request).execute()

        if (r.isSuccessful){
            r.use { response ->
                file = saveToRoot(context, response.body, folderName, fileName, ext)
            }
        }
        return file
    }

    private fun saveToRoot(
        context: Context,
        response: ResponseBody?,
        name: String,
        fileName: String,
        ext: String
    ): File? {
        if (response == null) {
            return null
        }
        var filePath = context.filesDir.toString()
        val fileFolder = File("$filePath/3dCache/")
        if (!fileFolder.exists()) fileFolder.mkdir()
        filePath = "$filePath/3dCache/$name/"
        val fileConPath = File(filePath)
        if (!fileConPath.exists()) fileConPath.mkdir()
        var input: InputStream? = null
        try {
            input = response.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val file = File("$filePath$fileName.$ext")
            if (!file.exists()) file.createNewFile()
            val fos = FileOutputStream("$filePath$fileName.$ext")
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return File("$filePath$fileName.$ext")
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return null
    }

    fun checkIfExist(
        context: Context,
        name: String,
        fileName: String,
        ext: String
    ): File? {
        var filePath = context.filesDir.toString()
        filePath = "$filePath/3dCache/$name/$fileName.$ext"
        val file = File(filePath)
        return if (file.exists()) {
            file
        } else {
            null
        }

    }
}
