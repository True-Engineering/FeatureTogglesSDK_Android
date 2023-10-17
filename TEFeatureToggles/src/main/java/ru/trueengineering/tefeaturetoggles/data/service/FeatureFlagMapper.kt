package ru.trueengineering.tefeaturetoggles.data.service

import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

internal object FeatureFlagMapper {

    fun map(flag: Pair<String, FeatureFlag>): SdkFlag {
        return SdkFlag(
            name = flag.first,
            isEnabled = flag.second.enable,
        )
    }
}