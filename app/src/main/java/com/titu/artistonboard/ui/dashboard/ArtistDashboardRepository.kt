package com.titu.artistonboard.ui.dashboard

import com.titu.artistonboard.supabase.SupabaseAuthRepository
import com.titu.artistonboard.supabase.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ArtistDashboardRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client,
    private val authRepository: SupabaseAuthRepository = SupabaseAuthRepository(supabase)
) {
    suspend fun fetchDashboardData(): DashboardData? {
        val userId = authRepository.currentUserId() ?: return null
        return try {
            val profileResponse = supabase.from("artist_profiles").select(Columns.ALL) {
                filter { eq("user_id", userId) }
            }.data
            
            val profileArray = Json.parseToJsonElement(profileResponse).jsonArray
            if (profileArray.isEmpty()) return null
            val profileObj = profileArray[0].jsonObject
            
            // Also fetch live services status
            var isLiveActive = false
            try {
                val liveResponse = supabase.from("artist_live_services").select(Columns.ALL) {
                    filter { eq("artist_id", userId) }
                }.data
                val liveArray = Json.parseToJsonElement(liveResponse).jsonArray
                if (liveArray.isNotEmpty()) {
                    isLiveActive = liveArray[0].jsonObject["is_active"]?.toString()?.toBooleanStrictOrNull() ?: false
                }
            } catch (e: Exception) {
                // Live services row might not exist
            }
            
            DashboardData(
                brandName = profileObj["brand_name"]?.toString()?.removeSurrounding("\"") ?: "Artist",
                isLiveServicesActive = isLiveActive
            )
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun toggleLiveServices(isActive: Boolean) {
        if (authRepository.isDemoArtistSession()) return

        val userId = authRepository.currentUserId() ?: return
        try {
            supabase.from("artist_live_services").update(
                buildJsonObject {
                    put("is_active", isActive)
                }
            ) {
                filter { eq("artist_id", userId) }
            }
        } catch (e: Exception) {
            // Handle error or upsert if row doesn't exist
            supabase.from("artist_live_services").upsert(
                buildJsonObject {
                    put("artist_id", userId)
                    put("is_active", isActive)
                }
            ) {
                onConflict = "artist_id"
            }
        }
    }
}

data class DashboardData(
    val brandName: String,
    val isLiveServicesActive: Boolean
)
