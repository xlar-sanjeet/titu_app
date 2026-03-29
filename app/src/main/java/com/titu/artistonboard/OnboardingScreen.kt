package com.titu.artistonboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.titu.artistonboard.ui.theme.AccentGold
import com.titu.artistonboard.ui.theme.CardSurface
import com.titu.artistonboard.ui.theme.DeepInk
import com.titu.artistonboard.ui.theme.Mint
import com.titu.artistonboard.ui.theme.Sand

private val CitiesByState = mapOf(
    "Andhra Pradesh" to listOf(
        "Anantapur", "Guntur", "Kakinada", "Kurnool", "Nellore",
        "Vijayawada", "Visakhapatnam"
    ),
    "Assam" to listOf("Guwahati"),
    "Bihar" to listOf("Arrah", "Bhagalpur", "Muzaffarpur", "Patna"),
    "Chhattisgarh" to listOf("Bilaspur", "Raipur"),
    "Delhi" to listOf("Delhi"),
    "Goa" to listOf("Goa"),
    "Gujarat" to listOf("Ahmedabad", "Rajkot", "Surat", "Vadodara"),
    "Haryana" to listOf("Ambala", "Faridabad", "Gurugram", "Karnal", "Panipat", "Sonipat"),
    "Jharkhand" to listOf("Bokaro", "Dhanbad", "Jamshedpur", "Ranchi"),
    "Jammu and Kashmir" to listOf("Jammu", "Srinagar"),
    "Karnataka" to listOf("Belagavi", "Bengaluru", "Davangere", "Gulbarga (Kalaburagi)", "Hubballi", "Mangalore", "Mysuru"),
    "Kerala" to listOf("Ernakulam", "Kochi", "Kollam", "Kozhikode", "Thiruvananthapuram", "Thrissur"),
    "Madhya Pradesh" to listOf("Bhopal", "Gwalior", "Indore", "Jabalpur", "Ujjain"),
    "Maharashtra" to listOf("Aurangabad", "Ichalkaranji", "Kolhapur", "Latur", "Mumbai", "Nagpur", "Nanded", "Nashik", "Pimpri-Chinchwad", "Pune", "Satara", "Solapur", "Thane", "Ulhasnagar"),
    "Odisha" to listOf("Bhubaneswar", "Cuttack", "Rourkela", "Sambalpur"),
    "Punjab" to listOf("Amritsar", "Jalandhar", "Ludhiana"),
    "Rajasthan" to listOf("Ajmer", "Bikaner", "Jaipur", "Jodhpur", "Kota", "Sikar", "Udaipur"),
    "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Erode", "Hosur", "Madurai", "Salem", "Thiruchirappalli", "Thoothukudi", "Tirunelveli", "Vellore"),
    "Telangana" to listOf("Hyderabad", "Warangal"),
    "Tripura" to listOf("Agartala"),
    "Uttar Pradesh" to listOf("Aligarh", "Allahabad (Prayagraj)", "Bareilly", "Firozabad", "Ghaziabad", "Kanpur", "Lucknow", "Meerut", "Moradabad", "Noida", "Saharanpur", "Varanasi"),
    "Uttarakhand" to listOf("Dehradun", "Haldwani"),
    "West Bengal" to listOf("Durgapur", "Howrah", "Kolkata", "Siliguri"),
    "Chandigarh" to listOf("Chandigarh"),
    "Puducherry" to listOf("Pondicherry")
)

private val IndianStates = listOf(
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
    "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
    "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
    "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
    "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
    "Uttar Pradesh", "Uttarakhand", "West Bengal",
    "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
    "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
)

