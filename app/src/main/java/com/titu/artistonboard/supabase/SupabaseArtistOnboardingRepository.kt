package com.titu.artistonboard.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.jsonObject
class SupabaseArtistOnboardingRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client,
    private val authRepository: SupabaseAuthRepository = SupabaseAuthRepository(supabase)
) {
    suspend fun upsertProfile(
        role: String,
        fullName: String,
        phone: String,
        email: String
    ) {
        val userId = requireNotNull(authRepository.currentUserId()) {
            "User must be authenticated before saving profile data."
        }

        supabase.from("profiles").upsert(
            listOf(
                buildJsonObject {
                    put("id", userId)
                    put("role", role)
                    put("full_name", fullName)
                    put("phone", phone)
                    put("email", email)
                }
            )
        ) {
            onConflict = "id"
        }
    }

    suspend fun upsertArtistProfile(
        brandName: String,
        city: String,
        state: String,
        liveSelfiePath: String? = null,
        identityVerified: Boolean = false
    ) {
        val userId = requireNotNull(authRepository.currentUserId()) {
            "User must be authenticated before saving artist profile data."
        }
        val existingProfile = fetchArtistProfile()
        val existingStatus = existingProfile
            ?.get("onboarding_status")
            ?.toString()
            ?.removeSurrounding("\"")

        supabase.from("artist_profiles").upsert(
            listOf(
                buildJsonObject {
                    put("user_id", userId)
                    put("brand_name", brandName)
                    put("city", city)
                    put("state", state)
                    put("identity_verified", identityVerified)
                    put("onboarding_status", existingStatus ?: "draft")
                    liveSelfiePath?.let { put("live_selfie_url", it) }
                }
            )
        ) {
            onConflict = "user_id"
        }
    }

    suspend fun updateArtistProfilePartial(updates: JsonObject) {
        if (authRepository.isDemoArtistSession()) return

        val userId = requireNotNull(authRepository.currentUserId()) {
            "User must be authenticated before updating artist profile data."
        }
        supabase.from("artist_profiles").update(updates) {
            filter { eq("user_id", userId) }
        }
    }

    suspend fun fetchArtistProfile(): JsonObject? {
        val userId = authRepository.currentUserId() ?: return null
        return try {
            val response = supabase.from("artist_profiles").select(Columns.ALL) {
                filter { eq("user_id", userId) }
            }.data
            
            // In Jan's Supabase SDK, data is a string containing a JSON array if not deserialized to a class.
            // Actually `decodeSingleOrNull` is better if we use standard data classes.
            // Let's just return raw string or JsonObject by deserializing.
            val jsonArrayStr = response
            val array = kotlinx.serialization.json.Json.parseToJsonElement(jsonArrayStr).jsonArray
            if (array.isEmpty()) null else array[0].jsonObject
        } catch (e: Exception) {
            null
        }
    }

    suspend fun upsertLiveArtServices(serviceData: JsonObject) {
        if (authRepository.isDemoArtistSession()) return

        val userId = requireNotNull(authRepository.currentUserId()) {
            "User must be authenticated before saving live art services."
        }
        // Force the user_id into the serviceData payload
        val payload = buildJsonObject {
            serviceData.forEach { (key, value) -> put(key, value) }
            put("artist_id", userId)
        }
        supabase.from("artist_live_services").upsert(listOf(payload)) {
            onConflict = "artist_id"
        }
    }
}
