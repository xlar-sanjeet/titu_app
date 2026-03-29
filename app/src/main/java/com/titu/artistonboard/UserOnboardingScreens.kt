package com.titu.artistonboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val UserBg = Color(0xFFF6F1EA)
private val UserPrimary = Color(0xFFC96A3D)
private val UserDeep = Color(0xFF1F2A44)
private val UserBodoni = FontFamily(Font(R.font.bodoni_moda_opsz_wght))
private val UserStepLabels = listOf("Occasion", "Budget", "Style", "Timeline")

private data class OccasionItem(val label: String, val resId: Int)
private data class TimelineItem(val label: String, val resId: Int)

@Composable
fun UserWelcomeScreen(
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UserBg)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.user_onboard_welcome_gift_box),
            contentDescription = "Welcome gift illustration",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(240.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your perfect\nhandmade piece\nawaits",
            color = Color(0xFF2E2A2A),
            textAlign = TextAlign.Center,
            fontFamily = UserBodoni,
            fontWeight = FontWeight.Medium,
            fontSize = 48.sp,
            lineHeight = 56.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AI-powered discovery\nfor meaningful gifting",
            color = Color(0xFF5D5D5D),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = UserPrimary,
                contentColor = Color.White
            )
        ) {
            Text(text = "Start Matching", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Browse curated drops  >",
            color = Color(0xFF6B6B6B),
            fontSize = 16.sp
        )
    }
}

@Composable
fun UserOccasionScreen(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onStepClick: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val items = listOf(
        OccasionItem("Birthday", R.drawable.user_onboard_occ_birthday),
        OccasionItem("Anniversary", R.drawable.user_onboard_occ_anniversary),
        OccasionItem("Wedding", R.drawable.user_onboard_occ_wedding),
        OccasionItem("Festive", R.drawable.user_onboard_occ_festive),
        OccasionItem("Corporate", R.drawable.user_onboard_occ_corporate),
        OccasionItem("Just for Me", R.drawable.user_onboard_occ_just_for_me)
    )

    UserStepScaffold(
        stepIndex = 0,
        title = "What's the occasion?",
        onStepClick = onStepClick,
        onBack = onBack,
        onNext = onNext
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                val isSelected = selected.contains(item.label)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(item.label) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) UserPrimary.copy(alpha = 0.16f) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) UserPrimary else Color(0xFFE7DDD3)
                    )
                ) {
                    Image(
                        painter = painterResource(item.resId),
                        contentDescription = item.label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = item.label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) UserPrimary else Color(0xFF3B3532),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun UserBudgetScreen(
    selectedBudget: String?,
    onSelect: (String) -> Unit,
    onStepClick: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val budgets = listOf(
        "Under Rs 1,000",
        "Rs 1,000 - Rs 3,000",
        "Rs 3,000 - Rs 7,000",
        "Rs 7,000 - Rs 15,000",
        "Rs 15,000 - Rs 30,000",
        "Flexible"
    )

    UserStepScaffold(
        stepIndex = 1,
        title = "What's your budget?",
        onStepClick = onStepClick,
        onBack = onBack,
        onNext = onNext
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            budgets.forEach { item ->
                val isSelected = item == selectedBudget
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) UserPrimary.copy(alpha = 0.12f) else Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, if (isSelected) UserPrimary else Color(0xFFE7DDD3), RoundedCornerShape(16.dp))
                        .clickable { onSelect(item) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (isSelected) UserPrimary else Color(0xFFD5C8BB), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = item, color = Color(0xFF3B3532), fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun UserStyleScreen(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onStepClick: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val styles = listOf("Minimal", "Traditional", "Elegant", "Rustic", "Luxury", "Playful")

    UserStepScaffold(
        stepIndex = 2,
        title = "What's your style?",
        onStepClick = onStepClick,
        onBack = onBack,
        onNext = onNext
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            styles.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { style ->
                        val isSelected = selected.contains(style)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) UserPrimary.copy(alpha = 0.16f) else Color.White,
                                    RoundedCornerShape(16.dp)
                                )
                                .border(1.dp, if (isSelected) UserPrimary else Color(0xFFE7DDD3), RoundedCornerShape(16.dp))
                                .clickable { onToggle(style) }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = style,
                                color = if (isSelected) UserPrimary else Color(0xFF3B3532),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun UserTimelineScreen(
    selectedTimeline: String?,
    onSelect: (String) -> Unit,
    onStepClick: (Int) -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val items = listOf(
        TimelineItem("Within 3 days", R.drawable.user_onboard_time_within_3_days),
        TimelineItem("Within a week", R.drawable.user_onboard_time_within_week),
        TimelineItem("2+ weeks", R.drawable.user_onboard_time_2_weeks),
        TimelineItem("Flexible", R.drawable.user_onboard_time_flexible)
    )

    UserStepScaffold(
        stepIndex = 3,
        title = "When do you need it?",
        onStepClick = onStepClick,
        onBack = onBack,
        onNext = onFinish,
        nextLabel = "See Matches"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                val isSelected = item.label == selectedTimeline
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) UserPrimary.copy(alpha = 0.90f) else Color.White, RoundedCornerShape(18.dp))
                        .border(1.dp, if (isSelected) UserPrimary else Color(0xFFE7DDD3), RoundedCornerShape(18.dp))
                        .clickable { onSelect(item.label) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(item.resId),
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(62.dp)
                            .background(Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = item.label,
                        color = if (isSelected) Color.White else Color(0xFF3B3532),
                        fontSize = 18.sp,
                        fontFamily = UserBodoni
                    )
                }
            }
        }
    }
}

