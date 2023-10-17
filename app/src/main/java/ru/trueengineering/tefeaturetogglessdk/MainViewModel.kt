package ru.trueengineering.tefeaturetogglessdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.trueengineering.tefeaturetoggles.FeatureTogglesSdk

class MainViewModel : ViewModel() {

    private val interceptor = FeatureTogglesSdk.interceptor

    private val client = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        ).build()

    private val api = client.create(TestApi::class.java)

    fun call() {
        viewModelScope.launch {
            api.getInfo()
        }
    }

    private interface TestApi {

        @GET("/test")
        suspend fun getInfo(): TestResponse
    }

    private class TestResponse(
        @SerializedName("name")
        val name: String,
        @SerializedName("surname")
        val surname: String,
    )
}