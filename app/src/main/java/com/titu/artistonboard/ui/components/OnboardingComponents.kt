package com.titu.artistonboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.titu.artistonboard.ui.theme.BorderSoft
import com.titu.artistonboard.ui.theme.BurntOrange
import com.titu.artistonboard.ui.theme.CardWhite
import com.titu.artistonboard.ui.theme.TextPrimary
import com.titu.artistonboard.ui.theme.TextSecondary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val background = when {
        !enabled -> BurntOrange.copy(alpha = 0.4f)
        pressed -> Color(0xFFB44F33)
        else -> BurntOrange
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = Color.White,
            disabledContainerColor = BurntOrange.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = MaterialTheme.typography.titleSmall.letterSpacing
            )
        )
    }
}

@Composable
fun OptionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    imageContentDescription: String? = null
) {
    val border = if (selected) BorderStroke(1.dp, BurntOrange) else BorderStroke(1.dp, BorderSoft)
    val tint = if (selected) BurntOrange.copy(alpha = 0.08f) else CardWhite
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = border,
        colors = CardDefaults.cardColors(containerColor = tint),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (image != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF1ECE6), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = image,
                        contentDescription = imageContentDescription,
                        modifier = Modifier.size(72.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            singleLine = singleLine,
            minLines = minLines,
            isError = error != null,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BurntOrange,
                unfocusedBorderColor = BorderSoft,
                focusedLabelColor = BurntOrange,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = BurntOrange,
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                errorBorderColor = Color(0xFFD32F2F),
                errorLabelColor = Color(0xFFD32F2F),
                errorCursorColor = Color(0xFFD32F2F),
                errorContainerColor = CardWhite,
                errorTextColor = TextPrimary
            )
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = error,
                color = Color(0xFFD32F2F),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ProgressIndicator(
    step: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Step $step of $total",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        modifier = modifier
    )
}
