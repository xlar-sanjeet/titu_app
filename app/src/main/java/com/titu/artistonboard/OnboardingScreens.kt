package com.titu.artistonboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import com.titu.artistonboard.ui.components.InputField
import com.titu.artistonboard.ui.components.OptionCard
import com.titu.artistonboard.ui.components.PrimaryButton
import com.titu.artistonboard.ui.components.ProgressIndicator
import com.titu.artistonboard.ui.theme.BorderSoft
import com.titu.artistonboard.ui.theme.BurntOrange
import com.titu.artistonboard.ui.theme.CreamBackground
import com.titu.artistonboard.ui.theme.DeepBlue
import com.titu.artistonboard.ui.theme.TextPrimary
import com.titu.artistonboard.ui.theme.TextSecondary
import com.titu.artistonboard.ui.theme.ArtistOnboardTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.delay
import java.util.regex.Pattern
import kotlin.math.exp
import kotlin.math.ln

private val BodoniModa = FontFamily(Font(R.font.bodoni_moda_opsz_wght))
private val EmailRegex: Pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

private fun Set<String>.toggle(value: String): Set<String> {
    return if (contains(value)) this - value else this + value
}

private fun formatRupee(value: Float): String {
    val rounded = value.toInt()
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "\u20B9 " + formatter.format(rounded)
}

