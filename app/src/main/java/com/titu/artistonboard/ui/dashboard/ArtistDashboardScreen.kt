package com.titu.artistonboard.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

val CreamyWhite = Color(0xFFFCFAF8)
val BurntOrange = Color(0xFFD35400)
val DarkNavy = Color(0xFF1E2A38)
val SoftGray = Color(0xFF8B95A1)
val CardBackground = Color.White

@Composable
fun ArtistDashboardScreen(
    onPreviewStorefront: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ArtistDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BurntOrange)
            }
        } else {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Welcome back,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftGray
                    )
                    Text(
                        text = state.brandName.ifBlank { "Artist" },
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkNavy
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(BurntOrange.copy(alpha = 0.2f))
                        .border(2.dp, BurntOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.brandName.takeIf { it.isNotBlank() }?.substring(0, 1) ?: "A",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurntOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Analytics Section
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = DarkNavy
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Profile Views",
                    value = state.profileViews.toString(),
                    trend = "+12%"
                )
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Inquiries",
                    value = state.totalInquiries.toString(),
                    trend = "+5%"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DashboardMetricCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Live Art Bookings",
                value = state.liveArtBookings.toString(),
                trend = null
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Live Services Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live Art Services",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = DarkNavy
                        )
                        Text(
                            text = if (state.isLiveServicesActive) "Currently accepting bookings" else "Currently inactive",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftGray
                        )
                    }
                    Switch(
                        checked = state.isLiveServicesActive,
                        onCheckedChange = { viewModel.toggleLiveServices(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BurntOrange,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0),
                            checkedBorderColor = Color.Transparent,
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = onPreviewStorefront,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Preview Storefront", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BurntOrange
                ),
                border = BorderStroke(1.dp, BurntOrange)
            ) {
                Text(text = "Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "Logout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = SoftGray)
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    trend: String?
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkNavy
                )
                if (trend != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trend,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF27AE60),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}
