package com.titu.artistonboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val brandName: String,
    val pincode: String,
    val city: String,
    val state: String,
    val whatsappNumber: String,
    val email: String,
    val yearsOfExperience: String,
    val artCategory: String,
    val artSubcategory: String,
    val retailDeliveryTime: String,
    val bulkDeliveryTime: String,
    val sameDayDelivery: String,
    val retailProductCost: String,
    val bulkProductCost: String,
    val costRange: String,
    val imageUris: List<String>,
    val enhancementStatus: String,
    val enhancementJobId: String?,
    val createdAt: Long
)
