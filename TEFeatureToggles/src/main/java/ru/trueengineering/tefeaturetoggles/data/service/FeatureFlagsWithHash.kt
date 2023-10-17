package ru.trueengineering.tefeaturetoggles.data.service

internal class FeatureFlagsWithHash(
    val featureFlags: Map<String, FeatureFlag>,
    val featureFlagsHash: String,
)

internal class FeatureFlag(
    val uid: String,
    val enable: Boolean,
    val description: String,
    val group: String,
    val permissions: List<String>,
    val customProperties: Map<String, String>,
    val flippingStrategy: FeatureFlagStrategyDetails
)

internal class FeatureFlagStrategyDetails(
    val className: String,
    val initParams: Map<String, String>,
)