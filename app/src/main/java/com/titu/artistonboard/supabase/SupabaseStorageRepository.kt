package com.titu.artistonboard.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage

class SupabaseStorageRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client
) {
    suspend fun uploadLiveSelfie(
        userId: String,
        bytes: ByteArray,
        fileExtension: String = "jpg"
    ): String {
        val path = "$userId/live_selfie.$fileExtension"
        supabase.storage.from("artist-selfies").upload(path, bytes) {
            upsert = true
        }
        return path
    }

    suspend fun uploadWorkPhoto(
        userId: String,
        fileName: String,
        bytes: ByteArray
    ): String {
        val path = "$userId/$fileName"
        supabase.storage.from("artist-works").upload(path, bytes) {
            upsert = true
        }
        return path
    }
}
