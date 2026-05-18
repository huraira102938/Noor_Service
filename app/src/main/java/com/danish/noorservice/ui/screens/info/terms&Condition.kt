package com.danish.noorservice.ui.screens.info


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Light Theme Colors ───────────────────────────────────────────────────────
private val BgLight       = Color(0xFFF5F6FA)
private val SurfaceWhite  = Color(0xFFFFFFFF)
private val AccentPurple  = Color(0xFF7B68EE)
private val AccentLight   = Color(0xFFEEEBFF)
private val AccentDark    = Color(0xFF5A4AD1)
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF5E5C80)
private val TextHint      = Color(0xFF9E9CBF)
private val DividerColor  = Color(0xFFE8E6F5)
private val GreenAccent   = Color(0xFF3DAA78)
private val GreenLight    = Color(0xFFEAF7F0)

// ─── Section Data ─────────────────────────────────────────────────────────────
private data class TermsSection(
    val emoji: String,
    val number: String,
    val title: String,
    val content: String,
    val bullets: List<String> = emptyList()
)

private val termsSections = listOf(
    TermsSection(
        emoji = "✅", number = "01", title = "Acceptance of Terms",
        content = "By creating an account or using Noor Service, you confirm that you are at least 18 years old and agree to these Terms & Conditions. If you do not agree, please discontinue use of the platform immediately."
    ),
    TermsSection(
        emoji = "👤", number = "02", title = "User Responsibilities",
        content = "You agree to:",
        bullets = listOf(
            "Provide accurate and up-to-date information when registering.",
            "Keep your login credentials confidential and secure.",
            "Use the platform only for lawful and legitimate purposes.",
            "Respect all users and engage professionally at all times."
        )
    ),
    TermsSection(
        emoji = "🤝", number = "03", title = "No Employment Relationship",
        content = "Noor Service acts solely as an intermediary marketplace. We do not employ workers or vendors listed on the platform. Any arrangement made through the platform is strictly between the employer and the worker/vendor. Noor Service bears no liability for the conduct of any party."
    ),
    TermsSection(
        emoji = "🚫", number = "04", title = "Prohibited Activities",
        content = "The following are strictly prohibited:",
        bullets = listOf(
            "Posting false, misleading, or fraudulent information.",
            "Harassing, threatening, or abusing other users.",
            "Bypassing the platform to circumvent admin processes.",
            "Accessing another user's account without authorization.",
            "Any activity violating Pakistani law or regulations."
        )
    ),
    TermsSection(
        emoji = "©️", number = "05", title = "Intellectual Property",
        content = "All content, branding, logos, and software associated with Noor Service are the exclusive property of Noor Service Provider (Pvt.) Ltd. You may not reproduce, modify, or distribute any materials without prior written consent."
    ),
    TermsSection(
        emoji = "⚖️", number = "06", title = "Limitation of Liability",
        content = "Noor Service shall not be liable for any indirect, incidental, or consequential damages arising from your use of the platform — including disputes between parties, service quality issues, or data loss. Our maximum liability is limited to fees paid in the preceding 30 days."
    ),
    TermsSection(
        emoji = "🔐", number = "07", title = "Privacy & Data",
        content = "We collect and process your personal data in accordance with our Privacy Policy. By using Noor Service, you consent to this processing. We are committed to protecting your information and will never sell it to third parties."
    ),
    TermsSection(
        emoji = "🔴", number = "08", title = "Account Termination",
        content = "We reserve the right to suspend or permanently terminate accounts that violate these Terms, engage in fraudulent behavior, or disrupt platform operations. You may also request deletion of your own account by contacting our admin."
    ),
    TermsSection(
        emoji = "🏛️", number = "09", title = "Governing Law",
        content = "These Terms are governed by the laws of the Islamic Republic of Pakistan. Any disputes shall be subject to the exclusive jurisdiction of the courts of Lahore, Punjab."
    ),
    TermsSection(
        emoji = "📩", number = "10", title = "Contact Us",
        content = "For any questions or concerns regarding these Terms & Conditions, please reach out to our team:",
        bullets = listOf(
            "Email: noorservicesprovider@gmail.com",
            "Phone: +92 300 925 4605"
        )
    ),
)

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(onBack: () -> Unit) {
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Hero header
            item {
                TermsHeroHeader()
            }

            // Intro notice
            item {
                RevealItem(listState = listState, index = 0) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentLight)
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("ℹ️", fontSize = 16.sp)
                            Text(
                                "By accessing or using Noor Service, you agree to be bound by these Terms. " +
                                        "Please read them carefully before using our platform.",
                                fontSize = 12.5.sp,
                                color = AccentDark,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Section cards
            itemsIndexed(termsSections) { index, section ->
                RevealItem(listState = listState, index = index + 1) {
                    TermsSectionCard(section = section)
                }
            }

            // Last updated
            item {
                RevealItem(listState = listState, index = termsSections.size + 2) {
                    Text(
                        "Last updated: May 2025",
                        fontSize = 11.sp,
                        color = TextHint,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// ─── Hero Header ──────────────────────────────────────────────────────────────
@Composable
private fun TermsHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6C5CE7), Color(0xFF7B68EE), Color(0xFF9D8FF5))
                )
            )
            .padding(horizontal = 24.dp, vertical = 36.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) { Text("📜", fontSize = 28.sp) }

            Spacer(Modifier.height(4.dp))

            Text(
                "Terms &\nConditions",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp
            )
            Text(
                "Noor Service Provider (Pvt.) Ltd.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
    }

    Spacer(Modifier.height(24.dp))
}

// ─── Section Card ─────────────────────────────────────────────────────────────
@Composable
private fun TermsSectionCard(section: TermsSection) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentLight),
                    contentAlignment = Alignment.Center
                ) { Text(section.emoji, fontSize = 18.sp) }

                Column {
                    Text(
                        "Section ${section.number}",
                        fontSize = 10.sp,
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        section.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
            Spacer(Modifier.height(12.dp))

            Text(section.content, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)

            if (section.bullets.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                section.bullets.forEach { bullet ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AccentPurple)
                        )
                        Text(bullet, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

// ─── Scroll-driven reveal composable ─────────────────────────────────────────
@Composable
private fun RevealItem(
    listState: LazyListState,
    index: Int,
    content: @Composable () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex <= index + 3) {
            revealed = true
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = (index % 4) * 60),
        label = "alpha_$index"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (revealed) 0f else 36f,
        animationSpec = tween(durationMillis = 500, delayMillis = (index % 4) * 60),
        label = "offsetY_$index"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            translationY = offsetY
        }
    ) {
        content()
    }
}