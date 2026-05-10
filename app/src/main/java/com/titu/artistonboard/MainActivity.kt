package com.titu.artistonboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.titu.artistonboard.ui.theme.ArtistOnboardTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.titu.artistonboard.supabase.LocalArtistSession.initialize(this)

        setContent {
            ArtistOnboardTheme {
                AppNavHost()
            }
        }
    }
}

private object Routes {
    const val ROLE_SELECTION = "role_selection"
    const val USER_ONBOARDING = "user_onboarding"
    const val USER_MATCHES = "user_matches"
    const val WELCOME = "welcome"
    const val NAME_CITY = "name_city"
    const val CATEGORY = "category"
    const val STYLE = "style"
    const val LIVE_ART = "live_art"
    const val LIVE_ART_MODE = "live_art_mode"
    const val LIVE_ART_SERVICE_TYPES = "live_art_service_types"
    const val LIVE_ART_AVAILABLE_DATES = "live_art_available_dates"
    const val PRICE_RANGE = "price_range"
    const val MATERIAL = "material"
    const val CUSTOMIZATION = "customization"
    const val CAPACITY = "capacity"
    const val DELIVERY = "delivery"
    const val UPLOAD_PHOTOS = "upload_photos"
    const val ARTIST_STORY = "artist_story"
    const val AI_PROCESSING = "ai_processing"
    const val SUCCESS = "success"
    const val ARTIST_DASHBOARD = "artist_dashboard"
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    val artistViewModel: com.titu.artistonboard.supabase.SupabaseArtistOnboardingViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val offersLiveArtState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val physicalLiveArtState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }

    val startRoute = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            val repo = com.titu.artistonboard.supabase.SupabaseArtistOnboardingRepository()
            val profile = repo.fetchArtistProfile()
            if (profile?.toString()?.contains("\"onboarding_status\":\"completed\"") == true) {
                startRoute.value = Routes.ARTIST_DASHBOARD
            } else {
                startRoute.value = Routes.ROLE_SELECTION
            }
        } catch (e: Exception) {
            startRoute.value = Routes.ROLE_SELECTION
        }
    }

    if (startRoute.value == null) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
    }

    CompositionLocalProvider(LocalArtistViewModel provides artistViewModel) {
        NavHost(
            navController = navController,
            startDestination = startRoute.value!!
        ) {
            composable(Routes.ARTIST_DASHBOARD) {
                com.titu.artistonboard.ui.dashboard.ArtistDashboardScreen(
                    onPreviewStorefront = { },
                    onEditProfile = { },
                    onLogout = {
                        scope.launch {
                            com.titu.artistonboard.supabase.SupabaseAuthRepository().logout()
                            navController.navigate(Routes.ROLE_SELECTION) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Routes.ROLE_SELECTION) {
                RoleSelectionScreen(
                    onChooseUser = { navController.navigate(Routes.USER_ONBOARDING) },
                    onChooseArtist = { navController.navigate(Routes.WELCOME) }
                )
            }
            composable(Routes.USER_ONBOARDING) {
                UserOnboardingFlow(
                    goToMatches = { navController.navigate(Routes.USER_MATCHES) }
                )
            }
            composable(Routes.USER_MATCHES) {
                AIProcessingScreen(
                    onFinished = { navController.navigate(Routes.ROLE_SELECTION) }
                )
            }
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onGetStarted = { navController.navigate(Routes.NAME_CITY) }
                )
            }
            composable(Routes.NAME_CITY) {
                NameCityScreen(
                    onBack = { navController.popBackStack() },
                    onContinue = { navController.navigate(Routes.CATEGORY) },
                    onCompletedArtistLogin = {
                        navController.navigate(Routes.ARTIST_DASHBOARD) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.CATEGORY) {
                CategoryScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.STYLE) }
                )
            }
            composable(Routes.STYLE) {
                StyleScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.LIVE_ART) }
                )
            }
            composable(Routes.LIVE_ART) {
                LiveArtServicesScreen(
                    onBack = { navController.popBackStack() },
                    onSelectionChange = { offersLiveArtState.value = it },
                    onNext = {
                        if (offersLiveArtState.value) {
                            navController.navigate(Routes.LIVE_ART_MODE)
                        } else {
                            navController.navigate(Routes.MATERIAL)
                        }
                    }
                )
            }
            composable(Routes.LIVE_ART_MODE) {
                LiveArtModeScreen(
                    onBack = { navController.popBackStack() },
                    onTypeChange = { physicalLiveArtState.value = it },
                    onNext = {
                        if (physicalLiveArtState.value) {
                            navController.navigate(Routes.LIVE_ART_SERVICE_TYPES)
                        } else {
                            navController.navigate(Routes.MATERIAL)
                        }
                    }
                )
            }
            composable(Routes.LIVE_ART_SERVICE_TYPES) {
                LiveArtServiceTypesScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.LIVE_ART_AVAILABLE_DATES) }
                )
            }
            composable(Routes.LIVE_ART_AVAILABLE_DATES) {
                LiveArtAvailableDatesScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { navController.navigate(Routes.MATERIAL) }
                )
            }
            composable(Routes.MATERIAL) {
                MaterialScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.PRICE_RANGE) }
                )
            }
            composable(Routes.PRICE_RANGE) {
                PriceRangeScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.CUSTOMIZATION) }
                )
            }
            composable(Routes.CUSTOMIZATION) {
                CustomizationScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.CAPACITY) }
                )
            }
            composable(Routes.CAPACITY) {
                CapacityScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.DELIVERY) }
                )
            }
            composable(Routes.DELIVERY) {
                DeliveryScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.UPLOAD_PHOTOS) }
                )
            }
            composable(Routes.UPLOAD_PHOTOS) {
                UploadPhotosScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.ARTIST_STORY) }
                )
            }
            composable(Routes.ARTIST_STORY) {
                ArtistStoryScreen(
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate(Routes.AI_PROCESSING) }
                )
            }
            composable(Routes.AI_PROCESSING) {
                AIProcessingScreen(
                    onFinished = {
                        navController.navigate(Routes.SUCCESS) {
                            popUpTo(Routes.AI_PROCESSING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.SUCCESS) {
                SuccessScreen(
                    onBack = { navController.popBackStack() },
                    onGoToDashboard = {
                        navController.navigate(Routes.ARTIST_DASHBOARD) {
                            popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
