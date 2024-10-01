package ru.trueengineering.tefeaturetoggles.domain

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal class FeatureTogglesInterceptor(
    private val hashChecker: HashChecker,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        runBlocking {
            hashChecker.obtainHash(
                headers = response.headers.toMultimap()
            )
        }

        return response
    }
}
