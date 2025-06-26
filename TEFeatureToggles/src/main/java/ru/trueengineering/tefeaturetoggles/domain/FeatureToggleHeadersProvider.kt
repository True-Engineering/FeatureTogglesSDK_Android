package ru.trueengineering.tefeaturetoggles.domain

interface FeatureToggleHeadersProvider {

    fun getHeaders(): List<Pair<String, String>>
}