package ru.trueengineering.tefeaturetoggles.data.storage.model

import java.io.Serializable

data class SdkFlag(
    val name: String,
    val isEnabled: Boolean,
    //may be other info
): Serializable