package com.dramalive.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramalive.app.ui.theme.*

@Composable
fun LoginScreen(
    onEmailLogin: (String, String) -> Unit,
    onEmailSignUp: (String, String) -> Unit,
    onSkip: () -> Unit,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Validation helpers
    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = password.length >= 6
    val isConfirmValid = !isSignUp || password == confirmPassword
    val canSubmit = email.isNotBlank() && isEmailValid && isPasswordValid && isConfirmValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NetflixRed.copy(alpha = 0.08f),
                            DeepBlack.copy(alpha = 0.95f),
                            DeepBlack
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = androidx.compose.ui.res.painterResource(
                    id = com.dramalive.app.R.drawable.app_icon
                ),
                contentDescription = "LivePlus Logo",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "LIVE PLUS",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = NetflixRed,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isSignUp) "إنشاء حساب جديد" else "تسجيل الدخول",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )

            Text(
                text = if (isSignUp)
                    "أدخل بريدك الإلكتروني الحقيقي لإنشاء حساب"
                else
                    "سجّل دخولك بالبريد الإلكتروني",
                fontSize = 13.sp,
                color = SubtextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Error Message
            AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = NetflixRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = NetflixRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = NetflixRed,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ─── Email Field ───────────────────────────────────────
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("البريد الإلكتروني", color = SubtextGray) },
                leadingIcon = {
                    Icon(Icons.Rounded.Email, contentDescription = null, tint = SubtextGray)
                },
                trailingIcon = {
                    if (email.isNotEmpty() && !isEmailValid) {
                        Icon(
                            Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = NetflixRed
                        )
                    }
                },
                isError = email.isNotEmpty() && !isEmailValid,
                supportingText = {
                    if (email.isNotEmpty() && !isEmailValid) {
                        Text("يرجى إدخال بريد إلكتروني صحيح", color = NetflixRed, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = DimGray,
                    errorBorderColor = NetflixRed,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    errorTextColor = PureWhite,
                    cursorColor = NetflixRed,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    errorContainerColor = CardDark
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ─── Password Field ────────────────────────────────────
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور", color = SubtextGray) },
                leadingIcon = {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = SubtextGray)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                            tint = SubtextGray
                        )
                    }
                },
                isError = password.isNotEmpty() && !isPasswordValid,
                supportingText = {
                    if (password.isNotEmpty() && !isPasswordValid) {
                        Text("كلمة المرور يجب أن تكون 6 أحرف على الأقل", color = NetflixRed, fontSize = 12.sp)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = DimGray,
                    errorBorderColor = NetflixRed,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    errorTextColor = PureWhite,
                    cursorColor = NetflixRed,
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    errorContainerColor = CardDark
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onDone = {
                        focusManager.clearFocus()
                        if (canSubmit) onEmailLogin(email, password)
                    }
                ),
                singleLine = true
            )

            // ─── Confirm Password (Sign Up only) ───────────────────
            AnimatedVisibility(visible = isSignUp) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("تأكيد كلمة المرور", color = SubtextGray) },
                        leadingIcon = {
                            Icon(Icons.Rounded.LockReset, contentDescription = null, tint = SubtextGray)
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Rounded.Visibility
                                    else Icons.Rounded.VisibilityOff,
                                    contentDescription = null,
                                    tint = SubtextGray
                                )
                            }
                        },
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("كلمتا المرور غير متطابقتين", color = NetflixRed, fontSize = 12.sp)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NetflixRed,
                            unfocusedBorderColor = DimGray,
                            errorBorderColor = NetflixRed,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            errorTextColor = PureWhite,
                            cursorColor = NetflixRed,
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark,
                            errorContainerColor = CardDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (canSubmit) onEmailSignUp(email, password)
                            }
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Submit Button ─────────────────────────────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isSignUp) onEmailSignUp(email, password)
                    else onEmailLogin(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NetflixRed,
                    disabledContainerColor = NetflixRed.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = canSubmit
            ) {
                Icon(
                    imageVector = if (isSignUp) Icons.Rounded.PersonAdd else Icons.Rounded.Login,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isSignUp) "إنشاء الحساب" else "دخول",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Toggle Sign Up / Sign In ──────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSignUp) "لديك حساب بالفعل؟ " else "لا تملك حساباً؟ ",
                    color = SubtextGray,
                    fontSize = 14.sp
                )
                Text(
                    text = if (isSignUp) "سجّل دخولك" else "أنشئ حساباً الآن",
                    color = NetflixRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        isSignUp = !isSignUp
                        confirmPassword = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ─── Skip Button ───────────────────────────────────────
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(DimGray, DimGray))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "تخطي ▶  الدخول بدون حساب",
                    color = SubtextGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
