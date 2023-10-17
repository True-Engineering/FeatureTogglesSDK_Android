package ru.trueengineering.tefeaturetoggles.domain

import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

abstract class FeatureTogglesStorage {

    abstract fun saveFlags(flags: List<SdkFlag>)

    abstract fun saveHash(hash: String)

    abstract fun getHash(): String?

    abstract fun getByName(name: String): SdkFlag?

    abstract fun getFlags(): List<SdkFlag>

    abstract fun clear()
}