package ru.trueengineering.tefeaturetoggles

import android.content.SharedPreferences

internal inline fun SharedPreferences.makeEdit(crossinline editAction: SharedPreferences.Editor.() -> Unit) {
    this.edit()
        .apply(editAction)
        .apply()
}