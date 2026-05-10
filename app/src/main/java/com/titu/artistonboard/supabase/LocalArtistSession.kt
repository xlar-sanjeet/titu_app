package com.titu.artistonboard.supabase

import android.content.Context
import java.nio.charset.StandardCharsets
import java.util.UUID

object LocalArtistSession {
    private const val PREFS_NAME = "artist_session"
    private const val KEY_EMAIL = "email"

    private var appContext: Context? = null
    private var activeEmail: String? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        activeEmail = appContext
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(KEY_EMAIL, null)
    }

    fun signInWithEmail(email: String): String {
        val normalizedEmail = email.trim().lowercase()
        activeEmail = normalizedEmail
        appContext
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_EMAIL, normalizedEmail)
            ?.apply()
        return userIdForEmail(normalizedEmail)
    }

    fun currentUserId(): String? = activeEmail?.let(::userIdForEmail)

    fun clear() {
        activeEmail = null
        appContext
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.remove(KEY_EMAIL)
            ?.apply()
    }

    private fun userIdForEmail(email: String): String {
        val demoUserId = when (email) {
            "picasso@titu.com" -> "11111111-1111-1111-1111-111111111111"
            "vangogh@titu.com" -> "22222222-2222-2222-2222-222222222222"
            else -> null
        }
        if (demoUserId != null) return demoUserId

        return UUID.nameUUIDFromBytes(
            "artist:$email".toByteArray(StandardCharsets.UTF_8)
        ).toString()
    }
}
