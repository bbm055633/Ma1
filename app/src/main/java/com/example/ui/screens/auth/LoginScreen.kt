package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * شاشة تسجيل الدخول المتوافقة بالكامل مع هوية وثيمات 2027
 * ترتبط ديناميكياً بليلة مرصعة بالنجوم، وضع النهار/الليل، وألوان FahadColors2027
 */
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    var isRegistering by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isStarryActive = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val isEnglish by viewModel.isEnglish.collectAsState()
    val starryNightSettings by viewModel.starryNightSettings.collectAsState()
    val themeSettings by viewModel.themeSettings.collectAsState()

    IslamicGeometricPatternBackground(
        patternColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            // شريط علوي سريع للتحكم بالثيم والمظهر من أول شاشة (صفحة الدخول)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة 2027
                Surface(
                    shape = Shapes2027.Pill,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("✨", fontSize = 11.sp)
                        Text(
                            text = "Fahad AI 2027",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // أزرار التحكم السريع: النهار/الليل، ليلة مرصعة بالنجوم، واللغة
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // تبديل سماء ليلة مرصعة بالنجوم
                    IconButton(
                        onClick = { viewModel.toggleStarryNightEnabled() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (starryNightSettings.isEnabled) Color(0xFFFFB703).copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("login_starry_toggle")
                    ) {
                        Icon(
                            imageVector = if (starryNightSettings.isEnabled) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Starry Night Live Sky",
                            tint = if (starryNightSettings.isEnabled) Color(0xFFFFB703) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // تبديل النهار والليل
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("login_dark_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (themeSettings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Dark/Light Mode",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // تبديل اللغة
                    FilledTonalButton(
                        onClick = { viewModel.toggleLanguage() },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(34.dp),
                        shape = Shapes2027.Pill
                    ) {
                        Text(
                            text = if (isEnglish) "عربي" else "EN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // بطاقة تسجيل الدخول الأنيقة 2027
            Surface(
                shape = Shapes2027.Large,
                color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                shadowElevation = if (isStarryActive) 0.dp else 4.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isStarryActive) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp)
                    .testTag("login_card")
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // أيقونة وشعار فهد الذكي 2027
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 28.sp)
                    }

                    // العنوان والترحيب
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isEnglish) "Fahad AI • Enterprise" else "فهد الذكي • Fahad AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isRegistering) {
                                if (isEnglish) "Create Executive AI Profile" else "تسجيل حساب تنفيذي جديد"
                            } else {
                                if (isEnglish) "Welcome back, please sign in" else "يا هلا بك.. تفضل بتسجيل الدخول"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (errorMessage != null) {
                        Surface(
                            shape = Shapes2027.Medium,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // حقل الاسم (في حال التسجيل الجديد)
                    AnimatedVisibility(visible = isRegistering) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isEnglish) "Full Name" else "الاسم الكريم") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            shape = Shapes2027.Medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_name_input"),
                            singleLine = true
                        )
                    }

                    // حقل البريد الإلكتروني
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (isEnglish) "Enterprise Email" else "البريد الإلكتروني المهني") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = Shapes2027.Medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        singleLine = true
                    )

                    // حقل كلمة المرور
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (isEnglish) "Password" else "كلمة المرور") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = Shapes2027.Medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        singleLine = true
                    )

                    // روابط الاستعادة والبصمة
                    if (!isRegistering) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                errorMessage = if (isEnglish) "Password recovery instructions sent to your email." else "تم إرسال تعليمات استعادة كلمة المرور إلى بريدك."
                            }) {
                                Text(
                                    text = if (isEnglish) "Forgot Password?" else "نسيت كلمة المرور؟",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            TextButton(onClick = {
                                isLoading = true
                                viewModel.loginUser(
                                    email = email,
                                    pass = password,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            }) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isEnglish) "Biometric" else "دخول بالبصمة",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // زر تسجيل الدخول الأساسي
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            if (isRegistering) {
                                viewModel.registerUser(
                                    name = name,
                                    email = email,
                                    pass = password,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            } else {
                                viewModel.loginUser(
                                    email = email,
                                    pass = password,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_primary_button"),
                        shape = Shapes2027.Medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isRegistering) {
                                    if (isEnglish) "Create Executive Account" else "إنشاء الحساب وحفظه"
                                } else {
                                    if (isEnglish) "Sign In" else "تسجيل الدخول"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // زر التخطي والمتابعة كضيف
                    OutlinedButton(
                        onClick = {
                            viewModel.setUserName(if (isEnglish) "Guest User" else "ضيف التطبيق")
                            viewModel.setUserEmail("guest@fahadai.local")
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("login_guest_button"),
                        shape = Shapes2027.Medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isEnglish) "Continue as Guest 👤" else "تخطٍّ ومتابعة كضيف 👤",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // التبديل بين تسجيل الدخول وتسجيل حساب جديد
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRegistering) {
                                if (isEnglish) "Already have an account?" else "لديك حساب بالفعل؟"
                            } else {
                                if (isEnglish) "Don't have an account?" else "ليس لديك حساب تنفيذي؟"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = {
                            isRegistering = !isRegistering
                            errorMessage = null
                        }) {
                            Text(
                                text = if (isRegistering) {
                                    if (isEnglish) "Sign In" else "تسجيل الدخول"
                                } else {
                                    if (isEnglish) "Register Now" else "تسجيل حساب جديد"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
