package com.example.ui.screens.products

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AgentDataDefaults
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.generateGradientForColor
import com.example.ui.theme.*
import com.example.ui.util.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Samsung One UI 9 Product Generator Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductGeneratorScreen(
    productId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler {
        viewModel.clearGeneratedProduct()
        onBack()
    }

    val product = remember(productId) {
        AgentDataDefaults.PRODUCTS.firstOrNull { it.id == productId } ?: AgentDataDefaults.PRODUCTS.first()
    }

    val isEnglish by viewModel.isEnglish.collectAsState()
    val isGenerating by viewModel.isProductGenerating.collectAsState()
    val generatedDoc by viewModel.generatedProductDoc.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    val localizedFields = remember(product, isEnglish) { product.localizedFields(isEnglish) }
    val inputValues = remember(product, isEnglish) {
        mutableStateMapOf<String, String>().apply {
            localizedFields.forEach { field ->
                put(field, "")
            }
        }
    }

    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.clearGeneratedProduct()
                                        onBack()
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF9F6F0))
                                        .testTag("product_gen_back_button")
                                ) {
                                    Icon(
                                        imageVector = if (isEnglish) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = if (isEnglish) "Back" else "رجوع",
                                        tint = Color(0xFF1C1B1A)
                                    )
                                }

                                Column {
                                    Text(
                                        text = product.localizedTitle(isEnglish),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1C1B1A)
                                    )
                                    Text(
                                        text = product.localizedCategory(isEnglish),
                                        fontSize = 11.sp,
                                        color = userPrimaryColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = userPrimaryColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "One UI 9",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = userPrimaryColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Description Card: Samsung One UI 9 White 24px Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(generateGradientForColor(userPrimaryColor, product.title.length)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = product.localizedDescription(isEnglish),
                            fontSize = 13.sp,
                            color = Color(0xFF6E685F),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inputs Card: Pure White 24px Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Generation Parameters & Requirements:" else "بيانات ومحددات التوليد:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1A)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Inputs with One UI 9 rounded borders
                        localizedFields.forEach { field ->
                            OutlinedTextField(
                                value = inputValues[field] ?: "",
                                onValueChange = { inputValues[field] = it },
                                label = { Text(field, fontSize = 13.sp) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = userPrimaryColor,
                                    unfocusedBorderColor = Color(0xFFE8E4DC),
                                    focusedContainerColor = Color(0xFFF9F6F0),
                                    unfocusedContainerColor = Color(0xFFF9F6F0)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Generate Button: One UI 9 Pill Button
                        Button(
                            onClick = {
                                viewModel.generateProduct(product, inputValues)
                                isCopied = false
                            },
                            enabled = !isGenerating,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_product_button")
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isEnglish) "Processing & Generating with AI..." else "جاري المعالجة والتوليد بالذكاء الاصطناعي...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isEnglish) "Generate Official Document Now" else "توليد الوثيقة المعتمدة الآن", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Generated Result Display: Pure White 24px Card
                if (generatedDoc != null) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, userPrimaryColor.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF107C41), modifier = Modifier.size(20.dp))
                                    Text(
                                        text = if (isEnglish) "Generated Document Ready:" else "الوثيقة المولدة جاهزة:",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1C1B1A)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedDoc ?: ""))
                                            isCopied = true
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF9F6F0))
                                    ) {
                                        Icon(
                                            Icons.Outlined.ContentCopy,
                                            contentDescription = if (isEnglish) "Copy" else "نسخ",
                                            tint = if (isCopied) Color(0xFF107C41) else userPrimaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    IconButton(
                                        onClick = {
                                            if (!generatedDoc.isNullOrBlank()) {
                                                val sendIntent = android.content.Intent().apply {
                                                    action = android.content.Intent.ACTION_SEND
                                                    putExtra(android.content.Intent.EXTRA_TEXT, generatedDoc)
                                                    type = "text/plain"
                                                }
                                                val shareIntent = android.content.Intent.createChooser(sendIntent, if (isEnglish) "Share Document" else "مشاركة الوثيقة")
                                                context.startActivity(shareIntent)
                                            }
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF9F6F0))
                                    ) {
                                        Icon(
                                            Icons.Outlined.Share,
                                            contentDescription = if (isEnglish) "Share" else "مشاركة",
                                            tint = userPrimaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            if (isCopied) {
                                Text(
                                    text = if (isEnglish) "Document copied to clipboard successfully ✅" else "تم نسخ الوثيقة إلى الحافظة بنجاح ✅",
                                    fontSize = 11.sp,
                                    color = Color(0xFF107C41),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1EFE9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF9F6F0),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = generatedDoc ?: "",
                                    fontSize = 13.sp,
                                    color = Color(0xFF1C1B1A),
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
