package ru.trueengineering.tefeaturetoggles.data.storage.impl

import android.content.Context
import com.google.gson.Gson
import ru.trueengineering.tefeaturetoggles.makeEdit
import ru.trueengineering.tefeaturetoggles.domain.FeatureTogglesStorage
import ru.trueengineering.tefeaturetoggles.data.storage.model.SdkFlag

internal class SharedPreferencesStorage(
    private val gson: Gson,
    context: Context,
    preferencesName: String = DEFAULT_PREFERENCES_NAME
) : FeatureTogglesStorage() {

    private val sharedPreferences =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    override fun saveFlags(flags: List<SdkFlag>) {
        flags.forEach { flag ->
            val flagJson = gson.toJson(flag)
            sharedPreferences.makeEdit {
                putString(flag.name, flagJson)
            }
        }
    }

    override fun saveHash(hash: String) {
        sharedPreferences.makeEdit {
            putString(KEY_FLAGS_HASH, hash)
        }
    }

    override fun getHash(): String? =
        sharedPreferences.getString(KEY_FLAGS_HASH, null)

    override fun getByName(name: String): SdkFlag? {
        val flagJson = sharedPreferences.getString(name, null) ?: return null
        return gson.fromJson(flagJson, SdkFlag::class.java)
    }

    override fun getFlags(): List<SdkFlag> =
        sharedPreferences.all
            .asSequence()
            .mapNotNull { it.value as? SdkFlag }
            .toList()

    override fun clear() {
        sharedPreferences.makeEdit {
            clear()
        }
    }

    companion object {

        private const val DEFAULT_PREFERENCES_NAME = "FTSDK:Storage:SharedPreferences"
        private const val KEY_FLAGS_HASH = "FTSDK:Storage:FlagsHash"
    }
}