package com.saveetha.cineselect.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object AuthTokenManager {
    private const val PREFS_NAME = "CineSelectPrefs"
    private const val TOKEN_KEY = "user_token"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
        Log.d("AuthTokenManager", "Token saved: $token")
    }

    fun getToken(context: Context): String? {
        val token = getPreferences(context).getString(TOKEN_KEY, null)
        Log.d("AuthTokenManager", "Token retrieved: $token")
        return token
    }

    fun clearToken(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
        Log.d("AuthTokenManager", "Token cleared")
    }
}