@Composable
private fun UserStepScaffold(
    stepIndex: Int,
    title: String,
    onStepClick: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextLabel: String = "Next",
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UserBg)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF7B6E62),
                modifier = Modifier.clickable { onBack() }
            )

            UserProgressHeader(currentStep = stepIndex, onStepClick = onStepClick)

            Text(
                text = title,
                color = Color(0xFF2E2A2A),
                fontFamily = UserBodoni,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 30.sp
            )

            content()
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = UserPrimary,
                contentColor = Color.White
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = nextLabel, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (nextLabel == "Next") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun UserProgressHeader(
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserStepLabels.forEachIndexed { index, label ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onStepClick(index) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(if (currentStep == index) 11.dp else 8.dp)
                            .background(if (index <= currentStep) UserPrimary else Color(0xFFD8CFC7), CircleShape)
                    )
                    if (index < UserStepLabels.lastIndex) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(26.dp)
                                .background(if (index < currentStep) UserPrimary else Color(0xFFE0D8CF))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (index == currentStep) Color(0xFF2E2A2A) else Color(0xFF8B8177),
                    fontWeight = if (index == currentStep) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun UserOnboardingFlow(
    goToMatches: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var selectedOccasions by remember { mutableStateOf(emptySet<String>()) }
    var selectedBudget by remember { mutableStateOf<String?>(null) }
    var selectedStyles by remember { mutableStateOf(emptySet<String>()) }
    var selectedTimeline by remember { mutableStateOf<String?>(null) }

    when (step) {
        0 -> UserWelcomeScreen(onStart = { step = 1 })
        1 -> UserOccasionScreen(
            selected = selectedOccasions,
            onToggle = { item ->
                selectedOccasions = if (selectedOccasions.contains(item)) selectedOccasions - item else selectedOccasions + item
            },
            onStepClick = { target -> step = target + 1 },
            onNext = { step = 2 },
            onBack = { step = 0 }
        )
        2 -> UserBudgetScreen(
            selectedBudget = selectedBudget,
            onSelect = { selectedBudget = it },
            onStepClick = { target -> step = target + 1 },
            onNext = { step = 3 },
            onBack = { step = 1 }
        )
        3 -> UserStyleScreen(
            selected = selectedStyles,
            onToggle = { item ->
                selectedStyles = if (selectedStyles.contains(item)) selectedStyles - item else selectedStyles + item
            },
            onStepClick = { target -> step = target + 1 },
            onNext = { step = 4 },
            onBack = { step = 2 }
        )
        else -> UserTimelineScreen(
            selectedTimeline = selectedTimeline,
            onSelect = { selectedTimeline = it },
            onStepClick = { target -> step = target + 1 },
            onFinish = goToMatches,
            onBack = { step = 3 }
        )
    }
}