private val ExperienceOptions = listOf("0-1", "1-3", "3-5", "5+")
private val SameDayOptions = listOf("Yes", "No")
private val ArtCategories = mapOf(
    "Textile & Fiber Handmade" to listOf(
        "Crochet & Knitting (flowers, toys, wearables)",
        "Handwoven Fabrics",
        "Embroidery & Thread Art",
        "Macramé (wall hangings, plant holders)"
    ),
    "Home Décor & Living" to listOf(
        "Scented / Decorative Candles",
        "Wall Décor & Hangings",
        "Wooden Handicrafts",
        "Ceramic & Clay Decor"
    ),
    "Jewelry & Accessories" to listOf(
        "Resin Jewelry",
        "Beaded / Wire Jewelry",
        "Clay & Metal Jewelry",
        "Hair Accessories & Fabric Jewelry"
    ),
    "Fashion & Utility Handmade" to listOf(
        "Handmade Bags & Pouches",
        "Leather Goods",
        "Handmade Footwear",
        "Belts, Wallets, Clutches"
    ),
    "Gifts & Personalised Items" to listOf(
        "Custom Name / Initial Gifts",
        "Occasion Gifts (birthday, anniversary)",
        "Gift Hampers",
        "Explosion Boxes & Scrapbooks"
    ),
    "Toys, Kids & Soft Crafts" to listOf(
        "Soft Toys & Amigurumi",
        "Felt Crafts",
        "Baby Accessories",
        "Learning / Sensory Toys"
    ),
    "Spiritual, Festive & Cultural" to listOf(
        "Pooja Items & Diyas",
        "Festival Decorations",
        "Traditional Handicrafts",
        "Spiritual Wall Art"
    ),
    "Natural & Wellness Handmade" to listOf(
        "Natural Soaps",
        "Herbal Skincare",
        "Bath & Body Products",
        "Aromatherapy Items"
    ),
    "Art & Stationery Handmade" to listOf(
        "Paintings & Illustrations",
        "Calligraphy & Letter Art",
        "Handmade Journals",
        "Cards & Bookmarks"
    )
)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            DeepInk,
            Color(0xFF1B1A28),
            Color(0xFF2A1E2B),
            Color(0xFF1B1A28)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Artist Onboarding",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Step ${state.step + 1} of 4",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                StepIndicator(step = state.step)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = state.step,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                            },
                            label = "step"
                        ) { step ->
                            when (step) {
                                0 -> StepBasics(state, onAction)
                                1 -> StepDetails(state, onAction)
                                2 -> StepImages(state, onAction)
                                else -> StepReview(state)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (state.message != null) {
                    MessageBanner(message = state.message)
                    LaunchedEffect(state.message) {
                        onAction(OnboardingAction.DismissMessage)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.step > 0) {
                        TextButton(onClick = { onAction(OnboardingAction.Back) }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (state.step < 3) {
                        Button(
                            onClick = { onAction(OnboardingAction.Next) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = DeepInk),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(text = "Next")
                        }
                    } else {
                        ElevatedButton(
                            onClick = { onAction(OnboardingAction.Submit) },
                            enabled = !state.isSubmitting,
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = Mint, contentColor = DeepInk),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (state.isSubmitting) "Saving..." else "Finish & Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(step: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(4) { index ->
            val isActive = index <= step
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        color = if (isActive) AccentGold else Color(0xFF3A3348),
                        shape = RoundedCornerShape(100.dp)
                    )
            )
        }
    }
}

@Composable
private fun StepBasics(state: OnboardingUiState, onAction: (OnboardingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Artist vendor details",
            subtitle = "Basic business and contact information"
        )

        ArtistTextField(
            value = state.fullName,
            onValueChange = { onAction(OnboardingAction.UpdateFullName(it)) },
            label = "Full Name",
            placeholder = "Enter full name",
            imeAction = ImeAction.Next,
            error = state.fieldErrors[ERR_FULL_NAME]
        )

        ArtistTextField(
            value = state.brandName,
            onValueChange = { onAction(OnboardingAction.UpdateBrandName(it)) },
            label = "Brand Name",
            placeholder = "Enter brand name",
            imeAction = ImeAction.Next,
            error = state.fieldErrors[ERR_BRAND]
        )

        ArtistTextField(
            value = state.pincode,
            onValueChange = { onAction(OnboardingAction.UpdatePincode(it)) },
            label = "Pincode",
            placeholder = "Enter 6-digit pincode",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            error = state.fieldErrors[ERR_PINCODE]
        )

        if (state.isPincodeLoading) {
            Text(text = "Fetching city and state...", color = Sand, style = MaterialTheme.typography.bodySmall)
        }
        if (state.pincodeLookupError != null) {
            Text(text = state.pincodeLookupError, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
        }

        ArtistDropdownField(
            value = state.state,
            onValueChange = { onAction(OnboardingAction.UpdateState(it)) },
            label = "State",
            placeholder = "Select state",
            options = IndianStates,
            searchable = true,
            error = state.fieldErrors[ERR_STATE]
        )

        val cityOptions = (CitiesByState[state.state].orEmpty() + listOf(state.city))
            .filter { it.isNotBlank() }
            .distinct()
        val cityPlaceholder = if (state.state.isBlank()) "Select state first" else "Select city"
        ArtistDropdownField(
            value = state.city,
            onValueChange = { onAction(OnboardingAction.UpdateCity(it)) },
            label = "City",
            placeholder = cityPlaceholder,
            options = cityOptions,
            searchable = true,
            enabled = state.state.isNotBlank(),
            error = state.fieldErrors[ERR_CITY]
        )

        ArtistTextField(
            value = state.whatsappNumber,
            onValueChange = { onAction(OnboardingAction.UpdateWhatsapp(it)) },
            label = "WhatsApp/Contact Number",
            placeholder = "+91 9876543210",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Phone,
            error = state.fieldErrors[ERR_WHATSAPP]
        )

        ArtistTextField(
            value = state.email,
            onValueChange = { onAction(OnboardingAction.UpdateEmail(it)) },
            label = "Email",
            placeholder = "Enter email",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            error = state.fieldErrors[ERR_EMAIL]
        )

        ArtistDropdownField(
            value = state.yearsOfExperience,
            onValueChange = { onAction(OnboardingAction.UpdateYearsExperience(it)) },
            label = "Years of Experience",
            placeholder = "Select years of experience",
            options = ExperienceOptions,
            error = state.fieldErrors[ERR_EXPERIENCE]
        )
    }
}

@Composable
private fun StepDetails(state: OnboardingUiState, onAction: (OnboardingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Products & delivery",
            subtitle = "Help us understand art and delivery timelines"
        )

        ArtistDropdownField(
            value = state.artCategory,
            onValueChange = { onAction(OnboardingAction.UpdateArtCategory(it)) },
            label = "Art Category",
            placeholder = "Select main category",
            options = ArtCategories.keys.toList(),
            error = state.fieldErrors[ERR_ART_CATEGORY]
        )

        ArtistDropdownField(
            value = state.artSubcategory,
            onValueChange = { onAction(OnboardingAction.UpdateArtSubcategory(it)) },
            label = "Art Subcategory",
            placeholder = if (state.artCategory.isBlank()) "Select category first" else "Select sub category",
            options = ArtCategories[state.artCategory].orEmpty(),
            error = state.fieldErrors[ERR_ART_SUBCATEGORY],
            enabled = state.artCategory.isNotBlank()
        )

        ArtistTextField(
            value = state.retailDeliveryTime,
            onValueChange = { onAction(OnboardingAction.UpdateRetailDelivery(it)) },
            label = "Retail Product Delivery Time (less than 20 products)",
            placeholder = "e.g., 3 days",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            error = state.fieldErrors[ERR_RETAIL_DELIVERY]
        )

        ArtistTextField(
            value = state.bulkDeliveryTime,
            onValueChange = { onAction(OnboardingAction.UpdateBulkDelivery(it)) },
            label = "Bulk Order Delivery Time (more than 20 products)",
            placeholder = "e.g., 10 days",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            error = state.fieldErrors[ERR_BULK_DELIVERY]
        )

        ArtistDropdownField(
            value = state.sameDayDelivery,
            onValueChange = { onAction(OnboardingAction.UpdateSameDayDelivery(it)) },
            label = "Same Day Delivery Available",
            placeholder = "Select yes or no",
            options = SameDayOptions,
            error = state.fieldErrors[ERR_SAME_DAY]
        )

        ArtistTextField(
            value = state.retailProductCost,
            onValueChange = { onAction(OnboardingAction.UpdateRetailCost(it)) },
            label = "Retail Product Cost",
            placeholder = "e.g., ₹200",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            error = state.fieldErrors[ERR_RETAIL_COST]
        )

        ArtistTextField(
            value = state.bulkProductCost,
            onValueChange = { onAction(OnboardingAction.UpdateBulkCost(it)) },
            label = "Bulk Product Cost",
            placeholder = "e.g., ₹150",
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            error = state.fieldErrors[ERR_BULK_COST]
        )

        ArtistTextField(
            value = state.costRange,
            onValueChange = { onAction(OnboardingAction.UpdateCostRange(it)) },
            label = "Average Cost per Piece (Range)",
            placeholder = "e.g., ₹200 - ₹500",
            imeAction = ImeAction.Done,
            error = state.fieldErrors[ERR_COST_RANGE]
        )

        InfoCard(
            icon = Icons.Default.Collections,
            title = "Pricing clarity",
            body = "Clear cost ranges help match collectors quickly."
        )
    }
}

