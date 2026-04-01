package com.danish.noorservice.ui.screens.employee


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

data class Conversation(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val lastMessage: String,
    val time: String,
    val unread: Int = 0
)

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean,
    val time: String
)

private val sampleConversations = listOf(
    Conversation("1", "Farhan Ahmed", "FA", NoorBlue,       "Can you start Monday morning?",         "10:32", 2),
    Conversation("2", "Sara Khan",    "SK", NoorOrange,     "Please confirm your availability",      "Yesterday", 1),
    Conversation("3", "Asad Malik",   "AM", NoorGreen,      "Thank you, great work today!",          "Mon", 0),
    Conversation("4", "Nadia Baig",   "NB", NoorTextHint,   "We'll need you twice a week",           "Sun", 0),
    Conversation("5", "Bilal Raza",   "BR", Color(0xFF9C27B0), "What is your daily rate?",           "Sat", 0),
)

private val sampleMessages = mapOf(
    "1" to listOf(
        ChatMessage("Hello! I saw your profile on Noor Services.", false, "10:10"),
        ChatMessage("Hi! Yes, I'm available. How can I help?", true, "10:12"),
        ChatMessage("We need a full-time driver, Mon–Fri. DHA Phase 5.", false, "10:15"),
        ChatMessage("That works for me! I'm familiar with that area.", true, "10:18"),
        ChatMessage("Can you start Monday morning?", false, "10:32"),
    ),
    "2" to listOf(
        ChatMessage("Hi Muhammad Ali, I found your profile.", false, "Yesterday 9:00"),
        ChatMessage("Hello! What service do you need?", true, "Yesterday 9:05"),
        ChatMessage("Please confirm your availability for weekends.", false, "Yesterday 9:10"),
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Messages List Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployeeMessagesScreen() {
    var openConversation by remember { mutableStateOf<Conversation?>(null) }

    if (openConversation != null) {
        ChatDetailScreen(
            conversation = openConversation!!,
            onBack       = { openConversation = null }
        )
    } else {
        MessagesListContent(
            conversations  = sampleConversations,
            onOpenChat     = { openConversation = it }
        )
    }
}

@Composable
private fun MessagesListContent(
    conversations: List<Conversation>,
    onOpenChat: (Conversation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text(
                    "Messages",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Your conversations with employers",
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.72f)
                )
            }
        }

        // List
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(conversations) { conv ->
                ConversationItem(
                    conversation = conv,
                    onClick      = { onOpenChat(conv) }
                )
                HorizontalDivider(
                    modifier  = Modifier.padding(start = 76.dp, end = 16.dp),
                    color     = NoorDivider,
                    thickness = 0.6.dp
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
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
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(conversation.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                conversation.initials,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                conversation.name,
                fontSize   = 14.sp,
                fontWeight = if (conversation.unread > 0) FontWeight.Bold else FontWeight.SemiBold,
                color      = NoorTextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                conversation.lastMessage,
                fontSize  = 12.sp,
                color     = if (conversation.unread > 0) NoorTextSecondary else NoorTextHint,
                fontWeight = if (conversation.unread > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines  = 1
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(conversation.time, fontSize = 10.sp, color = NoorTextHint)
            if (conversation.unread > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(NoorOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        conversation.unread.toString(),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chat Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChatDetailScreen(
    conversation: Conversation,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages    = remember {
        mutableStateListOf<ChatMessage>().also {
            it.addAll(sampleMessages[conversation.id] ?: emptyList())
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // Chat header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(conversation.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(conversation.initials, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(conversation.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Employer", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }
        }

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom, // 👈 FIX
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
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
                        messages.add(ChatMessage(messageText.trim(), true, "Now"))
                        messageText = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send",
                    tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val bgColor   = if (message.isFromMe) NoorBlue else NoorSurface
    val textColor = if (message.isFromMe) Color.White else NoorTextPrimary
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val shape     = if (message.isFromMe)
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
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