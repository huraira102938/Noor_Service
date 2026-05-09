package com.danish.noorservice.data.repository

import com.danish.noorservice.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return AuthResult.Error("Login failed")
            val user = getUserData(firebaseUser.uid)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(email: String, password: String, role: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return AuthResult.Error("Signup failed")

            val user = User(
                uid = firebaseUser.uid,
                email = email,
                role = role,
                isProfileComplete = false
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun getUserData(uid: String): User {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java) ?: User(uid = uid)
        } catch (e: Exception) {
            User(uid = uid)
        }
    }

    fun getCurrentUserUid(): String? = auth.currentUser?.uid

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun logout() {
        auth.signOut()
    }

    /**
     * Emits the current auth state as a Flow.
     *
     * IMPORTANT: We emit a special "loading" sentinel first so the UI
     * can distinguish between "not yet checked" vs "definitely logged out".
     * We suspend inside the listener using a coroutine-friendly approach
     * so there is no GlobalScope race condition.
     */
    fun authStateChanges(): Flow<AuthState> = callbackFlow {
        // Immediately emit Loading so NavHost waits before picking a destination
        trySend(AuthState.Loading)

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(AuthState.Unauthenticated)
            } else {
                // Launch inside callbackFlow's scope — no GlobalScope needed
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val user = try {
                        getUserData(firebaseUser.uid)
                    } catch (e: Exception) {
                        User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
                    }
                    trySend(AuthState.Authenticated(user))
                }
            }
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}

/**
 * Sealed class replacing nullable User so we can distinguish
 * Loading / Unauthenticated / Authenticated cleanly.
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}