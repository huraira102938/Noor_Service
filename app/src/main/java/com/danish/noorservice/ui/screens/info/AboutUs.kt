package com.danish.noorservice.ui.screens.info

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
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
private val OrangeAccent  = Color(0xFFF59E0B)
private val OrangeLight   = Color(0xFFFFF7E6)
private val GreenAccent   = Color(0xFF3DAA78)
private val GreenLight    = Color(0xFFEAF7F0)
private val InstaGradStart = Color(0xFFE1306C)
private val InstaGradEnd   = Color(0xFFF77737)
private val FacebookBlue   = Color(0xFF1877F2)
private val FacebookLight  = Color(0xFFE7F0FD)
private val GoogleRed      = Color(0xFFEA4335)
private val GoogleLight    = Color(0xFFFDECEB)
private val WebTeal        = Color(0xFF0D9488)
private val WebTealLight   = Color(0xFFE6F7F6)

private val WhatsAppGreen      = Color(0xFF25D366)
private val WhatsAppGreenLight = Color(0xFFE9FBF0)

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(onBack: () -> Unit) {
    val listState  = rememberLazyListState()
    val uriHandler = LocalUriHandler.current

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
            state          = listState,
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            // Hero
            item { AboutHeroHeader() }

            // Stats row
            item {
                AboutRevealItem(listState, 0) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            emoji    = "👷",
                            value    = "20,000+",
                            label    = "Workers",
                            accent   = AccentPurple,
                            bg       = AccentLight,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            emoji    = "🛠️",
                            value    = "150+",
                            label    = "Services",
                            accent   = OrangeAccent,
                            bg       = OrangeLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Who We Are
            item {
                AboutRevealItem(listState, 1) {
                    AboutSectionCard(emoji = "🌟", title = "Who We Are", bgColor = AccentLight) {
                        Text(
                            "Noor Service is Pakistan's trusted freelancer marketplace — a bridge between skilled workers, expert vendors, and employers seeking reliable talent across the country.",
                            fontSize = 13.sp, color = TextSecondary, lineHeight = 21.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Founded with a vision to digitize Pakistan's labor market, we connect thousands of employers with verified workers daily — transparently, safely, and efficiently.",
                            fontSize = 13.sp, color = TextSecondary, lineHeight = 21.sp
                        )
                    }
                }
            }

            // Mission
            item {
                AboutRevealItem(listState, 2) {
                    AboutSectionCard(emoji = "🎯", title = "Our Mission", bgColor = GreenLight) {
                        Text(
                            "To empower Pakistan's skilled workforce by creating meaningful connections between workers, vendors, and employers — making quality service accessible, transparent, and reliable across every city and region.",
                            fontSize = 13.sp, color = TextSecondary, lineHeight = 21.sp
                        )
                    }
                }
            }

            // What We Do
            item {
                AboutRevealItem(listState, 3) {
                    AboutSectionCard(emoji = "💼", title = "What We Do", bgColor = AccentLight) {
                        AboutBullet("Connect employers with verified workers and vendors.")
                        AboutBullet("Facilitate secure, transparent hiring via admin support.")
                        AboutBullet("Offer domestic, commercial, and technical services.")
                        AboutBullet("Maintain quality through thorough vetting and review.")
                    }
                }
            }

            // Coverage
            item {
                AboutRevealItem(listState, 4) {
                    AboutSectionCard(emoji = "🗺️", title = "Our Coverage", bgColor = OrangeLight) {
                        Text(
                            "We operate across Pakistan with a growing presence in major cities including Lahore, Karachi, Islamabad, Rawalpindi, Faisalabad, and Multan — and expanding rapidly to cover all provinces and regions.",
                            fontSize = 13.sp, color = TextSecondary, lineHeight = 21.sp
                        )
                    }
                }
            }

            // Why Choose
            item {
                AboutRevealItem(listState, 5) {
                    AboutSectionCard(emoji = "🏆", title = "Why Choose Noor Service?", bgColor = AccentLight) {
                        AboutBullet("Trusted by thousands of employers across Pakistan.")
                        AboutBullet("Verified profiles with real skills and experience.")
                        AboutBullet("Admin-mediated hiring for maximum safety.")
                        AboutBullet("Fast, reliable, and always at your service.")
                    }
                }
            }

            // Contact Card
            item {
                AboutRevealItem(listState, 6) {
                    Card(
                        modifier  = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                            .fillMaxWidth(),
                        shape     = RoundedCornerShape(18.dp),
                        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {

                            // Section title
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier              = Modifier.padding(bottom = 14.dp)
                            ) {
                                Box(
                                    modifier         = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GreenLight),
                                    contentAlignment = Alignment.Center
                                ) { Text("📞", fontSize = 18.sp) }
                                Text(
                                    "Get in Touch",
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = TextPrimary
                                )
                            }

                            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)

                            // ── Contact rows ──────────────────────────────────
                            ContactInfoRow(
                                emoji      = "📧",
                                label      = "Email",
                                value      = "noorservicesprovider@gmail.com",
                                valueColor = AccentPurple
                            )
                            HorizontalDivider(color = DividerColor, thickness = 0.6.dp)
                            ContactInfoRow(
                                emoji      = "📞",
                                label      = "Phone",
                                value      = "+92 300 925 4605",
                                valueColor = TextPrimary,
                                url        = "tel:+923009254605",
                                onOpen     = { uriHandler.openUri("tel:+923009254605") }
                            )
                            HorizontalDivider(color = DividerColor, thickness = 0.6.dp)
                            ContactInfoRow(
                                emoji      = "📍",
                                label      = "Location",
                                value      = "Karachi, Pakistan",
                                valueColor = TextSecondary
                            )

                            Spacer(Modifier.height(18.dp))
                            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
                            Spacer(Modifier.height(14.dp))

                            // ── Social & Web links ────────────────────────────
                            Text(
                                "Follow & Connect",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextHint,
                                letterSpacing = 0.6.sp,
                                modifier   = Modifier.padding(bottom = 12.dp)
                            )


                            SocialLinkRow(
                                emoji     = "💬",
                                label     = "WhatsApp",
                                handle    = "+92 300 925 4605",
                                bgColor   = WhatsAppGreenLight,
                                textColor = WhatsAppGreen,
                                onClick   = {
                                    uriHandler.openUri("https://wa.me/923009254605")
                                }
                            )
                            Spacer(Modifier.height(8.dp))



                            // Instagram
                            SocialLinkRow(
                                emoji      = "📸",
                                label      = "Instagram",
                                handle     = "@noorservicesprovider",
                                bgColor    = InstaGradStart.copy(alpha = 0.10f),
                                textColor  = InstaGradStart,
                                onClick    = {
                                    uriHandler.openUri("https://www.instagram.com/noorservicesprovider?igsh=cW54dzF2Ym5ldTRo")
                                }
                            )
                            Spacer(Modifier.height(8.dp))

                            // Facebook
                            SocialLinkRow(
                                emoji     = "📘",
                                label     = "Facebook",
                                handle    = "Noor Service",
                                bgColor   = FacebookLight,
                                textColor = FacebookBlue,
                                onClick   = {
                                    uriHandler.openUri("https://www.facebook.com/share/177Vg2K5U1/")
                                }
                            )
                            Spacer(Modifier.height(8.dp))

                            // Google
                            SocialLinkRow(
                                emoji     = "🔍",
                                label     = "Google",
                                handle    = "Find us on Google",
                                bgColor   = GoogleLight,
                                textColor = GoogleRed,
                                onClick   = {
                                    uriHandler.openUri("https://share.google/Qlulp9jHCpp1INnvJ")
                                }
                            )
                            Spacer(Modifier.height(8.dp))

                            // Website
                            SocialLinkRow(
                                emoji     = "🌐",
                                label     = "Website",
                                handle    = "noorservicesprovider.com.pk",
                                bgColor   = WebTealLight,
                                textColor = WebTeal,
                                onClick   = {
                                    uriHandler.openUri("https://noorservicesprovider.com.pk/")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Hero Header ──────────────────────────────────────────────────────────────
@Composable
private fun AboutHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF5A4AD1), Color(0xFF7B68EE), Color(0xFF9A8FF2))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 180.dp, y = (-40).dp)
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
            ) { Text("🌟", fontSize = 30.sp) }
            Spacer(Modifier.height(14.dp))
            Text(
                "About\nNoor Service",
                fontSize      = 28.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                lineHeight    = 34.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Connecting Pakistan's workforce",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.75f)
            )
        }
    }
    Spacer(Modifier.height(24.dp))
}

