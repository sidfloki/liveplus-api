package com.dramalive.app.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    fun getGoogleSignInClient(): GoogleSignInClient {
        val clientId = context.getString(context.resources.getIdentifier("default_web_client_id", "string", context.packageName))
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(clientId)
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }

    fun getCurrentUserPhoto(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }

    fun signOut() {
        auth.signOut()
        getGoogleSignInClient().signOut()
    }

    // Google Sign In
    fun signInWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(auth.currentUser)
                } else {
                    onError(task.exception?.message ?: "Google authentication failed")
                }
            }
    }

    // Email/Password Sign In
    fun signInWithEmail(
        email: String, 
        password: String, 
        onSuccess: (FirebaseUser?) -> Unit, 
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(auth.currentUser)
                } else {
                    onError(task.exception?.message ?: "Sign in failed")
                }
            }
    }

    // Email/Password Sign Up
    fun signUpWithEmail(
        email: String, 
        password: String, 
        onSuccess: (FirebaseUser?) -> Unit, 
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                    onSuccess(user)
                } else {
                    onError(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    fun sendEmailVerification(onComplete: (Boolean, String?) -> Unit) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    // Password Reset
    fun sendPasswordReset(
        email: String, 
        onSuccess: () -> Unit, 
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }
}
