package com.danish.noorservice.data.repository

import android.util.Log
import com.danish.noorservice.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Employer operations
    suspend fun saveEmployerProfile(employer: Employer): Result<Unit> {
        return try {
            firestore.collection("employers")
                .document(employer.uid)
                .set(employer)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmployerProfile(uid: String): Employer? {
        val doc = firestore.collection("employers").document(uid).get().await()
        return doc.toObject(Employer::class.java)
    }

    // Employee operations
    suspend fun saveEmployeeProfile(employee: Employee): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(employee.uid)
                .set(employee)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmployeeProfile(uid: String): Employee? {
        val doc = firestore.collection("employees").document(uid)
            .get(Source.SERVER)
            .await()
        return doc.toObject(Employee::class.java)
    }

    suspend fun saveEmployeeServices(uid: String, services: List<EmployeeService>): Result<Unit> {
        return try {
            services.forEach { service ->
                firestore.collection("employeeServices")
                    .document(uid)
                    .collection("services")
                    .document(service.serviceId)
                    .set(service)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmployeeServices(uid: String): List<EmployeeService> {
        val docs = firestore.collection("employeeServices")
            .document(uid)
            .collection("services")
            .get(Source.SERVER)
            .await()
        return docs.documents.mapNotNull { it.toObject(EmployeeService::class.java) }
    }

suspend fun getAllEmployees(): List<Employee> {
        val docs = firestore.collection("employees")
            .get(Source.SERVER)
            .await()
        return docs.documents.mapNotNull { it.toObject(Employee::class.java) }
    }

    suspend fun getAllApprovedEmployees(): List<Employee> {
        val docs = firestore.collection("employees")
            .whereEqualTo("profileApproved", true)
            .get(Source.SERVER)
            .await()
        return docs.documents.mapNotNull { it.toObject(Employee::class.java) }
    }

    suspend fun getAllVendors(): List<Vendor> {
        val docs = firestore.collection("vendors")
            .get(Source.SERVER)
            .await()
        return docs.documents.mapNotNull { it.toObject(Vendor::class.java) }
    }

    suspend fun searchEmployees(city: String?, serviceId: String?): List<Employee> {
        val docs = firestore.collection("employees")
            .whereEqualTo("profileApproved", true)
            .get()
            .await()

        var employees = docs.documents.mapNotNull { it.toObject(Employee::class.java) }

        if (!city.isNullOrBlank()) {
            employees = employees.filter { it.city.equals(city, ignoreCase = true) }
        }

        return employees
    }

    // Vendor operations
    suspend fun saveVendorProfile(vendor: Vendor): Result<Unit> {
        return try {
            firestore.collection("vendors")
                .document(vendor.uid)
                .set(vendor)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVendorProfile(uid: String): Vendor? {
        val doc = firestore.collection("vendors").document(uid)
            .get(Source.SERVER)
            .await()
        return doc.toObject(Vendor::class.java)
    }

    suspend fun saveVendorServices(uid: String, services: List<VendorService>): Result<Unit> {
        return try {
            services.forEach { service ->
                firestore.collection("vendorServices")
                    .document(uid)
                    .collection("services")
                    .document(service.serviceId)
                    .set(service)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVendorService(uid: String, serviceId: String) {
        firestore.collection("vendorServices")
            .document(uid)
            .collection("services")
            .document(serviceId)
            .delete()
            .await()
    }

    suspend fun updateVendorServiceActive(uid: String, service: VendorService): Result<Unit> {
        return try {
            firestore.collection("vendorServices")
                .document(uid)
                .collection("services")
                .document(service.serviceId)
                .set(service)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVendorServices(uid: String): List<VendorService> {
        val docs = firestore.collection("vendorServices")
            .document(uid)
            .collection("services")
            .get(Source.SERVER)
            .await()
        return docs.documents.mapNotNull { it.toObject(VendorService::class.java) }
    }

    suspend fun getAllApprovedVendors(): List<Vendor> {
        try {
            Log.e("UserRepository", ">>>>>> START getAllApprovedVendors")
            
            val task = firestore.collection("vendors").get(Source.SERVER)
            val docs = task.await()
            
            Log.e("UserRepository", ">>>>>> Found ${docs.size()} docs in vendors collection")
            
            if (docs.isEmpty) {
                Log.e("UserRepository", ">>>>>> NO DOCUMENTS FOUND!")
                return emptyList()
            }
            
            val vendors = mutableListOf<Vendor>()
            for (doc in docs.documents) {
                Log.e("UserRepository", ">>>>>> Processing doc: ${doc.id}")
                
                val vendor = doc.toObject(Vendor::class.java)
                if (vendor != null) {
                    Log.e("UserRepository", ">>>>>> MAPPED: ${vendor.businessName}, profileApproved=${vendor.isProfileApproved}, active=${vendor.isActive}")
                    vendors.add(vendor)
                } else {
                    Log.e("UserRepository", ">>>>>> FAILED TO MAP: ${doc.id}")
                }
            }
            
            Log.e("UserRepository", ">>>>>> Returning ${vendors.size} vendors")
            return vendors
        } catch (e: Exception) {
            Log.e("UserRepository", ">>>>>> ERROR: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getAllEmployers(): List<Employer> {
        return try {
            val docs = firestore.collection("employers").get(Source.SERVER).await()
            docs.documents.mapNotNull { it.toObject(Employer::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateProfileApproval(userId: String, role: String, isApproved: Boolean): Result<Unit> {
        return try {
            when (role) {
                "employee" -> {
                    firestore.collection("employees").document(userId)
                        .update("profileApproved", isApproved).await()
                }
                "vendor" -> {
                    firestore.collection("vendors").document(userId)
                        .update("profileApproved", isApproved).await()
                }
                "employer" -> {
                    firestore.collection("employers").document(userId)
                        .update("profileApproved", isApproved).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserActive(userId: String, role: String, isActive: Boolean): Result<Unit> {
        return try {
            when (role) {
                "employee" -> {
                    firestore.collection("employees").document(userId)
                        .update("active", isActive).await()
                }
                "vendor" -> {
                    firestore.collection("vendors").document(userId)
                        .update("active", isActive).await()
                }
                "employer" -> {
                    firestore.collection("employers").document(userId)
                        .update("active", isActive).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfileComplete(uid: String, isComplete: Boolean): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("isProfileComplete", isComplete).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIsProfileComplete(uid: String): Boolean {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getBoolean("isProfileComplete") ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Category and Skills Management
    suspend fun getAllCategories(): List<Category> {
        return try {
            val docs = firestore.collection("categories")
                .get()
                .await()
            val result = docs.documents.mapNotNull { doc ->
                val cat = doc.toObject(Category::class.java)
                android.util.Log.d("CategoryRepo", "Doc ${doc.id}: type=${cat?.categoryType}, label=${cat?.label}")
                cat
            }
            result
        } catch (e: Exception) {
            android.util.Log.e("CategoryRepo", "Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCategoriesByType(type: String): List<Category> {
        return try {
            val docs = firestore.collection("categories")
                .whereEqualTo("type", type)
                .whereEqualTo("active", true)
                .get()
                .await()
            docs.documents.mapNotNull { it.toObject(Category::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addCategory(category: Category): Result<Unit> {
        return try {
            firestore.collection("categories").document(category.id).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            firestore.collection("categories").document(category.id).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            firestore.collection("categories").document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSkillToCategory(categoryId: String, skill: Skill): Result<Unit> {
        return try {
            val doc = firestore.collection("categories").document(categoryId).get().await()
            val category = doc.toObject(Category::class.java)
            if (category != null) {
                val updatedSkills = category.skills + skill
                firestore.collection("categories").document(categoryId)
                    .update("skills", updatedSkills).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSkillInCategory(categoryId: String, oldSkillId: String, newSkill: Skill): Result<Unit> {
        return try {
            val doc = firestore.collection("categories").document(categoryId).get().await()
            val category = doc.toObject(Category::class.java)
            if (category != null) {
                val updatedSkills = category.skills.map { if (it.id == oldSkillId) newSkill else it }
                firestore.collection("categories").document(categoryId)
                    .update("skills", updatedSkills).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSkillFromCategory(categoryId: String, skillId: String): Result<Unit> {
        return try {
            val doc = firestore.collection("categories").document(categoryId).get().await()
            val category = doc.toObject(Category::class.java)
            if (category != null) {
                val updatedSkills = category.skills.filter { it.id != skillId }
                firestore.collection("categories").document(categoryId)
                    .update("skills", updatedSkills).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}