package com.titu.artistonboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: Artist): Long

    @Query("SELECT * FROM artists ORDER BY createdAt DESC")
    suspend fun getAll(): List<Artist>
}