private class RupeeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        if (digits.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        val formattedDigits = formatter.format(digits.toLong())
        val prefix = "\u20B9 "
        val formatted = prefix + formattedDigits

        val digitPositions = IntArray(digits.length)
        var digitIndex = 0
        for (i in formattedDigits.indices) {
            if (formattedDigits[i].isDigit()) {
                digitPositions[digitIndex] = i
                digitIndex++
                if (digitIndex == digits.length) break
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val safe = offset.coerceAtMost(digitPositions.size)
                val pos = digitPositions[safe - 1]
                return prefix.length + pos + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                val adjusted = (offset - prefix.length).coerceAtLeast(0)
                var count = 0
                for (i in formattedDigits.indices) {
                    if (formattedDigits[i].isDigit()) {
                        if (i >= adjusted) {
                            return count
                        }
                        count++
                    }
                }
                return digits.length
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}


@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your art deserves\nthe right audience",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = BodoniModa,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                color = Color(0xFF3A4F6A)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(6.dp))

        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.welcome_art),
            contentDescription = "Artist tools illustration",
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "AI-powered discovery\nfor handmade artists",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = Color(0xFF6B6B6B)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC5633E),
                contentColor = Color.White
            )
        ) {
            Text(text = "Start Free Profile\u26A1", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Takes less than 3 minutes",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = Color(0xFF8A8A8A)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun NameCityScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var fullName by remember { mutableStateOf("Test Artist") }
    var whatsappNumber by remember { mutableStateOf("9876543210") }
    var emailId by remember { mutableStateOf("artist@test.com") }
    val isValidPhone = whatsappNumber.length == 10
    val isValidEmail = emailId.isNotBlank() && EmailRegex.matcher(emailId).matches()
    val canContinue = fullName.isNotBlank() && isValidPhone && isValidEmail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top
    ) {
            Spacer(modifier = Modifier.height(92.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What's your name?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = BodoniModa,
                        fontSize = 42.sp,
                        lineHeight = 46.sp
                    ),
                    color = DeepBlue,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = "Enter your basic information",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                BasicInfoInput(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Full name / Brand name",
                    icon = Icons.Default.Person,
                    iconTint = BurntOrange,
                    highlight = true
                )

                BasicInfoInput(
                    value = whatsappNumber,
                    onValueChange = {
                        val digitsOnly = it.filter { ch -> ch.isDigit() }.take(10)
                        whatsappNumber = digitsOnly
                    },
                    placeholder = "WhatsApp number",
                    icon = Icons.Default.Phone,
                    iconTint = Color(0xFF4CAF50),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )

                if (whatsappNumber.isNotEmpty() && !isValidPhone) {
                    Text(
                        text = "Enter a valid 10-digit WhatsApp number",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB04A2A),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                BasicInfoInput(
                    value = emailId,
                    onValueChange = { emailId = it },
                    placeholder = "Enter your email ID",
                    icon = Icons.Default.Email,
                    iconTint = BurntOrange,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )

                if (emailId.isNotEmpty() && !isValidEmail) {
                    Text(
                        text = "Enter a valid email address",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB04A2A),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BurntOrange,
                contentColor = Color.White,
                disabledContainerColor = BurntOrange.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.75f)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Continue", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun BasicInfoInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(
                width = if (highlight) 2.dp else 1.dp,
                color = if (highlight) BurntOrange else BorderSoft,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            decorationBox = { innerTextField ->
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
                innerTextField()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CategoryScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var selectedCategories by remember { mutableStateOf(emptySet<String>()) }
    val canContinueCategory = selectedCategories.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "What do you create?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )

            val categories = listOf(
                CategoryCard("Handmade Jewellery", R.drawable.cat_jewellery, DeepBlue),
                CategoryCard("Paintings", R.drawable.cat_painting, Color(0xFFC97A4E)),
                CategoryCard("Home Decor", R.drawable.cat_homedecor, Color(0xFF9A7E6A)),
                CategoryCard("Personalized Gifts", R.drawable.cat_personalized, BurntOrange)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(392.dp)
            ) {
                items(categories) { item ->
                    CategoryCardItem(
                        item = item,
                        selected = selectedCategories.contains(item.title),
                        onClick = { selectedCategories = selectedCategories.toggle(item.title) }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueCategory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private data class CategoryCard(
    val title: String,
    val imageRes: Int,
    val barColor: Color
)

@Composable
private fun CategoryCardItem(
    item: CategoryCard,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(20.dp)
                            .background(Color(0xFF2E7D32), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = "\u2713",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(item.barColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StyleScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val styles = listOf(
        StyleCard("Minimal", R.drawable.minimal, DeepBlue),
        StyleCard("Traditional", R.drawable.traditional, Color(0xFF9A7E6A)),
        StyleCard("Modern", R.drawable.modern, BurntOrange),
        StyleCard("Boho", R.drawable.boho, Color(0xFFC97A4E))
    )
    var selectedStyles by remember { mutableStateOf(emptySet<String>()) }
    val canContinueStyle = selectedStyles.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "How would you describe your style?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(392.dp)
            ) {
                items(styles) { item ->
                    StyleCardItem(
                        item = item,
                        selected = selectedStyles.contains(item.title),
                        onClick = { selectedStyles = selectedStyles.toggle(item.title) }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private data class StyleCard(
    val title: String,
    val imageRes: Int,
    val barColor: Color
)

@Composable
private fun StyleCardItem(
    item: StyleCard,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(1.dp, BorderSoft)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(20.dp)
                            .background(Color(0xFF2E7D32), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = "\u2713",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(item.barColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LiveArtServicesScreen(
    onBack: () -> Unit,
    onSelectionChange: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    var liveServices by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.live_painting_bg),
            contentDescription = "Live art background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextSecondary,
                    modifier = Modifier.clickable { onBack() }
                )

                Text(
                    text = "Step 5 of 11",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Text(
                    text = "Offer Live Art\nServices?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        lineHeight = 30.sp
                    ),
                    color = Color(0xFF2D2237),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )

                Text(
                    text = "Does your art include live painting, portraits, or\ncreative sessions for events?",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                    color = Color(0xFF4A3D4D),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.84f), RoundedCornerShape(22.dp))
                        .border(1.dp, BorderSoft, RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Services",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = BodoniModa,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF2D2237),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .background(BurntOrange, RoundedCornerShape(50))
                            .border(1.dp, BurntOrange, RoundedCornerShape(50))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (liveServices) Color.White else Color.Transparent)
                                .clickable {
                                    liveServices = true
                                    onSelectionChange(true)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Yes",
                                color = if (liveServices) BurntOrange else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (liveServices) Color.Transparent else Color.White)
                                .clickable {
                                    liveServices = false
                                    onSelectionChange(false)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No",
                                color = if (liveServices) Color.White.copy(alpha = 0.8f) else BurntOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Continue", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun LiveArtModeScreen(
    onBack: () -> Unit,
    onTypeChange: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    val liveTypes = listOf("Physical Live Art", "Virtual Live Art")
    val radiusOptions = listOf("10 km", "25 km", "50 km", "100 km")
    var selectedType by remember { mutableStateOf(liveTypes.first()) }
    var selectedCities by remember { mutableStateOf(setOf("Pune", "Mumbai")) }
    var selectedRadius by remember { mutableStateOf(radiusOptions.first()) }
    val isPhysical = selectedType == liveTypes.first()
    val canContinue = if (isPhysical) selectedCities.isNotEmpty() && selectedRadius.isNotBlank() else true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 6 of 11",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "How do you offer live art?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 30.sp,
                    lineHeight = 36.sp
                ),
                color = Color(0xFF2D2237)
            )

            LiveArtOptionCard(
                title = "Physical Live Art",
                subtitle = "(In-person)",
                imageRes = R.drawable.live_icon,
                slotWidth = 168.dp,
                slotHeight = 124.dp,
                imageWidth = 168.dp,
                imageHeight = 124.dp,
                imageContentScale = ContentScale.Crop,
                fillImageSlot = true,
                imageOffsetX = (-6).dp,
                cardHeight = 156.dp,
                selected = isPhysical,
                onClick = {
                    selectedType = liveTypes.first()
                    onTypeChange(true)
                }
            )

            LiveArtOptionCard(
                title = "Virtual Live Art",
                subtitle = null,
                imageRes = R.drawable.virtual_icon,
                slotWidth = 168.dp,
                slotHeight = 136.dp,
                imageWidth = 276.dp,
                imageHeight = 180.dp,
                imageContentScale = ContentScale.Fit,
                fillImageSlot = false,
                imageAlignment = Alignment.CenterStart,
                imageOffsetX = (-18).dp,
                titleFontSize = 17.sp,
                titleMaxLines = 1,
                cardHeight = 156.dp,
                selected = !isPhysical,
                onClick = {
                    selectedType = liveTypes.last()
                    onTypeChange(false)
                }
            )

            Text(
                text = "Cities Available",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = BodoniModa,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF4A3340)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Pune", "Mumbai").forEach { city ->
                    val selected = selectedCities.contains(city)
                    Box(
                        modifier = Modifier
                            .background(
                                if (isPhysical && selected) BurntOrange.copy(alpha = 0.14f) else Color.White,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (!isPhysical) BorderSoft.copy(alpha = 0.5f) else if (selected) BurntOrange else BorderSoft,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                if (isPhysical) {
                                    selectedCities = if (selected) selectedCities - city else selectedCities + city
                                }
                            }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = city,
                            color = if (isPhysical) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            Text(
                text = "Travel Radius",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = BodoniModa,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF4A3340)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                radiusOptions.forEach { radius ->
                    val selected = selectedRadius == radius
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isPhysical && selected) BurntOrange else Color.White,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (!isPhysical) BorderSoft.copy(alpha = 0.5f) else if (selected) BurntOrange else BorderSoft,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                if (isPhysical) selectedRadius = radius
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = radius,
                            color = if (isPhysical && selected) Color.White else if (isPhysical) TextPrimary else TextSecondary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onNext,
                enabled = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.75f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LiveArtServiceTypesScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val serviceOptions = listOf(
        LiveServiceOption("Wedding Painting", R.drawable.live_service_wedding),
        LiveServiceOption("Birthday Portrait", R.drawable.live_service_birthday_portrait),
        LiveServiceOption("Corporate Event Mural", R.drawable.live_service_corporate_event),
        LiveServiceOption("Other", R.drawable.live_service_other)
    )
    var selectedService by remember { mutableStateOf<String?>(null) }
    val canContinue = selectedService != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 7 of 11",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Select Your Live Services",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                    lineHeight = 32.sp
                ),
                color = Color(0xFF2D2237)
            )

            Spacer(modifier = Modifier.height(2.dp))

            serviceOptions.forEach { option ->
                LiveServiceTypeCard(
                    title = option.title,
                    imageRes = option.imageRes,
                    slotWidth = 88.dp,
                    slotHeight = 88.dp,
                    imageWidth = 80.dp,
                    imageHeight = 80.dp,
                    imageContentScale = ContentScale.Fit,
                    fillImageSlot = false,
                    cardHeight = 102.dp,
                    selected = selectedService == option.title,
                    onClick = { selectedService = option.title }
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNext,
                enabled = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.75f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LiveArtAvailableDatesScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH) }
    val chargeOptions = listOf("\u20B95,000", "\u20B910,000", "\u20B920,000", "Custom Price")
    var displayedMonth by remember { mutableStateOf(YearMonth.of(2024, 4)) }
    var selectedDates by remember {
        mutableStateOf(
            setOf(
                LocalDate.of(2024, 4, 22),
                LocalDate.of(2024, 4, 23),
                LocalDate.of(2024, 4, 24),
                LocalDate.of(2024, 4, 29)
            )
        )
    }
    var selectedCharge by remember { mutableStateOf(chargeOptions.first()) }
    val canSave = selectedDates.isNotEmpty() && selectedCharge.isNotBlank()
    val monthDates = remember(displayedMonth) { buildCalendarDates(displayedMonth) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 8 of 11",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Available Dates",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 34.sp,
                    lineHeight = 42.sp
                ),
                color = Color(0xFF4A3340)
            )

            Text(
                text = "Select your available dates for live art booking.\nMark days you can offer live painting services.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 32.sp),
                color = TextSecondary
            )

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BorderSoft),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous month",
                            tint = Color(0xFFC8A89A),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { displayedMonth = displayedMonth.minusMonths(1) }
                        )
                        Text(
                            text = displayedMonth.format(monthFormatter),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = BodoniModa,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF4A3340)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next month",
                            tint = Color(0xFFC8A89A),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { displayedMonth = displayedMonth.plusMonths(1) }
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderSoft.copy(alpha = 0.8f))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        monthDates.chunked(7).forEach { week ->
                            CalendarRow(
                                dates = week,
                                selectedDates = selectedDates,
                                onDateClick = { date ->
                                    selectedDates = if (selectedDates.contains(date)) {
                                        selectedDates - date
                                    } else {
                                        selectedDates + date
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BorderSoft),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Minimum charge for live event booking",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        chargeOptions.forEach { option ->
                            val selected = selectedCharge == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (selected) BurntOrange else Color(0xFFFBF6F1),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedCharge = option }
                                    .padding(vertical = 14.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    color = if (selected) Color.White else TextSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.75f)
                )
            ) {
                Text(text = "Save", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LiveArtOptionCard(
    title: String,
    subtitle: String?,
    imageRes: Int,
    slotWidth: androidx.compose.ui.unit.Dp = 136.dp,
    slotHeight: androidx.compose.ui.unit.Dp = 112.dp,
    imageWidth: androidx.compose.ui.unit.Dp = 112.dp,
    imageHeight: androidx.compose.ui.unit.Dp = 112.dp,
    imageContentScale: ContentScale = ContentScale.Fit,
    fillImageSlot: Boolean = false,
    imageAlignment: Alignment = Alignment.Center,
    imageOffsetX: androidx.compose.ui.unit.Dp = 0.dp,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    titleMaxLines: Int = 2,
    cardHeight: androidx.compose.ui.unit.Dp = 94.dp,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .background(
                if (selected) Color(0xFFFFF6EF) else Color.White,
                RoundedCornerShape(24.dp)
            )
            .border(
                1.dp,
                if (selected) BurntOrange.copy(alpha = 0.35f) else BorderSoft,
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = slotWidth, height = slotHeight)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = imageAlignment
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = imageContentScale,
                modifier = if (fillImageSlot) {
                    Modifier
                        .fillMaxSize()
                        .offset(x = imageOffsetX)
                } else {
                    Modifier
                        .size(width = imageWidth, height = imageHeight)
                        .offset(x = imageOffsetX)
                }
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = BodoniModa,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = titleFontSize,
                    lineHeight = 22.sp
                ),
                color = Color(0xFF4A3340),
                maxLines = titleMaxLines
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = BodoniModa),
                    color = Color(0xFF5E4A54),
                    maxLines = 1
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (selected) BurntOrange else Color.Transparent,
                    RoundedCornerShape(50)
                )
                .border(1.dp, if (selected) BurntOrange else BorderSoft, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text(
                    text = "\u2713",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LiveServiceTypeCard(
    title: String,
    imageRes: Int,
    slotWidth: androidx.compose.ui.unit.Dp = 140.dp,
    slotHeight: androidx.compose.ui.unit.Dp = 96.dp,
    imageWidth: androidx.compose.ui.unit.Dp = 140.dp,
    imageHeight: androidx.compose.ui.unit.Dp = 96.dp,
    imageContentScale: ContentScale = ContentScale.Fit,
    fillImageSlot: Boolean = false,
    cardHeight: androidx.compose.ui.unit.Dp = 96.dp,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .background(
                if (selected) Color(0xFFFFF3EA) else Color.White,
                RoundedCornerShape(26.dp)
            )
            .border(
                1.2.dp,
                if (selected) BurntOrange else BorderSoft,
                RoundedCornerShape(26.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = slotWidth, height = slotHeight)
                .clip(RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = imageContentScale,
                modifier = if (fillImageSlot) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.size(width = imageWidth, height = imageHeight)
                }
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                ),
                color = Color(0xFF2F2F2F),
                maxLines = 2
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(if (selected) BurntOrange else Color.Transparent, RoundedCornerShape(50))
                .border(1.dp, if (selected) BurntOrange else Color(0xFFD5D5D5), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.White, RoundedCornerShape(50))
                )
            }
        }
    }
}

@Composable
private fun CalendarRow(
    dates: List<LocalDate?>,
    selectedDates: Set<LocalDate>,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dates.forEach { date ->
            val isSelected = date != null && selectedDates.contains(date)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(
                        if (isSelected) BurntOrange.copy(alpha = 0.2f) else Color(0xFFFAF4EF).copy(alpha = if (date != null) 0.55f else 0f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = date != null) {
                        if (date != null) onDateClick(date)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (date != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isSelected) BurntOrange else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text(
                                text = "\u2022",
                                color = BurntOrange,
                                fontSize = 18.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildCalendarDates(month: YearMonth): List<LocalDate?> {
    val leadingEmpty = month.atDay(1).dayOfWeek.value % 7
    val totalDays = month.lengthOfMonth()
    val cells = MutableList<LocalDate?>(leadingEmpty) { null }
    for (day in 1..totalDays) {
        cells += month.atDay(day)
    }
    while (cells.size % 7 != 0) {
        cells += null
    }
    return cells
}

private data class LiveServiceOption(
    val title: String,
    val imageRes: Int
)

@Composable
fun PriceRangeScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val minPrice = 50f
    val maxPrice = 50000f
    val logBase = ln(maxPrice / minPrice)
    fun priceToSlider(price: Float): Float = (ln(price / minPrice) / logBase).coerceIn(0f, 1f)
    fun sliderToPrice(slider: Float): Float = (minPrice * exp(logBase * slider)).coerceIn(minPrice, maxPrice)

    var priceValue by remember { mutableStateOf(2500f) }
    var sliderPosition by remember { mutableStateOf(priceToSlider(priceValue)) }
    var priceText by remember { mutableStateOf("") }
    val canContinuePrice = priceText.isNotBlank()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 5 of 10",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Where do your prices usually fall?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2F7D61), RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = formatRupee(priceValue),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    priceValue = sliderToPrice(it)
                    priceText = priceValue.toInt().toString()
                },
                valueRange = 0f..1f,
                steps = 0,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = Color(0xFFE9D6CB),
                    activeTrackColor = BurntOrange,
                    inactiveTrackColor = Color(0xFF2D2C3A)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    val labels = listOf("\u20B950", "\u20B9500", "\u20B95,000", "\u20B950,000+")
                    val anchors = listOf(0f, 1f / 3f, 2f / 3f, 1f)
                    val slotWidth = 72.dp
                    labels.forEachIndexed { index, label ->
                        Box(
                            modifier = Modifier
                                .width(slotWidth)
                                .offset(x = (maxWidth * anchors[index]) - (slotWidth / 2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderSoft, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = priceText,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                            priceText = digits
                            if (digits.isEmpty()) {
                                return@BasicTextField
                            }
                            val numeric = digits.toInt()
                            val clamped = numeric.coerceIn(minPrice.toInt(), maxPrice.toInt())
                            priceValue = clamped.toFloat()
                            sliderPosition = priceToSlider(priceValue)
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        singleLine = true,
                        visualTransformation = RupeeVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        decorationBox = { innerTextField ->
                            if (priceText.isEmpty()) {
                                Text(
                                    text = "Enter price",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }

            Text(
                text = "We will show your work only to buyers comfortable in this range.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinuePrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CustomizationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val yesNo = listOf("Yes", "No")
    val timeOptions = listOf("< 2 days", "2-5 days", "5-10 days")
    var customization by remember { mutableStateOf<String?>(null) }
    var avgTime by remember { mutableStateOf<String?>(null) }
    val canSelectAvgTime = customization == "Yes"
    val canContinueCustomization = customization != null && (customization == "No" || avgTime != null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 6 of 10",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Do you offer customization?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PillOption(
                    label = yesNo[0],
                    selected = customization == yesNo[0],
                    onClick = { customization = yesNo[0] },
                    modifier = Modifier.weight(1f),
                    cornerRadius = 10.dp,
                    height = 36.dp,
                    selectedContainerColor = Color(0xFF6E7B64),
                    unselectedContainerColor = Color(0xFFF3E9DD),
                    selectedBorderColor = BorderSoft,
                    unselectedBorderColor = BorderSoft,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                PillOption(
                    label = yesNo[1],
                    selected = customization == yesNo[1],
                    onClick = {
                        customization = yesNo[1]
                        avgTime = null
                    },
                    modifier = Modifier.weight(1f),
                    cornerRadius = 10.dp,
                    height = 36.dp,
                    selectedContainerColor = Color(0xFF6E7B64),
                    unselectedContainerColor = Color(0xFFF3E9DD),
                    selectedBorderColor = BorderSoft,
                    unselectedBorderColor = BorderSoft,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
            }

            Text(
                text = "Average time to create?",
                style = MaterialTheme.typography.titleMedium,
                color = if (canSelectAvgTime) TextPrimary else TextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                timeOptions.forEach { option ->
                    PillOption(
                        label = option,
                        selected = avgTime == option,
                        onClick = {
                            if (canSelectAvgTime) avgTime = option
                        },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = if (canSelectAvgTime) Color(0xFF6E7B64) else Color(0xFFD6D2CD),
                        unselectedContainerColor = if (canSelectAvgTime) Color(0xFFF3E9DD) else Color(0xFFF1EEEA),
                        selectedBorderColor = if (canSelectAvgTime) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        unselectedBorderColor = if (canSelectAvgTime) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (canSelectAvgTime) TextPrimary else TextSecondary
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color(0xFFF2E8DE), RoundedCornerShape(10.dp))
                            .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueCustomization,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CapacityScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val bulkOrderOptions = listOf("Yes", "No", "Sometimes")
    val capacityOptions = listOf("< 10", "10-25", "25-50", "50+")
    val timeOptions = listOf("5-10 days", "10-20 days", "20-30 days")
    var selectedBulkOrder by remember { mutableStateOf<String?>(null) }
    var selectedCapacity by remember { mutableStateOf<String?>(null) }
    var avgTime by remember { mutableStateOf<String?>(null) }
    val areCapacityFieldsEnabled = selectedBulkOrder != null && selectedBulkOrder != "No"
    val canContinueCapacity = when (selectedBulkOrder) {
        null -> false
        "No" -> true
        else -> selectedCapacity != null && avgTime != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 7 of 10",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Do you take bulk orders?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                bulkOrderOptions.forEach { option ->
                    PillOption(
                        label = option,
                        selected = selectedBulkOrder == option,
                        onClick = {
                            selectedBulkOrder = option
                            if (option == "No") {
                                selectedCapacity = null
                                avgTime = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = Color(0xFF6E7B64),
                        unselectedContainerColor = Color(0xFFF3E9DD),
                        selectedBorderColor = BorderSoft,
                        unselectedBorderColor = BorderSoft,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Text(
                text = "Monthly order capacity?",
                style = MaterialTheme.typography.titleMedium,
                color = if (areCapacityFieldsEnabled) TextPrimary else TextSecondary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                capacityOptions.forEach { option ->
                    PillOption(
                        label = option,
                        selected = selectedCapacity == option,
                        onClick = {
                            if (areCapacityFieldsEnabled) selectedCapacity = option
                        },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = if (areCapacityFieldsEnabled) Color(0xFF6E7B64) else Color(0xFFD6D2CD),
                        unselectedContainerColor = if (areCapacityFieldsEnabled) Color(0xFFF3E9DD) else Color(0xFFF1EEEA),
                        selectedBorderColor = if (areCapacityFieldsEnabled) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        unselectedBorderColor = if (areCapacityFieldsEnabled) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (areCapacityFieldsEnabled) TextPrimary else TextSecondary
                        )
                    )
                }
            }

            Text(
                text = "Average time to create?",
                style = MaterialTheme.typography.titleMedium,
                color = if (areCapacityFieldsEnabled) TextPrimary else TextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                timeOptions.forEach { option ->
                    PillOption(
                        label = option,
                        selected = avgTime == option,
                        onClick = {
                            if (areCapacityFieldsEnabled) avgTime = option
                        },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = if (areCapacityFieldsEnabled) Color(0xFF6E7B64) else Color(0xFFD6D2CD),
                        unselectedContainerColor = if (areCapacityFieldsEnabled) Color(0xFFF3E9DD) else Color(0xFFF1EEEA),
                        selectedBorderColor = if (areCapacityFieldsEnabled) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        unselectedBorderColor = if (areCapacityFieldsEnabled) BorderSoft else BorderSoft.copy(alpha = 0.6f),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (areCapacityFieldsEnabled) TextPrimary else TextSecondary
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color(0xFFF2E8DE), RoundedCornerShape(10.dp))
                            .border(1.dp, BorderSoft, RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueCapacity,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MaterialScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val materials = listOf("Resin", "Beads", "Wool", "Clay", "Wood", "Acrylic")
    var selectedMaterials by remember { mutableStateOf(emptySet<String>()) }
    var otherMaterial by remember { mutableStateOf("") }
    val canContinueMaterial = selectedMaterials.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 4 of 10",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "What material you work with ?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                materials.take(3).forEach { option ->
                    PillOption(
                        label = option,
                        selected = selectedMaterials.contains(option),
                        onClick = { selectedMaterials = selectedMaterials.toggle(option) },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = Color(0xFF6E7B64),
                        unselectedContainerColor = Color(0xFFF3E9DD),
                        selectedBorderColor = BorderSoft,
                        unselectedBorderColor = BorderSoft,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                materials.drop(3).take(3).forEach { option ->
                    PillOption(
                        label = option,
                        selected = selectedMaterials.contains(option),
                        onClick = { selectedMaterials = selectedMaterials.toggle(option) },
                        modifier = Modifier.weight(1f),
                        cornerRadius = 10.dp,
                        height = 36.dp,
                        selectedContainerColor = Color(0xFF6E7B64),
                        unselectedContainerColor = Color(0xFFF3E9DD),
                        selectedBorderColor = BorderSoft,
                        unselectedBorderColor = BorderSoft,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                PillOption(
                    label = "Other",
                    selected = selectedMaterials.contains("Other"),
                    onClick = { selectedMaterials = selectedMaterials.toggle("Other") },
                    modifier = Modifier.weight(1f),
                    cornerRadius = 10.dp,
                    height = 36.dp,
                    selectedContainerColor = Color(0xFF6E7B64),
                    unselectedContainerColor = Color(0xFFF3E9DD),
                    selectedBorderColor = BorderSoft,
                    unselectedBorderColor = BorderSoft,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            if (selectedMaterials.contains("Other")) {
                InputField(
                    value = otherMaterial,
                    onValueChange = { otherMaterial = it },
                    label = " ",
                    placeholder = "Type your material"
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueMaterial,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DeliveryScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val speeds = listOf("Same day", "1-2 days", "3-5 days")
    var city by remember { mutableStateOf("Jaipur") }
    var sameCityDelivery by remember { mutableStateOf<String?>(null) }
    var selectedSpeed by remember { mutableStateOf<String?>(null) }
    val canContinueDelivery = sameCityDelivery != null && selectedSpeed != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "How fast can you deliver?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            InputField(
                value = city,
                onValueChange = { city = it },
                label = " ",
                placeholder = "Your City"
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Same-city delivery:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                PillOption(
                    label = "Yes",
                    selected = sameCityDelivery == "Yes",
                    onClick = { sameCityDelivery = "Yes" },
                    modifier = Modifier.weight(0.5f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PillOption(
                    label = "No",
                    selected = sameCityDelivery == "No",
                    onClick = { sameCityDelivery = "No" },
                    modifier = Modifier.weight(0.5f)
                )
            }

            Text(
                text = "Fastest delivery:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                speeds.forEach { option ->
                    PillOption(
                        label = option,
                        selected = selectedSpeed == option,
                        onClick = { selectedSpeed = option },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                enabled = canContinueDelivery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White,
                    disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PillOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 50.dp,
    height: androidx.compose.ui.unit.Dp = 28.dp,
    selectedContainerColor: Color = BurntOrange,
    unselectedContainerColor: Color = Color(0xFFF3E9DD),
    selectedBorderColor: Color = BurntOrange,
    unselectedBorderColor: Color = BorderSoft,
    textStyle: androidx.compose.ui.text.TextStyle? = null
) {
    val background = if (selected) selectedContainerColor else unselectedContainerColor
    val border = if (selected) selectedBorderColor else unselectedBorderColor
    val textColor = if (selected) Color.White else TextPrimary
    val resolvedTextStyle =
        textStyle ?: MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
    Box(
        modifier = modifier
            .height(height)
            .background(background, RoundedCornerShape(cornerRadius))
            .border(1.dp, border, RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = resolvedTextStyle,
            color = textColor
        )
    }
}

@Composable
fun UploadPhotosScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Upload photos of your original work",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(3) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        repeat(3) { col ->
                            val index = row * 3 + col
                            PhotoPlaceholder(
                                isPrimary = index == 0,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Clear photos build buyer trust",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ArtistStoryScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var story by remember {
        mutableStateOf(
            "I grew up watching my mother weave stories into textiles, and I bring that tradition into every piece I make."
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Tell buyers why you started creating this art",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                color = TextPrimary
            )

            InputField(
                value = story,
                onValueChange = { story = it },
                label = " ",
                placeholder = "2-3 lines is perfect",
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                singleLine = false,
                minLines = 3
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AIProcessingScreen(
    onFinished: () -> Unit = {}
) {
    val warmBeige = Color(0xFFF6F1EA)
    val terracotta = Color(0xFFC96A3D)
    val deepNavy = Color(0xFF1F2A44)

    val infiniteTransition = rememberInfiniteTransition()
    val loaderScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var showBullets by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showBullets = true
        delay(3000)
        onFinished()
    }

    val bulletLines = listOf(
        "Understanding your art style...",
        "Finding ideal buyers...",
        "Positioning your pricing..."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(warmBeige)
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preparing your profile...",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = deepNavy
        )

        Spacer(modifier = Modifier.height(34.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(loaderScale),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize().border(width = 3.dp, color = terracotta, shape = CircleShape))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bulletLines.forEachIndexed { index, line ->
                val alpha by animateFloatAsState(
                    targetValue = if (showBullets) 1f else 0f,
                    animationSpec = tween(durationMillis = 700, delayMillis = index * 300)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(terracotta.copy(alpha = alpha), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = line,
                        fontSize = 14.sp,
                        color = deepNavy.copy(alpha = 0.75f * alpha)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Almost ready. This takes just a few seconds.",
            fontSize = 12.sp,
            color = deepNavy.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val sparkPoints = listOf(
        Pair((-78).dp, (-18).dp),
        Pair((-60).dp, 42.dp),
        Pair((-18).dp, (-76).dp),
        Pair(28.dp, (-68).dp),
        Pair(66.dp, (-24).dp),
        Pair(74.dp, 22.dp),
        Pair(22.dp, 68.dp),
        Pair((-34).dp, 72.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier.clickable { onBack() }
            )

            Text(
                text = "Step 11 of 11",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF253F78),
                                    DeepBlue,
                                    Color(0xFF1E315F)
                                )
                            )
                        )
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Creating Your Profile...",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(168.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFFDCC9FF).copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(84.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(144.dp)
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFFF7EFFF).copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(72.dp)
                                )
                        )

                        sparkPoints.forEachIndexed { index, point ->
                            Box(
                                modifier = Modifier
                                    .offset(x = point.first, y = point.second)
                                    .size(if (index % 2 == 0) 6.dp else 4.dp)
                                    .background(
                                        color = Color(0xFFFFF3D8).copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "• Analyzing your style...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                        Text(
                            text = "• Detecting buyer segments...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                        Text(
                            text = "• Optimizing pricing...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .background(
                                color = Color(0xFFF4F0EA),
                                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Almost there. Your profile will be buyer-ready in seconds.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF5A5D66),
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurntOrange,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Continue")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SuccessScreen(
    onBack: () -> Unit,
    onGoToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { onBack() }
            )
            Text(
                text = "You're live!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 26.sp
                ),
                color = TextPrimary
            )
            Text(
                text = "You'll now be matched with buyers who love your style.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "AI-Verified Handmade Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onGoToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepBlue,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Go to Dashboard")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PhotoPlaceholder(
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .border(1.dp, if (isPrimary) BurntOrange else BorderSoft, RoundedCornerShape(12.dp))
                .background(Color(0xFFF1ECE6), RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "+", style = MaterialTheme.typography.titleLarge, color = TextSecondary)
        }
        if (isPrimary) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Primary",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Preview(name = "Welcome", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewWelcomeScreen() {
    ArtistOnboardTheme {
        WelcomeScreen(onGetStarted = {})
    }
}

@Preview(name = "Name & City", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewNameCityScreen() {
    ArtistOnboardTheme {
        NameCityScreen(onBack = {}, onContinue = {})
    }
}

@Preview(name = "Category", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewCategoryScreen() {
    ArtistOnboardTheme {
        CategoryScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Style", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewStyleScreen() {
    ArtistOnboardTheme {
        StyleScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Price Range", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewPriceRangeScreen() {
    ArtistOnboardTheme {
        PriceRangeScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Customization", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewCustomizationScreen() {
    ArtistOnboardTheme {
        CustomizationScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Materials", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewMaterialScreen() {
    ArtistOnboardTheme {
        MaterialScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Capacity", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewCapacityScreen() {
    ArtistOnboardTheme {
        CapacityScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Delivery", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewDeliveryScreen() {
    ArtistOnboardTheme {
        DeliveryScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Upload Photos", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewUploadPhotosScreen() {
    ArtistOnboardTheme {
        UploadPhotosScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Artist Story", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewArtistStoryScreen() {
    ArtistOnboardTheme {
        ArtistStoryScreen(onBack = {}, onNext = {})
    }
}

@Preview(name = "Success", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewSuccessScreen() {
    ArtistOnboardTheme {
        SuccessScreen(onBack = {}, onGoToDashboard = {})
    }
}

@Preview(name = "AI Processing", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewAIProcessingScreen() {
    ArtistOnboardTheme {
        AIProcessingScreen()
    }
}

@Preview(name = "Profile Setup", showBackground = true, device = "spec:width=390dp,height=844dp")
@Composable
private fun PreviewProfileSetupScreen() {
    ArtistOnboardTheme {
        ProfileSetupScreen(onBack = {}, onNext = {})
    }
}


