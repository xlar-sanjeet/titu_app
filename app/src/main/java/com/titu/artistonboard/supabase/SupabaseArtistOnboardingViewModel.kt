package com.titu.artistonboard.supabase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.buildJsonArray

data class OnboardingDraftState(
    val isLoadingDraft: Boolean = true,
    
    // Step 1: Identity
    val brandName: String = "",
    val liveSelfiePath: String? = null,
    
    // Step 2: Categories
    val selectedCategories: Set<String> = emptySet(),
    
    // Step 3: Styles
    val selectedStyles: Set<String> = emptySet(),
    
    // Step 4: Live Services
    val offersLiveServices: Boolean = false,
    
    // Step 5: Live Art Mode
    val liveArtMode: String = "",
    val liveArtCities: Set<String> = emptySet(),
    val liveArtRadius: Float = 0f,
    
    // Step 6: Live Art Services Types
    val liveArtServiceTypes: Set<String> = emptySet(),
    
    // Step 7: Live Art Dates
    val liveArtDates: Set<String> = emptySet(),
    val liveArtCharge: String = "",
    
    // Step 8: Price Range
    val basePriceRange: Float = 1000f,
    
    // Step 9: Customization
    val offersCustomization: Boolean = false,
    val customizationTime: String = "",
    
    // Step 10: Capacity
    val acceptsBulkOrders: Boolean = false,
    val bulkCapacity: String = "",
    
    // Step 11: Materials
    val materials: Set<String> = emptySet(),
    
    // Step 12: Delivery
    val deliveryCity: String = "",
    val sameCityDelivery: Boolean = false,
    val deliverySpeed: String = "",
    
    // Step 13: Upload Photos
    val portfolioUrls: List<String> = emptyList(),
    
    // Step 14: Artist Story
    val storyBio: String = ""
)

class SupabaseArtistOnboardingViewModel(
    private val repository: SupabaseArtistOnboardingRepository = SupabaseArtistOnboardingRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingDraftState())
    val state: StateFlow<OnboardingDraftState> = _state.asStateFlow()

    init {
        fetchDraft()
    }

    private fun fetchDraft() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingDraft = true) }
            val profile = repository.fetchArtistProfile()
            if (profile != null) {
                _state.update { current ->
                    current.copy(
                        isLoadingDraft = false,
                        brandName = profile["brand_name"]?.jsonPrimitive?.content ?: "",
                        liveSelfiePath = profile["live_selfie_url"]?.jsonPrimitive?.content,
                        selectedCategories = profile["categories"]?.jsonArray?.map { it.jsonPrimitive.content }?.toSet() ?: emptySet(),
                        selectedStyles = profile["styles"]?.jsonArray?.map { it.jsonPrimitive.content }?.toSet() ?: emptySet(),
                        basePriceRange = profile["price_range"]?.jsonPrimitive?.doubleOrNull?.toFloat() ?: 1000f,
                        offersCustomization = profile["customization_enabled"]?.jsonPrimitive?.booleanOrNull ?: false,
                        customizationTime = profile["customization_time"]?.jsonPrimitive?.content ?: "",
                        acceptsBulkOrders = profile["bulk_order_enabled"]?.jsonPrimitive?.booleanOrNull ?: false,
                        bulkCapacity = profile["bulk_capacity"]?.jsonPrimitive?.content ?: "",
                        materials = profile["materials"]?.jsonArray?.map { it.jsonPrimitive.content }?.toSet() ?: emptySet(),
                        deliveryCity = profile["delivery_city"]?.jsonPrimitive?.content ?: "",
                        sameCityDelivery = profile["same_city_delivery"]?.jsonPrimitive?.booleanOrNull ?: false,
                        deliverySpeed = profile["delivery_speed"]?.jsonPrimitive?.content ?: "",
                        portfolioUrls = profile["portfolio_urls"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
                        storyBio = profile["story_bio"]?.jsonPrimitive?.content ?: ""
                    )
                }
            } else {
                _state.update { it.copy(isLoadingDraft = false) }
            }
        }
    }

    fun updateState(block: (OnboardingDraftState) -> OnboardingDraftState) {
        _state.update(block)
    }


    suspend fun saveIncrementalState() {
        val currentState = _state.value
        val updates = buildJsonObject {
            put("categories", buildJsonArray {
                currentState.selectedCategories.forEach { add(it) }
            })
            put("styles", buildJsonArray {
                currentState.selectedStyles.forEach { add(it) }
            })
            put("price_range", currentState.basePriceRange)
            put("customization_enabled", currentState.offersCustomization)
            if (currentState.customizationTime.isNotBlank()) {
                put("customization_time", currentState.customizationTime)
            }
            put("bulk_order_enabled", currentState.acceptsBulkOrders)
            if (currentState.bulkCapacity.isNotBlank()) {
                put("bulk_capacity", currentState.bulkCapacity)
            }
            put("materials", buildJsonArray {
                currentState.materials.forEach { add(it) }
            })
            if (currentState.deliveryCity.isNotBlank()) {
                put("delivery_city", currentState.deliveryCity)
            }
            put("same_city_delivery", currentState.sameCityDelivery)
            if (currentState.deliverySpeed.isNotBlank()) {
                put("delivery_speed", currentState.deliverySpeed)
            }
            put("portfolio_urls", buildJsonArray {
                currentState.portfolioUrls.forEach { add(it) }
            })
            if (currentState.storyBio.isNotBlank()) {
                put("story_bio", currentState.storyBio)
            }
        }
        
        repository.updateArtistProfilePartial(updates)
        
        if (currentState.offersLiveServices || currentState.liveArtMode.isNotBlank()) {
            val liveUpdates = buildJsonObject {
                put("is_active", currentState.offersLiveServices)
                put("mode", currentState.liveArtMode)
                put("service_types", buildJsonArray {
                    currentState.liveArtServiceTypes.forEach { add(it) }
                })
                put("available_cities", buildJsonArray {
                    currentState.liveArtCities.forEach { add(it) }
                })
                put("travel_radius_km", currentState.liveArtRadius)
                if (currentState.liveArtCharge.isNotBlank()) {
                    put("charge_per_session", currentState.liveArtCharge.toDoubleOrNull() ?: 0.0)
                }
                put("available_dates", buildJsonArray {
                    currentState.liveArtDates.forEach { add(it) }
                })
            }
            repository.upsertLiveArtServices(liveUpdates)
        }
    }

}
