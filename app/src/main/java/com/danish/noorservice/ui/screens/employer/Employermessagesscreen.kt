package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

data class EmployerConversation(
    val id: String,
    val workerName: String,
    val initials: String,
    val avatarColor: Color,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0
)

data class EmployerChatMessage(
    val text: String,
    val isFromMe: Boolean,     // true = employer sent it
    val time: String
)

private val employerConversations = listOf(
    EmployerConversation("1", "Muhammad Ali", "MA", NoorBlue,    "I will be there at 8 AM Monday.",        "10:45", 2),
    EmployerConversation("2", "Ayesha Bibi",  "AB", NoorOrange,  "Yes, I'm available Wednesday mornings.", "Yesterday", 1),
    EmployerConversation("3", "Nazia Malik",  "NM", Color(0xFFE91E63), "Thank you! Looking forward to it.", "Mon", 0),
    EmployerConversation("4", "Zulfiqar Ali", "ZA", Color(0xFF009688), "Understood. I'll bring my LTV licence.", "Sun", 0),
)

private val employerMessages = mapOf(
    "1" to listOf(
        EmployerChatMessage("Hello Muhammad, we need a driver starting Monday.", true,  "10:10"),
        EmployerChatMessage("Sure! Which area are you located in?",              false, "10:12"),
        EmployerChatMessage("DHA Phase 3. Mon–Fri, 8 AM to 6 PM.",              true,  "10:15"),
        EmployerChatMessage("That works perfectly for me.",                       false, "10:30"),
        EmployerChatMessage("I will be there at 8 AM Monday.",                   false, "10:45"),
    ),
    "2" to listOf(
        EmployerChatMessage("Hi Ayesha, do you do morning shifts?",              true,  "Yesterday 9:00"),
        EmployerChatMessage("Yes, I'm available Wednesday mornings.",             false, "Yesterday 9:08"),
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Messages Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerMessagesScreen() {
    var openConversation by remember { mutableStateOf<EmployerConversation?>(null) }

    if (openConversation != null) {
        EmployerChatDetailScreen(
            conversation = openConversation!!,
            onBack       = { openConversation = null }
        )
    } else {
        EmployerMessagesListContent(
            conversations = employerConversations,
            onOpenChat    = { openConversation = it }
        )
    }
}

@Composable
private fun EmployerMessagesListContent(
    conversations: List<EmployerConversation>,
    onOpenChat: (EmployerConversation) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text("Messages", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text("Conversations with service workers", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
            }
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(conversations) { conv ->
                EmployerConversationItem(conversation = conv, onClick = { onOpenChat(conv) })
                HorizontalDivider(modifier = Modifier.padding(start = 76.dp, end = 16.dp), color = NoorDivider, thickness = 0.6.dp)
            }
        }
    }
}

@Composable
private fun EmployerConversationItem(
    conversation: EmployerConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoorSurface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(conversation.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(conversation.initials, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                conversation.workerName,
                fontSize   = 14.sp,
                fontWeight = if (conversation.unread > 0) FontWeight.Bold else FontWeight.SemiBold,
                color      = NoorTextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                conversation.lastMessage,
                fontSize   = 12.sp,
                color      = if (conversation.unread > 0) NoorTextSecondary else NoorTextHint,
                fontWeight = if (conversation.unread > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines   = 1
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(conversation.time, fontSize = 10.sp, color = NoorTextHint)
            if (conversation.unread > 0) {
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape).background(NoorOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(conversation.unread.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chat Detail
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployerChatDetailScreen(
    conversation: EmployerConversation,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages    = remember {
        mutableStateListOf<EmployerChatMessage>().also {
            it.addAll(employerMessages[conversation.id] ?: emptyList())
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(conversation.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(conversation.initials, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(conversation.workerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Service Worker", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        // Messages
        LazyColumn(
            modifier            = Modifier.weight(1f).padding(horizontal = 16.dp),
            state               = listState,
            contentPadding      = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages) { msg ->
                EmployerChatBubble(msg)
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NoorSurface)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value         = messageText,
                onValueChange = { messageText = it },
                placeholder   = { Text("Type a message…", fontSize = 13.sp, color = NoorTextHint) },
                modifier      = Modifier.weight(1f),
                shape         = RoundedCornerShape(24.dp),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = NoorBlue,
                    unfocusedBorderColor = NoorBorder
                )
            )
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (messageText.isNotBlank()) NoorBlue else NoorBorder)
                    .clickable(enabled = messageText.isNotBlank()) {
                        messages.add(EmployerChatMessage(messageText.trim(), true, "Now"))
                        messageText = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun EmployerChatBubble(message: EmployerChatMessage) {
    val bgColor   = if (message.isFromMe) NoorOrange else NoorSurface
    val textColor = if (message.isFromMe) Color.White else NoorTextPrimary
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val shape     = if (message.isFromMe)
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(shape)
                .background(bgColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(message.text, fontSize = 13.sp, color = textColor, lineHeight = 19.sp)
        }
        Spacer(Modifier.height(2.dp))
        Text(message.time, fontSize = 9.sp, color = NoorTextHint)
    }
}