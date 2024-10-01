package ru.trueengineering.tefeaturetoggles.domain

internal class HashChecker(
    private val repository: FeatureTogglesRepository,
    private val headerKey: String,
) {

    suspend fun obtainHash(headers: Map<String, List<String>>) {
        val hash = headers[headerKey]?.firstOrNull()
        repository.checkHash(hash)
    }
}
