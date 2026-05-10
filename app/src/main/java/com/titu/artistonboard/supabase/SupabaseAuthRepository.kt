package com.titu.artistonboard.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP

class SupabaseAuthRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client
) {
    companion object {
        private const val DEMO_PASSWORD = "123456"
        private val demoArtistsByEmail = mapOf(
            "picasso@titu.com" to "11111111-1111-1111-1111-111111111111",
            "vangogh@titu.com" to "22222222-2222-2222-2222-222222222222"
        )
        private var activeDemoUserId: String? = null
    }

    suspend fun sendEmailOtp(email: String) {
        if (email.trim().lowercase() in demoArtistsByEmail) return

        supabase.auth.signInWith(OTP) {
            this.email = email
        }
    }

    suspend fun verifyEmailOtp(email: String, otp: String) {
        val normalizedEmail = email.trim().lowercase()
        val demoUserId = demoArtistsByEmail[normalizedEmail]
        if (demoUserId != null && otp == DEMO_PASSWORD) {
            activeDemoUserId = demoUserId
            return
        }

        supabase.auth.verifyEmailOtp(
            type = OtpType.Email.SIGNUP,
            email = normalizedEmail,
            token = otp
        )
    }

    suspend fun startLocalEmailSession(email: String): String {
        supabase.auth.clearSession()
        return LocalArtistSession.signInWithEmail(email)
    }

    suspend fun currentUserId(): String? {
        LocalArtistSession.currentUserId()?.let { return it }
        activeDemoUserId?.let { return it }
        supabase.auth.awaitInitialization()
        return supabase.auth.currentUserOrNull()?.id
    }

    fun isDemoArtistSession(): Boolean = activeDemoUserId != null

    suspend fun logout() {
        activeDemoUserId = null
        LocalArtistSession.clear()
        supabase.auth.clearSession()
    }
}
