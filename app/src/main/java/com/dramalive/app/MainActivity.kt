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
import androidx.lifecycle.lifecycleScope
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
                            onGoogleLogin = {
                                val signInIntent = authManager.getGoogleSignInClient().signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            onSkip = {
                                showLogin.value = false
                            },
                            errorMessage = error
                        )
                    } else {
                        val user = FirebaseAuth.getInstance().currentUser
                        
                        DramaLiveScreen(
                            userName = user?.displayName
                                ?: user?.email?.split("@")?.firstOrNull()
                                ?: "User",
                            userPhoto = user?.photoUrl?.toString(),
                            onLoginRequest = {
                                showLogin.value = true
                            }
                        )
                    }
                }
            }
        }
    }

    private val googleSignInLauncher = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult().let { contract ->
        registerForActivityResult(contract) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                    account.idToken?.let { idToken ->
                        handleGoogleLogin(idToken)
                    }
                } catch (e: Exception) {
                    loginError.value = "Google login failed: ${e.message}"
                }
            }
        }
    }

    private fun handleGoogleLogin(idToken: String) {
        authManager.signInWithGoogle(
            idToken = idToken,
            onSuccess = { user ->
                showLogin.value = false
                Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                loginError.value = error
            }
        )
    }

    private var mInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    private fun loadInterstitialAd() {
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, com.dramalive.app.Config.ADMOB_INTERSTITIAL_ID, adRequest, 
            object : com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                    mInterstitialAd = interstitialAd
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
                    loadInterstitialAd()
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
            loginError.value = "Please enter email and password"
            return
        }
        loginError.value = null

        authManager.signInWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                showLogin.value = false
            },
            onError = { error ->
                loginError.value = error
            }
        )
    }

    private fun handleEmailSignUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            loginError.value = "Please enter email and password"
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            loginError.value = "Please enter a valid email"
            return
        }
        if (password.length < 6) {
            loginError.value = "Password must be at least 6 characters"
            return
        }
        loginError.value = null

        authManager.signUpWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                Toast.makeText(
                    this,
                    "Account created! Please verify your email.",
                    Toast.LENGTH_LONG
                ).show()
                showLogin.value = false
            },
            onError = { error ->
                loginError.value = error
            }
        )
    }

    private fun translateFirebaseError(error: String): String {
        return error // Simplified as user wants English
    }
}