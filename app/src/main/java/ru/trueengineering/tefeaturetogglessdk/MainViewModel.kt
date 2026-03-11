package ru.trueengineering.tefeaturetogglessdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _error: MutableSharedFlow<Throwable> = MutableSharedFlow()
    val error: Flow<Throwable> = _error
        .produceIn(viewModelScope)
        .receiveAsFlow()

    fun call() {
        viewModelScope.launch {
            try {
                api.getInfo()
            } catch (t: Throwable) {
                _error.emit(t)
            }
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