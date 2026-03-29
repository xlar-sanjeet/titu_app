package com.titu.artistonboard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Artist::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArtistDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao

    companion object {
        @Volatile private var INSTANCE: ArtistDatabase? = null

        fun getInstance(context: Context): ArtistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArtistDatabase::class.java,
                    "artist_onboard.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
