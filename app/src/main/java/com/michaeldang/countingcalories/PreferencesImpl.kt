package com.michaeldang.countingcalories

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import java.util.*

object PreferencesImpl {
    val USER_ID = "user_id_that_should_be_server_generated"
    private val prefFileName = "countingCaloriesSharedPrefs"

    fun getUserId(context: Context): String {
        val prefs = context.getSharedPreferences(prefFileName, MODE_PRIVATE)
        val id = prefs.getString(USER_ID, null)
        if (id == null) {
            val newId = UUID.randomUUID().toString()
            saveUserId(context, newId)
            return newId
        }
        return id
    }

    fun saveUserId(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(prefFileName, MODE_PRIVATE)
        prefs.edit().putString(USER_ID, userId).apply()
    }
}