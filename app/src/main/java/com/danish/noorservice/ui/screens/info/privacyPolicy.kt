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
private val GreenDark     = Color(0xFF1E7A52)

// ─── Section Data ─────────────────────────────────────────────────────────────
private data class PrivacySection(
    val emoji: String,
    val number: String,
    val title: String,
    val content: String,
    val bullets: List<String> = emptyList()
)

private val privacySections = listOf(
    PrivacySection(
        emoji = "📋", number = "01", title = "Data Collection",
        content = "We collect information you provide directly, including your name, phone number, email address, location, and profile details. We also collect usage data such as app interactions, device type, and session information to continuously improve the platform."
    ),
    PrivacySection(
        emoji = "⚙️", number = "02", title = "How We Use Your Data",
        content = "Your data helps us deliver a better experience:",
        bullets = listOf(
            "To create and manage your account.",
            "To match employers with suitable workers and vendors.",
            "To send notifications about proposals and updates.",
            "To improve platform performance and user experience."
        )
    ),
    PrivacySection(
        emoji = "💾", number = "03", title = "Data Storage",
        content = "Your data is securely stored on encrypted cloud servers. We retain your information for as long as your account is active or as needed to provide our services. You may request data deletion at any time by contacting our admin."
    ),
    PrivacySection(
        emoji = "🔄", number = "04", title = "Data Sharing",
        content = "We do not sell your personal information to third parties. We may share limited data only with:",
        bullets = listOf(
            "Service providers assisting with platform operations.",
            "Law enforcement if required by applicable law.",
            "We never share your data for advertising purposes outside Noor Service."
        )
    ),
    PrivacySection(
        emoji = "🍪", number = "05", title = "Cookies & Tracking",
        content = "We use lightweight cookies and tracking technologies to maintain your session and analyze platform usage. You can control cookie preferences through your device settings. Disabling cookies may affect some platform features."
    ),
    PrivacySection(
        emoji = "🛡️", number = "06", title = "Security",
        content = "We implement industry-standard security measures including data encryption, secure access controls, and regular security audits. However, no internet transmission is 100% secure — we encourage strong passwords and careful account management."
    ),
    PrivacySection(
        emoji = "⚖️", number = "07", title = "Your Rights",
        content = "As a Noor Service user, you have the right to:",
        bullets = listOf(
            "Access and review your personal data.",
            "Request correction of inaccurate information.",
            "Request deletion of your account and associated data.",
            "Opt out of non-essential communications at any time."
        )
    ),
    PrivacySection(
        emoji = "👶", number = "08", title = "Children's Privacy",
        content = "Noor Service is not intended for users under the age of 18. We do not knowingly collect personal information from minors. If we discover such data has been collected, it will be promptly and permanently deleted."
    ),
    PrivacySection(
        emoji = "🔔", number = "09", title = "Policy Changes",
        content = "We may update this Privacy Policy periodically. We will notify you of significant changes through in-app notifications or email. Continued use of the platform after changes constitutes your acceptance of the updated policy."
    ),
    PrivacySection(
        emoji = "📩", number = "10", title = "Contact Us",
        content = "For privacy-related questions, data requests, or concerns, please reach out to our team:",
        bullets = listOf(
            "Email: noorservicesprovider@gmail.com"
        )
    ),
)

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
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
            // Hero
            item { PrivacyHeroHeader() }

            // Trust banner
            item {
                PrivacyRevealItem(listState, 0) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GreenLight)
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🌿", fontSize = 16.sp)
                            Text(
                                "At Noor Service, protecting your personal information is our priority. " +
                                        "This policy explains what we collect and how we use it.",
                                fontSize = 12.5.sp,
                                color = GreenDark,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Section cards
            itemsIndexed(privacySections) { index, section ->
                PrivacyRevealItem(listState, index + 1) {
                    PrivacySectionCard(section)
                }
            }

            // Last updated footer
            item {
                PrivacyRevealItem(listState, privacySections.size + 2) {
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
private fun PrivacyHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF2D8A5F), Color(0xFF3DAA78), Color(0xFF52C28F))
                )
            )
    ) {
        // Decorative shape
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 200.dp, y = (-50).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) { Text("🔐", fontSize = 30.sp) }

            Spacer(Modifier.height(14.dp))

            Text(
                "Privacy\nPolicy",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Your data, your rights — we keep it safe",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }

    Spacer(Modifier.height(24.dp))
}

// ─── Section Card ─────────────────────────────────────────────────────────────
@Composable
private fun PrivacySectionCard(section: PrivacySection) {
    // Alternate icon background between green and purple tones
    val bgColor = if (section.number.toInt() % 2 == 0) AccentLight else GreenLight

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
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) { Text(section.emoji, fontSize = 18.sp) }

                Column {
                    Text(
                        "Section ${section.number}",
                        fontSize = 10.sp,
                        color = GreenAccent,
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
                                .background(GreenAccent)
                        )
                        Text(bullet, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

// ─── Scroll-driven reveal ─────────────────────────────────────────────────────
@Composable
private fun PrivacyRevealItem(
    listState: LazyListState,
    index: Int,
    content: @Composable () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex <= index + 3) revealed = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(500, delayMillis = (index % 4) * 60),
        label = "alpha_$index"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (revealed) 0f else 32f,
        animationSpec = tween(500, delayMillis = (index % 4) * 60),
        label = "offset_$index"
    )

    Box(modifier = Modifier.graphicsLayer { this.alpha = alpha; translationY = offsetY }) {
        content()
    }
}