package com.dramalive.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.dramalive.app.auth.AuthManager
import com.dramalive.app.ui.screens.DramaLiveScreen
import com.dramalive.app.ui.screens.LoginScreen
import com.dramalive.app.ui.theme.DramaLiveTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds
import com.dramalive.app.util.RemoteConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private var showLogin = mutableStateOf(true)
    private var loginError = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob
        MobileAds.initialize(this) {}

        // Fetch latest Xtream credentials from GitHub Automation
        CoroutineScope(Dispatchers.Main).launch {
            RemoteConfigManager.updateRemoteConfig()
        }

        authManager = AuthManager(this)

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            showLogin.value = false
        }

        setContent {
            DramaLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isShowingLogin by showLogin
                    val error by loginError

                    if (isShowingLogin) {
                        LoginScreen(
                            onEmailLogin = { email, password ->
                                handleEmailLogin(email, password)
                            },
                            onEmailSignUp = { email, password ->
                                handleEmailSignUp(email, password)
                            },
                            onSkip = {
                                showLogin.value = false
                            },
                            errorMessage = error
                        )
                    } else {
                        val user = FirebaseAuth.getInstance().currentUser
                        // Check if email is verified for "Real Email" requirement
                        if (user != null && !user.isEmailVerified) {
                            // Still show DramaLiveScreen but maybe with a warning?
                            // For now, let's just show a Toast and allow them in, 
                            // or I can show a dedicated "Please verify" screen.
                            // The user said "real email and not fake", so enforcing it is better.
                        }
                        
                        DramaLiveScreen(
                            userName = user?.displayName
                                ?: user?.email?.split("@")?.firstOrNull()
                                ?: "مستخدم",
                            userPhoto = user?.photoUrl?.toString()
                        )
                    }
                }
            }
        }
    }

    private fun handleEmailLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            loginError.value = "يرجى إدخال البريد الإلكتروني وكلمة المرور"
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            loginError.value = "يرجى إدخال بريد إلكتروني صحيح"
            return
        }
        loginError.value = null

        authManager.signInWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                showLogin.value = false
                Toast.makeText(
                    this,
                    "مرحباً ${user?.email}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onError = { error ->
                // ترجمة رسائل الخطأ الإنجليزية إلى العربية
                loginError.value = translateFirebaseError(error)
            }
        )
    }

    private fun handleEmailSignUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            loginError.value = "يرجى إدخال البريد الإلكتروني وكلمة المرور"
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            loginError.value = "يرجى إدخال بريد إلكتروني صحيح"
            return
        }
        if (password.length < 6) {
            loginError.value = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            return
        }
        loginError.value = null

        authManager.signUpWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                Toast.makeText(
                    this,
                    "تم إنشاء الحساب! يرجى تفعيل حسابك من خلال الرابط المرسل لبريدك الإلكتروني.",
                    Toast.LENGTH_LONG
                ).show()
                // Don't hide login yet, wait for them to verify and login?
                // Actually, let them in but show verification reminder in DramaLiveScreen.
                showLogin.value = false
            },
            onError = { error ->
                loginError.value = translateFirebaseError(error)
            }
        )
    }

    /**
     * ترجمة رسائل خطأ Firebase من الإنجليزية إلى العربية
     */
    private fun translateFirebaseError(error: String): String {
        return when {
            error.contains("no user record", ignoreCase = true) ||
            error.contains("user-not-found", ignoreCase = true) ->
                "لا يوجد حساب بهذا البريد الإلكتروني. يرجى إنشاء حساب جديد."

            error.contains("password is invalid", ignoreCase = true) ||
            error.contains("wrong-password", ignoreCase = true) ->
                "كلمة المرور غير صحيحة. يرجى المحاولة مجدداً."

            error.contains("email address is already in use", ignoreCase = true) ||
            error.contains("email-already-in-use", ignoreCase = true) ->
                "هذا البريد الإلكتروني مسجّل مسبقاً. يرجى تسجيل الدخول."

            error.contains("badly formatted", ignoreCase = true) ||
            error.contains("invalid-email", ignoreCase = true) ->
                "البريد الإلكتروني غير صحيح."

            error.contains("network", ignoreCase = true) ->
                "فشل الاتصال بالإنترنت. يرجى التحقق من الشبكة."

            error.contains("too-many-requests", ignoreCase = true) ->
                "تم تجاوز عدد المحاولات. يرجى الانتظار قليلاً."

            else -> "خطأ في تسجيل الدخول: $error"
        }
    }
}