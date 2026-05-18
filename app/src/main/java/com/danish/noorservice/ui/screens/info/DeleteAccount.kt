package com.danish.noorservice.ui.screens.info

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
private val RedAccent     = Color(0xFFE53935)
private val RedLight      = Color(0xFFFFEBEB)
private val RedDark       = Color(0xFFB71C1C)
private val AmberAccent   = Color(0xFFF59E0B)
private val AmberLight    = Color(0xFFFFF8E1)
private val GreenAccent   = Color(0xFF3DAA78)
private val GreenLight    = Color(0xFFEAF7F0)

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(onBack: () -> Unit) {
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
            item { DeleteHeroHeader() }

            // Reassurance banner
            item {
                DeleteRevealItem(listState, 0) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(RedLight)
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("❤️", fontSize = 16.sp)
                            Text(
                                "We're sorry to see you go. Account deletion is permanent and cannot be undone — " +
                                        "but we'll make the process as smooth as possible for you.",
                                fontSize = 12.5.sp,
                                color = RedDark,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // Before you delete
            item {
                DeleteRevealItem(listState, 1) {
                    DeleteSectionCard(
                        emoji = "📌",
                        title = "Before You Delete",
                        bgColor = RedLight
                    ) {
                        DeleteBullet("All your profile data, proposals, and history will be permanently erased.", RedAccent)
                        DeleteBullet("Any pending arrangements or proposals will be automatically cancelled.", RedAccent)
                        DeleteBullet("This action cannot be undone — your account will not be recoverable.", RedAccent)
                        DeleteBullet("You're always welcome to create a new account in the future.", GreenAccent)
                    }
                }
            }

            // How to request
            item {
                DeleteRevealItem(listState, 2) {
                    DeleteSectionCard(
                        emoji = "📧",
                        title = "How to Request Deletion",
                        bgColor = AccentLight
                    ) {
                        Text(
                            "Account deletion is handled by our admin team. Simply send us an email and " +
                                    "we'll take care of the rest — usually within ",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                        Text(
                            "2–3 business days.",
                            fontSize = 13.sp,
                            color = AccentPurple,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "In your email, please include:",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        DeleteBullet("Your full name as registered on Noor Service.", AccentPurple)
                        DeleteBullet("Your registered phone number or email address.", AccentPurple)
                        DeleteBullet("A brief reason for deletion (optional but appreciated).", AccentPurple)
                    }
                }
            }

            // Email CTA card
            item {
                DeleteRevealItem(listState, 3) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AccentLight),
                                contentAlignment = Alignment.Center
                            ) { Text("📧", fontSize = 26.sp) }

                            Text(
                                "Send Your Request To",
                                fontSize = 11.sp,
                                color = TextHint,
                                letterSpacing = 0.5.sp,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentLight)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "noorservicesprovider@gmail.com",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentDark,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Subject: Account Deletion Request",
                                        fontSize = 11.sp,
                                        color = TextHint,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // What happens next
            item {
                DeleteRevealItem(listState, 4) {
                    DeleteSectionCard(
                        emoji = "✅",
                        title = "What Happens Next?",
                        bgColor = GreenLight
                    ) {
                        DeleteBullet("Our admin receives your request and verifies your identity.", GreenAccent)
                        DeleteBullet("Your account and all associated data are permanently removed.", GreenAccent)
                        DeleteBullet("You'll receive a confirmation email once deletion is complete.", GreenAccent)
                        DeleteBullet("No further action is required from your side.", GreenAccent)
                    }
                }
            }

            // Come back anytime
            item {
                DeleteRevealItem(listState, 5) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("💛", fontSize = 24.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "We'd Love to Have You Back",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78500A)
                                )
                                Text(
                                    "Changed your mind? That's totally okay! You can always rejoin our growing community of 20,000+ workers and employers across Pakistan.",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF9A6A20),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }

            // Need help instead
            item {
                DeleteRevealItem(listState, 6) {
                    DeleteSectionCard(
                        emoji = "🤝",
                        title = "Need Help Instead?",
                        bgColor = AccentLight
                    ) {
                        Text(
                            "If you're facing an issue that led you here, we'd love to resolve it first! Our admin team is ready to help.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentLight)
                                .padding(12.dp)
                        ) {
                            Text(
                                "noorservicesprovider@gmail.com",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentDark
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
private fun DeleteHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFC62828), Color(0xFFE53935), Color(0xFFEF5350))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = 200.dp, y = (-40).dp)
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
            ) { Text("🗑️", fontSize = 30.sp) }

            Spacer(Modifier.height(14.dp))

            Text(
                "Delete\nAccount",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "We'll handle it safely and securely",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }

    Spacer(Modifier.height(24.dp))
}

// ─── Section Card ─────────────────────────────────────────────────────────────
@Composable
private fun DeleteSectionCard(
    emoji: String,
    title: String,
    bgColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
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
private fun DeleteBullet(text: String, dotColor: Color) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(text, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
    }
}

// ─── Scroll-driven reveal ─────────────────────────────────────────────────────
@Composable
private fun DeleteRevealItem(
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
        animationSpec = tween(500, delayMillis = (index % 4) * 70),
        label = "alpha_$index"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (revealed) 0f else 32f,
        animationSpec = tween(500, delayMillis = (index % 4) * 70),
        label = "offset_$index"
    )

    Box(modifier = Modifier.graphicsLayer { this.alpha = alpha; translationY = offsetY }) {
        content()
    }
}