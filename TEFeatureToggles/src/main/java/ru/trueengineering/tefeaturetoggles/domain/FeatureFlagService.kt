package ru.trueengineering.tefeaturetoggles.domain

import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlagsWithHash

internal interface FeatureFlagService {

    suspend fun loadFeatureToggles(): Result<SdkFlagsWithHash>
}
