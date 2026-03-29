package com.titu.artistonboard

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.titu.artistonboard.data.Artist
import com.titu.artistonboard.data.ArtistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

private const val MAX_IMAGES = 2
private const val PINCODE_LENGTH = 6

private const val ERR_FULL_NAME = "fullName"
private const val ERR_BRAND = "brandName"
private const val ERR_PINCODE = "pincode"
private const val ERR_STATE = "state"
private const val ERR_CITY = "city"
private const val ERR_WHATSAPP = "whatsapp"
private const val ERR_EMAIL = "email"
private const val ERR_EXPERIENCE = "experience"
private const val ERR_ART_CATEGORY = "artCategory"
private const val ERR_ART_SUBCATEGORY = "artSubcategory"
private const val ERR_RETAIL_DELIVERY = "retailDelivery"
private const val ERR_BULK_DELIVERY = "bulkDelivery"
private const val ERR_SAME_DAY = "sameDay"
private const val ERR_RETAIL_COST = "retailCost"
private const val ERR_BULK_COST = "bulkCost"
private const val ERR_COST_RANGE = "costRange"
private const val ERR_IMAGES = "images"

class ArtistOnboardingViewModel(private val repository: ArtistRepository) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state
    private var lastPincodeLookup: String? = null

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.UpdateFullName -> updateField(ERR_FULL_NAME) { it.copy(fullName = action.value) }
            is OnboardingAction.UpdateBrandName -> updateField(ERR_BRAND) { it.copy(brandName = action.value) }
            is OnboardingAction.UpdatePincode -> handlePincode(action.value)
            is OnboardingAction.UpdateCity -> updateField(ERR_CITY) { it.copy(city = action.value) }
            is OnboardingAction.UpdateState -> updateState {
                it.copy(state = action.value, city = "", fieldErrors = it.fieldErrors - ERR_STATE - ERR_CITY)
            }
            is OnboardingAction.UpdateWhatsapp -> updateField(ERR_WHATSAPP) { it.copy(whatsappNumber = normalizeWhatsapp(action.value)) }
            is OnboardingAction.UpdateEmail -> updateField(ERR_EMAIL) { it.copy(email = action.value) }
            is OnboardingAction.UpdateYearsExperience -> updateField(ERR_EXPERIENCE) { it.copy(yearsOfExperience = action.value) }
            is OnboardingAction.UpdateArtCategory -> updateState {
                it.copy(
                    artCategory = action.value,
                    artSubcategory = "",
                    fieldErrors = it.fieldErrors - ERR_ART_CATEGORY - ERR_ART_SUBCATEGORY
                )
            }
            is OnboardingAction.UpdateArtSubcategory -> updateField(ERR_ART_SUBCATEGORY) { it.copy(artSubcategory = action.value) }
            is OnboardingAction.UpdateRetailDelivery -> updateField(ERR_RETAIL_DELIVERY) { it.copy(retailDeliveryTime = action.value) }
            is OnboardingAction.UpdateBulkDelivery -> updateField(ERR_BULK_DELIVERY) { it.copy(bulkDeliveryTime = action.value) }
            is OnboardingAction.UpdateSameDayDelivery -> updateField(ERR_SAME_DAY) { it.copy(sameDayDelivery = action.value) }
            is OnboardingAction.UpdateRetailCost -> updateField(ERR_RETAIL_COST) { it.copy(retailProductCost = action.value) }
            is OnboardingAction.UpdateBulkCost -> updateField(ERR_BULK_COST) { it.copy(bulkProductCost = action.value) }
            is OnboardingAction.UpdateCostRange -> updateField(ERR_COST_RANGE) { it.copy(costRange = action.value) }
            is OnboardingAction.AddImages -> addImages(action.uris)
            OnboardingAction.RemoveAllImages -> updateState { it.copy(imageUris = emptyList()) }
            OnboardingAction.Next -> goNext()
            OnboardingAction.Back -> goBack()
            OnboardingAction.Submit -> submit()
            OnboardingAction.DismissMessage -> updateState { it.copy(message = null) }
        }
    }

    private fun addImages(uris: List<String>) {
        updateState { current ->
            val merged = (current.imageUris + uris).distinct().take(MAX_IMAGES)
            current.copy(imageUris = merged, fieldErrors = current.fieldErrors - ERR_IMAGES)
        }
    }

    private fun goNext() {
        val current = _state.value
        val errors = validateStep(current.step, current)
        if (errors.isNotEmpty()) {
            updateState { it.copy(fieldErrors = errors) }
            return
        }
        updateState { it.copy(step = (it.step + 1).coerceAtMost(3), fieldErrors = emptyMap()) }
    }

    private fun goBack() {
        updateState { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }

    private fun submit() {
        val current = _state.value
        val errors = validateStep(3, current)
        if (errors.isNotEmpty()) {
            updateState { it.copy(fieldErrors = errors) }
            return
        }

        updateState { it.copy(isSubmitting = true, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            val artist = Artist(
                fullName = current.fullName.trim(),
                brandName = current.brandName.trim(),
                pincode = current.pincode.trim(),
                city = current.city.trim(),
                state = current.state.trim(),
                whatsappNumber = current.whatsappNumber.trim(),
                email = current.email.trim(),
                yearsOfExperience = current.yearsOfExperience.trim(),
                artCategory = current.artCategory.trim(),
                artSubcategory = current.artSubcategory.trim(),
                retailDeliveryTime = current.retailDeliveryTime.trim(),
                bulkDeliveryTime = current.bulkDeliveryTime.trim(),
                sameDayDelivery = current.sameDayDelivery.trim(),
                retailProductCost = current.retailProductCost.trim(),
                bulkProductCost = current.bulkProductCost.trim(),
                costRange = current.costRange.trim(),
                imageUris = current.imageUris,
                enhancementStatus = "QUEUED",
                enhancementJobId = null,
                createdAt = System.currentTimeMillis()
            )

            repository.saveArtist(artist)

            updateState {
                it.copy(
                    isSubmitting = false,
                    submitted = true,
                    message = "Artist profile saved"
                )
            }
        }
    }

    private fun validateStep(step: Int, state: OnboardingUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        when (step) {
            0 -> {
                if (state.fullName.isBlank()) errors[ERR_FULL_NAME] = "Full name is required"
                if (state.brandName.isBlank()) errors[ERR_BRAND] = "Brand name is required"
                if (state.pincode.isBlank()) errors[ERR_PINCODE] = "Pincode is required"
                if (state.pincode.isNotBlank() && !isValidPincode(state.pincode)) {
                    errors[ERR_PINCODE] = "Enter a valid 6-digit pincode"
                }
                if (state.state.isBlank()) errors[ERR_STATE] = "State is required"
                if (state.city.isBlank()) errors[ERR_CITY] = "City is required"
                if (state.whatsappNumber.isBlank()) errors[ERR_WHATSAPP] = "WhatsApp/Contact number is required"
                if (state.whatsappNumber.isNotBlank() && !isValidWhatsapp(state.whatsappNumber)) {
                    errors[ERR_WHATSAPP] = "Number must start with +91 and have 10 digits"
                }
                if (state.email.isBlank()) errors[ERR_EMAIL] = "Email is required"
                if (state.email.isNotBlank() && !isValidEmail(state.email)) {
                    errors[ERR_EMAIL] = "Enter a valid email address"
                }
                if (state.yearsOfExperience.isBlank()) errors[ERR_EXPERIENCE] = "Select years of experience"
            }
            1 -> {
                if (state.artCategory.isBlank()) errors[ERR_ART_CATEGORY] = "Select an art category"
                if (state.artSubcategory.isBlank()) errors[ERR_ART_SUBCATEGORY] = "Select an art subcategory"
                if (state.retailDeliveryTime.isBlank()) errors[ERR_RETAIL_DELIVERY] = "Retail delivery time is required"
                if (state.retailDeliveryTime.isNotBlank() && !state.retailDeliveryTime.all { it.isDigit() }) {
                    errors[ERR_RETAIL_DELIVERY] = "Retail delivery time must be in days"
                }
                if (state.bulkDeliveryTime.isBlank()) errors[ERR_BULK_DELIVERY] = "Bulk delivery time is required"
                if (state.bulkDeliveryTime.isNotBlank() && !state.bulkDeliveryTime.all { it.isDigit() }) {
                    errors[ERR_BULK_DELIVERY] = "Bulk delivery time must be in days"
                }
                if (state.sameDayDelivery.isBlank()) errors[ERR_SAME_DAY] = "Select same day delivery"
                if (state.retailProductCost.isBlank()) errors[ERR_RETAIL_COST] = "Retail cost is required"
                if (state.bulkProductCost.isBlank()) errors[ERR_BULK_COST] = "Bulk cost is required"
                if (state.costRange.isBlank()) errors[ERR_COST_RANGE] = "Average cost range is required"
            }
            2, 3 -> {
                if (state.imageUris.isEmpty()) errors[ERR_IMAGES] = "Please upload at least one image"
            }
        }
        return errors
    }

    private fun updateState(block: (OnboardingUiState) -> OnboardingUiState) {
        _state.update(block)
    }

    private fun updateField(errorKey: String, block: (OnboardingUiState) -> OnboardingUiState) {
        _state.update { current ->
            val updated = block(current)
            updated.copy(fieldErrors = updated.fieldErrors - errorKey)
        }
    }

    private fun handlePincode(value: String) {
        updateField(ERR_PINCODE) {
            it.copy(
                pincode = value.filter { ch -> ch.isDigit() }.take(PINCODE_LENGTH),
                pincodeLookupError = null
            )
        }
        val pincode = _state.value.pincode
        if (pincode.length == PINCODE_LENGTH && pincode != lastPincodeLookup) {
            fetchCityStateForPincode(pincode)
        }
    }

    private fun fetchCityStateForPincode(pincode: String) {
        lastPincodeLookup = pincode
        updateState { it.copy(isPincodeLoading = true, pincodeLookupError = null) }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { lookupPincode(pincode) }
            if (result == null) {
                updateState {
                    it.copy(
                        isPincodeLoading = false,
                        pincodeLookupError = "Unable to fetch city/state for this pincode"
                    )
                }
            } else {
                updateState {
                    val updated = it.copy(
                        isPincodeLoading = false,
                        pincodeLookupError = null,
                        city = result.first,
                        state = result.second
                    )
                    updated.copy(fieldErrors = updated.fieldErrors - ERR_STATE - ERR_CITY)
                }
            }
        }
    }

    private fun lookupPincode(pincode: String): Pair<String, String>? {
        val url = URL("https://api.postalpincode.in/pincode/$pincode")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val array = JSONArray(response)
            if (array.length() == 0) return null
            val first = array.getJSONObject(0)
            if (first.optString("Status") != "Success") return null
            val postOffices = first.getJSONArray("PostOffice")
            if (postOffices.length() == 0) return null
            val office = postOffices.getJSONObject(0)
            val city = office.optString("District")
            val state = office.optString("State")
            if (city.isBlank() || state.isBlank()) null else city to state
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun normalizeWhatsapp(input: String): String {
        val digits = input.filter { it.isDigit() }
        val national = if (digits.startsWith("91")) digits.drop(2) else digits
        return "+91" + national.take(10)
    }

    private fun isValidWhatsapp(value: String): Boolean {
        return value.matches(Regex("^\\+91\\d{10}$"))
    }

    private fun isValidEmail(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
    }

    private fun isValidPincode(value: String): Boolean {
        return value.matches(Regex("^\\d{6}$"))
    }

    class Factory(private val repository: ArtistRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArtistOnboardingViewModel(repository) as T
        }
    }
}

