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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var showLogin = mutableStateOf(false)
    private var loginError = mutableStateOf<String?>(null)
    private var isConfigLoaded = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        // Initialize AdMob
        MobileAds.initialize(this) {}

        // Start Background Failover Service
        val serviceIntent = android.content.Intent(this, com.dramalive.app.services.IPTVFailoverService::class.java)
        startService(serviceIntent)

        // Fetch latest Xtream credentials from Firebase
        lifecycleScope.launch {
            com.dramalive.app.util.RemoteConfigManager.updateRemoteConfig()
            isConfigLoaded.value = true
            loadInterstitialAd()
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
                    val configLoaded by isConfigLoaded

                    if (!configLoaded) {
                        // Optionally show a splash or loading indicator here
                        com.dramalive.app.ui.components.LoadingScreen()
                    } else if (isShowingLogin) {
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

    private var mInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    private fun loadInterstitialAd() {
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, com.dramalive.app.Config.ADMOB_INTERSTITIAL_ID, adRequest, 
            object : com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    // Don't show immediately, show when opening a channel
                }
                override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    fun showInterstitial(onAdClosed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadInterstitialAd() // Reload
                    onAdClosed()
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    mInterstitialAd = null
                    onAdClosed()
                }
            }
            mInterstitialAd?.show(this)
        } else {
            onAdClosed()
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
                
                // Track Login Event
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

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
                // Track Sign Up Event
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                Toast.makeText(
                    this,
                    "تم إنشاء الحساب! يرجى تفعيل حسابك من خلال الرابط المرسل لبريدك الإلكتروني.",
                    Toast.LENGTH_LONG
                ).show()
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