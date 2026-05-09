package com.danish.noorservice.data.repository

import com.danish.noorservice.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
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
        val doc = firestore.collection("employees").document(uid).get().await()
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
            .get()
            .await()
        return docs.documents.mapNotNull { it.toObject(EmployeeService::class.java) }
    }

    suspend fun getAllApprovedEmployees(): List<Employee> {
        val docs = firestore.collection("employees")
            .whereEqualTo("isProfileApproved", true)
            .get()
            .await()
        return docs.documents.mapNotNull { it.toObject(Employee::class.java) }
    }

    suspend fun searchEmployees(city: String?, serviceId: String?): List<Employee> {
        val docs = firestore.collection("employees")
            .whereEqualTo("isProfileApproved", true)
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
        val doc = firestore.collection("vendors").document(uid).get().await()
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

    suspend fun getVendorServices(uid: String): List<VendorService> {
        val docs = firestore.collection("vendorServices")
            .document(uid)
            .collection("services")
            .get()
            .await()
        return docs.documents.mapNotNull { it.toObject(VendorService::class.java) }
    }

    suspend fun getAllApprovedVendors(): List<Vendor> {
        val docs = firestore.collection("vendors")
            .whereEqualTo("isProfileApproved", true)
            .whereEqualTo("isActive", true)
            .get()
            .await()
        return docs.documents.mapNotNull { it.toObject(Vendor::class.java) }
    }

    suspend fun updateProfileApproval(userId: String, role: String, isApproved: Boolean): Result<Unit> {
        return try {
            when (role) {
                "employee" -> {
                    firestore.collection("employees").document(userId)
                        .update("isProfileApproved", isApproved).await()
                }
                "vendor" -> {
                    firestore.collection("vendors").document(userId)
                        .update("isProfileApproved", isApproved).await()
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
}