@Composable
private fun StepImages(state: OnboardingUiState, onAction: (OnboardingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Upload artwork",
            subtitle = "Up to two images will be enhanced for showcase"
        )

        val picker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(2)
        ) { uris ->
            onAction(OnboardingAction.AddImages(uris.map { it.toString() }))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = DeepInk),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Images")
            }

            TextButton(
                onClick = { onAction(OnboardingAction.RemoveAllImages) }
            ) {
                Text(text = "Clear")
            }
        }

        if (state.imageUris.isEmpty()) {
            EmptyImageSlot()
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                state.imageUris.forEach { uri ->
                    Card(
                        modifier = Modifier.size(92.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected artwork",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        state.fieldErrors[ERR_IMAGES]?.let { error ->
            Text(text = error, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
        }

        InfoCard(
            icon = Icons.Default.Image,
            title = "Enhancement placeholder",
            body = "These images will be routed to an enhancement model in the backend."
        )
    }
}

@Composable
private fun StepReview(state: OnboardingUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Review & submit",
            subtitle = "Confirm the artist's info before saving"
        )

        SummaryRow(label = "Full name", value = state.fullName)
        SummaryRow(label = "Brand name", value = state.brandName)
        SummaryRow(label = "Pincode", value = state.pincode)
        SummaryRow(label = "City", value = state.city)
        SummaryRow(label = "State", value = state.state)
        SummaryRow(label = "WhatsApp/Contact", value = state.whatsappNumber)
        SummaryRow(label = "Email", value = state.email)
        SummaryRow(label = "Experience", value = state.yearsOfExperience)
        SummaryRow(label = "Art category", value = state.artCategory)
        SummaryRow(label = "Art subcategory", value = state.artSubcategory)
        SummaryRow(label = "Retail delivery", value = state.retailDeliveryTime)
        SummaryRow(label = "Bulk delivery", value = state.bulkDeliveryTime)
        SummaryRow(label = "Same day delivery", value = state.sameDayDelivery)
        SummaryRow(label = "Retail cost", value = state.retailProductCost)
        SummaryRow(label = "Bulk cost", value = state.bulkProductCost)
        SummaryRow(label = "Cost range", value = state.costRange)
        SummaryRow(label = "Images", value = "${state.imageUris.size} selected")

        Divider(color = Color(0xFF2C2433))

        InfoCard(
            icon = Icons.Default.AutoAwesome,
            title = "Enhancement queued",
            body = "A backend job placeholder will mark these images for enhancement."
        )

        if (state.submitted) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Mint, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Mint)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Artist saved locally", color = Mint, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Sand
        )
    }
}

