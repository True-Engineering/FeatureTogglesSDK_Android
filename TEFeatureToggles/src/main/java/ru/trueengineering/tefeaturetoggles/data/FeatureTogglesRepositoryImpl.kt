package ru.trueengineering.tefeaturetoggles.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag
import ru.trueengineering.tefeaturetoggles.domain.FeatureFlagService
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesRepository
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesStorage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class FeatureTogglesRepositoryImpl(
    private val storage: FeatureTogglesStorage,
    private val service: FeatureFlagService
) : FeatureTogglesRepository {

    private val lock = ReentrantReadWriteLock()
    private val checkHashMutex = Mutex()

    override suspend fun checkHash(hash: String?) {
        checkHashMutex.withLock {
            if (storage.getHash() != hash) {
                loadFeaturesFromRemote()
            }
        }
    }

    override fun getByName(name: String): SdkFlag? = lock.read {
        storage.getByName(name)
    }

    override fun getFlags(): List<SdkFlag> = lock.read { storage.getFlags() }

    override suspend fun loadFeaturesFromRemote() {
        service.loadFeatureToggles()?.let {
            lock.write {
                storage.saveHash(it.hash)
                storage.saveFlags(it.flags)
            }
        }
    }
}