// ─── Reusable composables ─────────────────────────────────────────────────────

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    accent: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(bg),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 22.sp) }
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Text(label, fontSize = 11.sp, color = TextHint, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AboutSectionCard(
    emoji: String,
    title: String,
    bgColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp).fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(bgColor),
                    contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 18.sp) }
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun AboutBullet(text: String) {
    Row(
        modifier              = Modifier.padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(AccentPurple)
        )
        Text(text, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
    }
}

@Composable
private fun ContactInfoRow(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color,
    url: String? = null,
    onOpen: (() -> Unit)? = null
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .then(if (onOpen != null) Modifier.clickable { onOpen() } else Modifier)
            .padding(vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, color = TextHint)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
        if (onOpen != null) {
            Text("↗", fontSize = 14.sp, color = TextHint)
        }
    }
}

@Composable
private fun SocialLinkRow(
    emoji: String,
    label: String,
    handle: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize   = 11.sp,
                color      = textColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                handle,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = textColor
            )
        }
        Text("↗", fontSize = 16.sp, color = textColor.copy(alpha = 0.6f))
    }
}

// ─── Scroll-driven reveal ─────────────────────────────────────────────────────
@Composable
private fun AboutRevealItem(
    listState: LazyListState,
    index: Int,
    content: @Composable () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex <= index + 3) revealed = true
    }

    val alpha by animateFloatAsState(
        targetValue  = if (revealed) 1f else 0f,
        animationSpec = tween(500, delayMillis = (index % 4) * 70),
        label        = "alpha_$index"
    )
    val offsetY by animateFloatAsState(
        targetValue  = if (revealed) 0f else 32f,
        animationSpec = tween(500, delayMillis = (index % 4) * 70),
        label        = "offset_$index"
    )

    Box(modifier = Modifier.graphicsLayer { this.alpha = alpha; translationY = offsetY }) {
        content()
    }
}