@Composable
private fun ArtistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    imeAction: ImeAction,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, color = Sand, style = MaterialTheme.typography.labelMedium)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4FF))
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(text = placeholder, color = Color(0xFF4A415C)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGold,
                    unfocusedBorderColor = Color(0xFFB8AEC8),
                    focusedLabelColor = Color(0xFF2A2438),
                    unfocusedLabelColor = Color(0xFF4A415C),
                    focusedTextColor = Color(0xFF1A1424),
                    unfocusedTextColor = Color(0xFF1A1424),
                    cursorColor = AccentGold,
                    focusedContainerColor = Color(0xFFF7F4FF),
                    unfocusedContainerColor = Color(0xFFF7F4FF)
                )
            )
        }
        if (error != null) {
            Text(text = error, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ArtistDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    options: List<String>,
    searchable: Boolean = false,
    enabled: Boolean = true,
    error: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, color = Sand, style = MaterialTheme.typography.labelMedium)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4FF))
        ) {
            var expanded by remember { mutableStateOf(false) }
            var query by remember { mutableStateOf("") }
            Box(modifier = Modifier.fillMaxWidth()) {
                val selectionColor = if (value.isBlank()) Color(0xFF1A1424) else Mint
                OutlinedTextField(
                    value = value,
                    onValueChange = {},
                    placeholder = { Text(text = placeholder, color = Color(0xFF4A415C)) },
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF4A415C)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = Color(0xFFB8AEC8),
                        focusedLabelColor = Color(0xFF2A2438),
                        unfocusedLabelColor = Color(0xFF4A415C),
                        focusedTextColor = selectionColor,
                        unfocusedTextColor = selectionColor,
                        cursorColor = AccentGold,
                        focusedContainerColor = Color(0xFFF7F4FF),
                        unfocusedContainerColor = Color(0xFFF7F4FF)
                    )
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = enabled) { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded && enabled,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filtered = if (!searchable || query.isBlank()) {
                        options
                    } else {
                        options.filter { it.contains(query.trim(), ignoreCase = true) }
                    }

                    if (searchable) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text(text = "Search...", color = Color(0xFF4A415C)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = Color(0xFFB8AEC8),
                                focusedLabelColor = Color(0xFF2A2438),
                                unfocusedLabelColor = Color(0xFF4A415C),
                                focusedTextColor = Color(0xFF1A1424),
                                unfocusedTextColor = Color(0xFF1A1424),
                                cursorColor = AccentGold,
                                focusedContainerColor = Color(0xFFF7F4FF),
                                unfocusedContainerColor = Color(0xFFF7F4FF)
                            )
                        )
                    }

                    filtered.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = if (option == value) Mint else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onValueChange(option)
                                query = ""
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        if (error != null) {
            Text(text = error, color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2538))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AccentGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = DeepInk)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(text = body, style = MaterialTheme.typography.bodySmall, color = Sand)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Sand)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun EmptyImageSlot() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF3A3348), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Sand)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "No images yet", color = Sand)
    }
}

@Composable
private fun MessageBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF332A3F), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = message, color = Sand)
    }
}
