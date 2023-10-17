package ru.trueengineering.tefeaturetoggles.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag
import ru.trueengineering.tefeaturetoggles.domain.FeatureFlagService
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesRepository
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesStorage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class FeatureTogglesRepositoryImpl(
    private val storage: FeatureTogglesStorage,
    private val service: FeatureFlagService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FeatureTogglesRepository {

    private val lock = ReentrantReadWriteLock()

    override fun checkHash(hash: String?) {
        lock.read {
            if (storage.getHash() != hash) {
                updateFeatureToggles()
            }
        }
    }

    override fun getByName(name: String): SdkFlag? = lock.read { storage.getByName(name) }

    override fun getFlags(): List<SdkFlag> = lock.read { storage.getFlags() }

    private fun updateFeatureToggles() {
        CoroutineScope(ioDispatcher).launch {
            loadFeaturesFromRemote()
        }
    }

    override suspend fun loadFeaturesFromRemote() {
        service.loadFeatureToggles()?.let {
            lock.write {
                storage.saveHash(it.hash)
                storage.clear()
                storage.saveFlags(it.flags)
            }
        }
    }
}