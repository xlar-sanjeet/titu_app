package com.titu.artistonboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArtistDashboardState(
    val isLoading: Boolean = true,
    val brandName: String = "",
    val profileViews: Int = 1240, // Dummy data
    val totalInquiries: Int = 42, // Dummy data
    val liveArtBookings: Int = 8, // Dummy data
    val isLiveServicesActive: Boolean = false
)

class ArtistDashboardViewModel(
    private val repository: ArtistDashboardRepository = ArtistDashboardRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ArtistDashboardState())
    val state: StateFlow<ArtistDashboardState> = _state.asStateFlow()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val data = repository.fetchDashboardData()
            if (data != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        brandName = data.brandName,
                        isLiveServicesActive = data.isLiveServicesActive
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleLiveServices(isActive: Boolean) {
        viewModelScope.launch {
            // Optimistic update
            _state.update { it.copy(isLiveServicesActive = isActive) }
            try {
                repository.toggleLiveServices(isActive)
            } catch (e: Exception) {
                // Revert on failure
                _state.update { it.copy(isLiveServicesActive = !isActive) }
            }
        }
    }
}
