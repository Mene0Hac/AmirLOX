package com.example.postopoche

import android.content.Context

class Settings(private val context: Context) {

    var seeKalories: Boolean = true
    var seeReting: Boolean = true
    var BigLook: Boolean = false
    var seeId: Boolean = false

    var username: String = ""
    var token: String = ""
    var temp: String = ""

    private val prefs = context.getSharedPreferences("my_settings", Context.MODE_PRIVATE)

    fun save() {
        prefs.edit().apply {
            putBoolean("seeKalories", seeKalories)
            putBoolean("seeReting", seeReting)
            putBoolean("BigLook", BigLook)
            putBoolean("seeId", seeId)

            putString("username", username)
            putString("token", token)

            putString("temp",temp)

            apply()
        }
    }

    fun load() {
        seeKalories = prefs.getBoolean("seeKalories", true)
        seeReting = prefs.getBoolean("seeReting", true)
        BigLook = prefs.getBoolean("BigLook", false)
        seeId = prefs.getBoolean("seeId", false)

        username = prefs.getString("username", "") ?: ""
        token = prefs.getString("token", "") ?: ""

        temp = prefs.getString("temp", "") ?: ""
    }
}
