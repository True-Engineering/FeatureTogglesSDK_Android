package ru.trueengineering.tefeaturetoggles.domain

import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

internal interface FeatureTogglesRepository {

    suspend fun checkHash(hash: String?)

    fun getByName(name: String): SdkFlag?

    fun getFlags(): List<SdkFlag>

    suspend fun loadFeaturesFromRemote(): Result<List<SdkFlag>>
}