data class OnboardingUiState(
    val step: Int = 0,
    val fullName: String = "",
    val brandName: String = "",
    val pincode: String = "",
    val city: String = "",
    val state: String = "",
    val whatsappNumber: String = "+91",
    val email: String = "",
    val yearsOfExperience: String = "",
    val artCategory: String = "",
    val artSubcategory: String = "",
    val retailDeliveryTime: String = "",
    val bulkDeliveryTime: String = "",
    val sameDayDelivery: String = "",
    val retailProductCost: String = "",
    val bulkProductCost: String = "",
    val costRange: String = "",
    val imageUris: List<String> = emptyList(),
    val isPincodeLoading: Boolean = false,
    val pincodeLookupError: String? = null,
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val message: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

sealed class OnboardingAction {
    data class UpdateFullName(val value: String) : OnboardingAction()
    data class UpdateBrandName(val value: String) : OnboardingAction()
    data class UpdatePincode(val value: String) : OnboardingAction()
    data class UpdateCity(val value: String) : OnboardingAction()
    data class UpdateState(val value: String) : OnboardingAction()
    data class UpdateWhatsapp(val value: String) : OnboardingAction()
    data class UpdateEmail(val value: String) : OnboardingAction()
    data class UpdateYearsExperience(val value: String) : OnboardingAction()
    data class UpdateArtCategory(val value: String) : OnboardingAction()
    data class UpdateArtSubcategory(val value: String) : OnboardingAction()
    data class UpdateRetailDelivery(val value: String) : OnboardingAction()
    data class UpdateBulkDelivery(val value: String) : OnboardingAction()
    data class UpdateSameDayDelivery(val value: String) : OnboardingAction()
    data class UpdateRetailCost(val value: String) : OnboardingAction()
    data class UpdateBulkCost(val value: String) : OnboardingAction()
    data class UpdateCostRange(val value: String) : OnboardingAction()
    data class AddImages(val uris: List<String>) : OnboardingAction()
    data object RemoveAllImages : OnboardingAction()
    data object Next : OnboardingAction()
    data object Back : OnboardingAction()
    data object Submit : OnboardingAction()
    data object DismissMessage : OnboardingAction()
}
