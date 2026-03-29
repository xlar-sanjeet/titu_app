package com.titu.artistonboard.data

import androidx.room.TypeConverter

object Converters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString("|")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split("|")
    }
}