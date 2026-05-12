package com.danish.noorservice.ui.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Skill
import com.danish.noorservice.viewmodel.admin.CategoryManagementViewModel
import com.danish.noorservice.ui.theme.*

enum class CategoryType { INDIVIDUAL, VENDOR }

data class ManagedSkill(val id: String, val name: String)

data class ManagedCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val type: CategoryType,
    val skills: MutableList<ManagedSkill> = mutableListOf()
)

fun Category.toManagedCategory(): ManagedCategory {
    return ManagedCategory(
        id = this.id,
        label = this.label,
        emoji = this.emoji,
        type = if (this.categoryType == "individual") CategoryType.INDIVIDUAL else CategoryType.VENDOR,
        skills = this.skills.map { ManagedSkill(it.id, it.name) }.toMutableList()
    )
}

fun ManagedCategory.toCategory(): Category {
    return Category(
        id = this.id,
        label = this.label,
        emoji = this.emoji,
        categoryType = if (this.type == CategoryType.INDIVIDUAL) "individual" else "vendor",
        isActive = true,
        skills = this.skills.map { Skill(it.id, it.name) }
    )
}

@Composable
fun AdminCategoryManagementScreen(
    onBack: () -> Unit,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    var selectedType by remember { mutableStateOf(CategoryType.INDIVIDUAL) }
    var showAddCategory by remember { mutableStateOf(false) }
    var expandedCategoryId by remember { mutableStateOf<String?>(null) }
    var editTarget by remember { mutableStateOf<ManagedCategory?>(null) }
    var deleteTarget by remember { mutableStateOf<ManagedCategory?>(null) }
    var skillCategoryId by remember { mutableStateOf<String?>(null) }
    var editSkillTarget by remember { mutableStateOf<Pair<String, ManagedSkill>?>(null) }
    var deleteSkillTarget by remember { mutableStateOf<Pair<String, ManagedSkill>?>(null) }

    val visibleCategories = if (selectedType == CategoryType.INDIVIDUAL) {
        uiState.individualCategories.map { it.toManagedCategory() }
    } else {
        uiState.vendorCategories.map { it.toManagedCategory() }
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 22.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Categories & Skills", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Add, edit or remove categories and their skills", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NoorDivider),
        ) {
            CategoryType.entries.forEach { type ->
                val isSelected = selectedType == type
                val label = if (type == CategoryType.INDIVIDUAL) "Individuals" else "Vendors"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AdminPurple else Color.Transparent)
                        .clickable { selectedType = type; expandedCategoryId = null }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = if (isSelected) Color.White else NoorTextSecondary)
                }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AdminPurple)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AdminPurpleLight)
                        .border(1.dp, AdminPurple.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { showAddCategory = true }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(AdminPurple), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text("Add New Category", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AdminPurple)
                }
            }

            if (visibleCategories.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🗂️", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No categories yet", color = NoorTextHint, fontSize = 13.sp)
                        }
                    }
                }
            }

            items(visibleCategories, key = { it.id }) { cat ->
                CategoryCard(
                    category = cat,
                    isExpanded = expandedCategoryId == cat.id,
                    onToggle = { expandedCategoryId = if (expandedCategoryId == cat.id) null else cat.id },
                    onEdit = { editTarget = cat },
                    onDelete = { deleteTarget = cat },
                    onAddSkill = { skillCategoryId = cat.id },
                    onEditSkill = { skill -> editSkillTarget = Pair(cat.id, skill) },
                    onDeleteSkill = { skill -> deleteSkillTarget = Pair(cat.id, skill) }
                )
            }
        }
    }

    if (showAddCategory) {
        AddEditCategoryDialog(
            title = "Add Category",
            initialLabel = "",
            initialEmoji = "",
            confirmLabel = "Add",
            onConfirm = { lbl, emoji ->
                viewModel.addCategory(lbl, emoji, if (selectedType == CategoryType.INDIVIDUAL) "individual" else "vendor")
                showAddCategory = false
            },
            onDismiss = { showAddCategory = false }
        )
    }

    editTarget?.let { cat ->
        AddEditCategoryDialog(
            title = "Edit Category",
            initialLabel = cat.label,
            initialEmoji = cat.emoji,
            confirmLabel = "Save",
            onConfirm = { lbl, emoji ->
                viewModel.updateCategory(cat.toCategory().copy(label = lbl, emoji = emoji))
                editTarget = null
            },
            onDismiss = { editTarget = null }
        )
    }

    deleteTarget?.let { cat ->
        ConfirmDeleteDialog(
            title = "Delete \"${cat.label}\"?",
            body = "This will permanently remove the category and all its skills.",
            onConfirm = {
                viewModel.deleteCategory(cat.id)
                if (expandedCategoryId == cat.id) expandedCategoryId = null
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }

    skillCategoryId?.let { catId ->
        AddEditSkillDialog(
            title = "Add Skill",
            initialValue = "",
            confirmLabel = "Add",
            onConfirm = { name ->
                viewModel.addSkill(catId, name)
                skillCategoryId = null
            },
            onDismiss = { skillCategoryId = null }
        )
    }

    editSkillTarget?.let { (catId, skill) ->
        AddEditSkillDialog(
            title = "Edit Skill",
            initialValue = skill.name,
            confirmLabel = "Save",
            onConfirm = { name ->
                viewModel.updateSkill(catId, skill.id, name)
                editSkillTarget = null
            },
            onDismiss = { editSkillTarget = null }
        )
    }

    deleteSkillTarget?.let { (catId, skill) ->
        ConfirmDeleteDialog(
            title = "Delete skill \"${skill.name}\"?",
            body = "This skill will be removed from the category.",
            onConfirm = {
                viewModel.deleteSkill(catId, skill.id)
                deleteSkillTarget = null
            },
            onDismiss = { deleteSkillTarget = null }
        )
    }
}

@Composable
private fun CategoryCard(
    category: ManagedCategory,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddSkill: () -> Unit,
    onEditSkill: (ManagedSkill) -> Unit,
    onDeleteSkill: (ManagedSkill) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NoorSurface)
            .border(1.dp, NoorBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(AdminPurpleLight), contentAlignment = Alignment.Center) {
                Text(category.emoji, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(category.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Text("${category.skills.size} skill${if (category.skills.size != 1) "s" else ""}", fontSize = 11.sp, color = NoorTextHint)
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AdminPurple, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NoorRed, modifier = Modifier.size(18.dp))
            }
            Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = NoorTextHint, modifier = Modifier.size(20.dp))
        }

        AnimatedVisibility(visible = isExpanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NoorBackground.copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AdminPurpleLight)
                        .clickable { onAddSkill() }
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AdminPurple, modifier = Modifier.size(16.dp))
                    Text("Add Skill", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AdminPurple)
                }
                if (category.skills.isEmpty()) {
                    Text("No skills added yet.", fontSize = 12.sp, color = NoorTextHint, modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp))
                }
                category.skills.forEach { skill ->
                    SkillRow(skill = skill, onEdit = { onEditSkill(skill) }, onDelete = { onDeleteSkill(skill) })
                }
            }
        }
    }
}

