package ru.trueengineering.tefeaturetoggles.data.service

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlagsWithHash
import ru.trueengineering.tefeaturetoggles.domain.FeatureFlagService
import java.io.IOException

internal class FeatureFlagServiceImpl(
    private val httpClient: OkHttpClient,
    private val endpoint: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val gson: Gson = Gson()
) : FeatureFlagService {

    override suspend fun loadFeatureToggles(): Result<SdkFlagsWithHash> =
        withContext(ioDispatcher) {
            return@withContext try {
                val request = Request.Builder()
                    .url(endpoint)
                    .get()
                    .build()
                val response = httpClient.newCall(request).execute()
                val body = response.body

                if (response.isSuccessful && body != null) {
                    val responseModel = gson
                        .fromJson(body.string(), FeatureFlagsWithHash::class.java)

                    Result.success(
                        value = SdkFlagsWithHash(
                            flags = responseModel.featureFlags.map { FeatureFlagMapper.map(it.toPair()) },
                            hash = responseModel.featureFlagsHash,
                        )
                    )
                } else {
                    Result.failure(IOException(response.toString()))
                }
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }
        }
}
