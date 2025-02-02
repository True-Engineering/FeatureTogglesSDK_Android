package ru.trueengineering.tefeaturetoggles

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import ru.trueengineering.tefeaturetoggles.data.FeatureTogglesRepositoryImpl
import ru.trueengineering.tefeaturetoggles.data.service.FeatureFlagServiceImpl
import ru.trueengineering.tefeaturetoggles.data.storage.impl.InMemoryStorage
import ru.trueengineering.tefeaturetoggles.data.storage.impl.SharedPreferencesStorage
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesInterceptor
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesRepository
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesStorage
import ru.trueengineering.tefeaturetoggles.domain.HashChecker

@Suppress("unused", "MemberVisibilityCanBePrivate")
class FeatureTogglesSdk private constructor(
    private val repository: FeatureTogglesRepository,
    private val headerKey: String,
) {

    private fun isEnabled(flag: String): Boolean =
        repository.getByName(flag)?.isEnabled == true

    private fun isEnabled(flags: List<String>): Boolean =
        flags.asSequence()
            .map { repository.getByName(it) }
            .all { it?.isEnabled == true }

    private fun getFlags(): List<SdkFlag> = repository.getFlags()

    private val interceptor: Interceptor
        get() = FeatureTogglesInterceptor(
            hashChecker = HashChecker(
                repository = repository,
                headerKey = headerKey,
            )
        )

    @Deprecated("")
    private val hashObtain: suspend (Map<String, List<String>>) -> Unit
        get() = HashChecker(
            repository = repository,
            headerKey = headerKey
        )::obtainHash

    private suspend fun loadRemote(): Result<List<SdkFlag>> = repository.loadFeaturesFromRemote()

    private suspend fun obtainHash(headers: Map<String, List<String>>) {
        HashChecker(
            repository = repository,
            headerKey = headerKey
        ).obtainHash(headers)
    }

    companion object {

        private var INSTANCE: FeatureTogglesSdk? = null

        fun isEnabled(flag: String): Boolean = INSTANCE!!.isEnabled(flag)

        fun isEnabled(flags: List<String>): Boolean = INSTANCE!!.isEnabled(flags)

        fun isEnabled(vararg flags: String): Boolean = INSTANCE!!.isEnabled(flags.asList())

        fun getFlags(): List<SdkFlag> =
            INSTANCE!!.getFlags()

        fun getFlagsMap(): Map<String, Boolean> = getFlags()
            .associateBy({ flag -> flag.name }) { flag -> flag.isEnabled }

        val interceptor: Interceptor
            get() = INSTANCE!!.interceptor

        @Deprecated("Replace with obtainHash() or obtainHashBlocking() method")
        val parser: suspend (Map<String, List<String>>) -> Unit
            get() = INSTANCE!!.hashObtain

        suspend fun loadRemote(): Result<List<SdkFlag>> = INSTANCE!!.loadRemote()

        fun loadRemoteBlocking(): Result<List<SdkFlag>> = runBlocking { loadRemote() }

        suspend fun obtainHash(headers: Map<String, List<String>>) = INSTANCE!!.obtainHash(headers)

        fun obtainHashBlocking(headers: Map<String, List<String>>) = runBlocking {
            obtainHash(headers)
        }
    }

    class Initializer {

        private var storage: FeatureTogglesStorage? = null
        private var headerKey: String = DEFAULT_HEADER_KEY
        private var baseUrl: String? = null
        private var apiFeaturesPath: String = DEFAULT_API_FEATURES_PATH
        private var interceptors: List<Interceptor> = emptyList()

        fun withInMemoryStorage(): Initializer = apply {
            check(this.storage == null) {
                "Attempt to override storage configuration! Existing ${this.storage?.javaClass?.name} with InMemoryStorage"
            }

            this.storage = InMemoryStorage()
        }

        fun sharedPreferencesStorage(context: Context): Initializer = apply {
            check(this.storage == null) {
                "Attempt to override storage configuration! Existing ${this.storage?.javaClass?.name} with SharedPreferencesStorage"
            }

            this.storage = SharedPreferencesStorage(
                gson = Gson(),
                context = context
            )
        }

        fun customStorage(storage: FeatureTogglesStorage): Initializer = apply {
            check(this.storage == null) {
                "Attempt to override storage configuration! Existing ${this.storage?.javaClass?.name} with ${storage.javaClass.name}"
            }

            this.storage = storage
        }

        fun headerKey(headerKey: String): Initializer = apply {
            require(headerKey.isNotBlank()) { "FF header key have to be not blank!" }

            this.headerKey = headerKey
        }

        fun baseUrl(baseUrl: String) = apply {
            require(baseUrl.isNotBlank()) { "FF base url have to be not blank!" }

            this.baseUrl = baseUrl
        }

        fun apiFeaturesPath(path: String) = apply {
            this.apiFeaturesPath = path
        }

        fun interceptors(interceptors: List<Interceptor>) = apply {
            this.interceptors = interceptors
        }

        fun initialize(): FeatureTogglesSdk =
            FeatureTogglesSdk(
                repository = FeatureTogglesRepositoryImpl(
                    storage = checkNotNull(storage),
                    service = FeatureFlagServiceImpl(
                        httpClient = getOkHttpClient(),
                        endpoint = "${checkNotNull(baseUrl)}${apiFeaturesPath}",
                    )
                ),
                headerKey = headerKey,
            ).also {
                INSTANCE = it
            }

        private fun getOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder().apply {
                this@Initializer.interceptors.forEach(this::addInterceptor)
            }.build()

        private companion object {

            const val DEFAULT_HEADER_KEY = "FF-Hash"
            const val DEFAULT_API_FEATURES_PATH = "/api/features"
        }
    }
}
