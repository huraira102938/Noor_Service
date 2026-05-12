package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Skill
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryManagementState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val individualCategories: List<Category> = emptyList(),
    val vendorCategories: List<Category> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementState())
    val uiState: StateFlow<CategoryManagementState> = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val allCategories = userRepository.getAllCategories()
                val individualCategories = allCategories.filter { it.categoryType.equals("individual", ignoreCase = true) }
                val vendorCategories = allCategories.filter { it.categoryType.equals("vendor", ignoreCase = true) }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    categories = allCategories,
                    individualCategories = individualCategories,
                    vendorCategories = vendorCategories
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun addCategory(label: String, emoji: String, type: String) {
        viewModelScope.launch {
            try {
                val id = label.lowercase().replace(" ", "_") + "_${System.currentTimeMillis()}"
                val category = Category(
                    id = id,
                    label = label,
                    emoji = emoji,
                    categoryType = type,
                    isActive = true,
                    skills = emptyList()
                )
                userRepository.addCategory(category)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                userRepository.updateCategory(category)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteCategory(categoryId)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addSkill(categoryId: String, skillName: String) {
        viewModelScope.launch {
            try {
                val skillId = skillName.lowercase().replace(" ", "_") + "_${System.currentTimeMillis()}"
                val skill = Skill(id = skillId, name = skillName)
                userRepository.addSkillToCategory(categoryId, skill)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateSkill(categoryId: String, oldSkillId: String, newSkillName: String) {
        viewModelScope.launch {
            try {
                val newSkill = Skill(id = oldSkillId, name = newSkillName)
                userRepository.updateSkillInCategory(categoryId, oldSkillId, newSkill)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteSkill(categoryId: String, skillId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteSkillFromCategory(categoryId, skillId)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}