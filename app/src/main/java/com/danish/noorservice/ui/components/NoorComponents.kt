package com.danish.noorservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// STEP PROGRESS BAR
//
// Fix #1 & #4 — on a blue gradient header:
//   current step  → solid WHITE   (was showing blue-on-blue, invisible)
//   completed     → NoorGreen
//   future        → White @ 28 % opacity  (clearly inactive)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StepProgressBar(
    totalSteps: Int,
    currentStep: Int,       // 1-based
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(totalSteps) { index ->
            val stepNum = index + 1
            val color = when {
                stepNum < currentStep  -> NoorGreen                        // done  → green
                stepNum == currentStep -> Color.White                       // active → solid white
                else                   -> Color.White.copy(alpha = 0.28f)  // future → faint white
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN HEADER  (gradient)
//
// Fix #2 & #5 — back arrow was clipped behind the system status bar.
// Solution: apply .statusBarsPadding() on the outer Box so all content
// (including the back arrow) is always below the status-bar height on every
// device / notch size.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoorScreenHeader(
    title: String,
    subtitle: String,
    currentStep: Int,
    totalSteps: Int,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(colors = listOf(NoorBlue, NoorBlueDark)))
            .statusBarsPadding()                               // ← clears status-bar area
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
    ) {
        Column {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            StepProgressBar(
                totalSteps  = totalSteps,
                currentStep = currentStep,
                modifier    = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text          = title,
                color         = Color.White,
                fontSize      = 22.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = subtitle,
                color    = Color.White.copy(alpha = 0.78f),
                fontSize = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRIMARY BUTTON
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoorPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = NoorBlue,
            disabledContainerColor = NoorBorder
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text          = text,
            fontSize      = 15.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = Color.White,
            letterSpacing = 0.3.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OUTLINED TEXT FIELD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value            = value,
            onValueChange    = onValueChange,
            label            = { Text(label, fontSize = 13.sp) },
            placeholder      = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = NoorTextHint, fontSize = 13.sp) }
            } else null,
            modifier         = Modifier.fillMaxWidth(),
            singleLine       = singleLine,
            maxLines         = maxLines,
            readOnly         = readOnly,
            enabled          = enabled,
            isError          = isError,
            keyboardOptions  = keyboardOptions,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon     = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector        = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint               = NoorTextHint
                        )
                    }
                }
            } else trailingIcon,
            shape  = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = NoorBlue,
                unfocusedBorderColor = NoorBorder,
                focusedLabelColor    = NoorBlue,
                unfocusedLabelColor  = NoorTextHint,
                cursorColor          = NoorBlue,
                errorBorderColor     = NoorRed,
                errorLabelColor      = NoorRed
            )
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text     = errorMessage,
                color    = NoorRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoorSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content  = content
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SELECTABLE CHIP
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NoorSelectableChip(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor   = if (selected) NoorBlueLight else NoorSurface
    val textColor = if (selected) NoorBlue else NoorTextSecondary
    val border    = if (selected) NoorBlue else NoorBorder

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(1.5.dp, border, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(icon, fontSize = 14.sp)
        Text(
            text       = label,
            color      = textColor,
            fontSize   = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (selected) {
            Spacer(Modifier.width(2.dp))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint     = NoorBlue,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INFO ROW  (summary cards)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = NoorTextHint,    fontSize = 12.sp)
        Text(value, color = NoorTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
}