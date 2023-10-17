package ru.trueengineering.tefeaturetoggles.domain

import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

internal interface FeatureTogglesRepository {

    fun checkHash(hash: String?)

    fun getByName(name: String): SdkFlag?

    fun getFlags(): List<SdkFlag>

    suspend fun loadFeaturesFromRemote()
}