@Composable
private fun SkillRow(skill: ManagedSkill, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NoorSurface)
            .border(1.dp, NoorBorder, RoundedCornerShape(8.dp))
            .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AdminAccent))
        Spacer(Modifier.width(10.dp))
        Text(skill.name, fontSize = 13.sp, color = NoorTextPrimary, modifier = Modifier.weight(1f))
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Skill", tint = AdminPurple, modifier = Modifier.size(15.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Skill", tint = NoorRed, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun AddEditCategoryDialog(
    title: String,
    initialLabel: String,
    initialEmoji: String,
    confirmLabel: String,
    onConfirm: (label: String, emoji: String) -> Unit,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf(initialLabel) }
    var emoji by remember { mutableStateOf(initialEmoji) }
    val isValid = label.isNotBlank() && emoji.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NoorSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            OutlinedTextField(value = emoji, onValueChange = { if (it.length <= 4) emoji = it }, label = { Text("Emoji") }, placeholder = { Text("e.g. 🚗") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = adminTextFieldColors())
            OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Category Name") }, placeholder = { Text("e.g. Electrician") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = adminTextFieldColors())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                Button(onClick = { if (isValid) onConfirm(label.trim(), emoji.trim()) }, enabled = isValid, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AdminPurple)) { Text(confirmLabel) }
            }
        }
    }
}

@Composable
private fun AddEditSkillDialog(title: String, initialValue: String, confirmLabel: String, onConfirm: (name: String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(initialValue) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NoorSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Skill Name") }, placeholder = { Text("e.g. Solar Installation") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = adminTextFieldColors())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                Button(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }, enabled = name.isNotBlank(), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = AdminPurple)) { Text(confirmLabel) }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(title: String, body: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NoorSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🗓️  $title", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            Text(body, fontSize = 13.sp, color = NoorTextSecondary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                Button(onClick = onConfirm, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = NoorRed)) { Text("Delete", color = Color.White) }
            }
        }
    }
}

@Composable
private fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPurple, focusedLabelColor = AdminPurple, cursorColor = AdminPurple, unfocusedBorderColor = NoorBorder, unfocusedLabelColor = NoorTextHint)