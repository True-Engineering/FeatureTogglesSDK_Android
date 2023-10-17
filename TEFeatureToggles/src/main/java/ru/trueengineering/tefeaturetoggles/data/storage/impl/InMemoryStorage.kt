package ru.trueengineering.tefeaturetoggles.data.storage.impl

import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesStorage
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

internal class InMemoryStorage: FeatureTogglesStorage() {

    private val flagsStorage: MutableMap<String, SdkFlag> = mutableMapOf()

    private var flagsHash: String? = null

    override fun saveFlags(flags: List<SdkFlag>) {
        flags.forEach { flag ->
            flagsStorage[flag.name] = flag
        }
    }

    override fun saveHash(hash: String) {
        flagsHash = hash
    }

    override fun getHash(): String? = flagsHash

    override fun getByName(name: String): SdkFlag? = flagsStorage[name]

    override fun getFlags(): List<SdkFlag> = flagsStorage.values.toList()

    override fun clear() {
        flagsHash = null
        flagsStorage.clear()
    }
}