package com.example.ui.screens.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.models.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PaymentModalMode {
    TOP_UP,
    SUBSCRIPTION_UPGRADE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaudiPaymentBottomSheet(
    mode: PaymentModalMode,
    onDismiss: () -> Unit,
    onPaymentSuccess: (amountSar: Double, points: Int, title: String) -> Unit,
    onProcessTopUp: (TopUpPackage, SaudiPaymentMethod, String) -> Unit,
    onProcessUpgrade: (SubscriptionPlan, SaudiPaymentMethod, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var selectedTopUpPackage by remember { mutableStateOf(PaymentDefaults.TOP_UP_PACKAGES[1]) }
    var selectedPlan by remember { mutableStateOf(PaymentDefaults.SUBSCRIPTION_PLANS[1]) }
    var selectedPaymentMethod by remember { mutableStateOf(SaudiPaymentMethod.MADA) }

    // Card Form Inputs
    var cardNumber by remember { mutableStateOf("4210 8892 3410 7721") }
    var cardExpiry by remember { mutableStateOf("08/29") }
    var cardCvv by remember { mutableStateOf("482") }
    var cardHolderName by remember { mutableStateOf("فهد محمد القحطاني") }

    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastPaymentSummary by remember { mutableStateOf("") }

    val totalAmountSar = if (mode == PaymentModalMode.TOP_UP) selectedTopUpPackage.priceSar else selectedPlan.priceSar
    val vatAmountSar = totalAmountSar * 0.15 / 1.15
    val subtotalSar = totalAmountSar - vatAmountSar

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("saudi_payment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (mode == PaymentModalMode.TOP_UP) "شحن محفظة النقاط الذكية 💳" else "ترقية باقة الاشتراك 👑",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "بوابة دفع سعودية آمنة ومعتمدة (مدى، Apple Pay، stc pay)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // 1. Selection Section (Packages or Plans)
            if (mode == PaymentModalMode.TOP_UP) {
                Text(
                    text = "اختر باقة شحن النقاط الذكية:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                PaymentDefaults.TOP_UP_PACKAGES.forEach { pkg ->
                    val isSelected = selectedTopUpPackage.id == pkg.id
                    Surface(
                        onClick = { selectedTopUpPackage = pkg },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedTopUpPackage = pkg },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${pkg.points} نقطة ذكية",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (pkg.bonusBadge != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = SaudiGold.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = pkg.bonusBadge,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SaudiGoldDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "صالحة لتوليد المستندات والمحادثات لجميع الوكلاء",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${pkg.priceSar.toInt()} ر.س",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "اختر باقة الاشتراك المناسبة:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                PaymentDefaults.SUBSCRIPTION_PLANS.filter { it.priceSar > 0 }.forEach { plan ->
                    val isSelected = selectedPlan.id == plan.id
                    Surface(
                        onClick = { selectedPlan = plan },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPlan = plan },
                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                )
                                Column {
                                    Text(text = plan.titleAr, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = plan.periodAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text(
                                text = "${plan.priceSar.toInt()} ر.س",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }

            // 2. Select Saudi Payment Method
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "طريقة الدفع المعتمدة:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SaudiPaymentMethod.entries.take(4).forEach { method ->
                    val isSelected = selectedPaymentMethod == method
                    Surface(
                        onClick = { selectedPaymentMethod = method },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(method.badgeColorHex).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(method.badgeColorHex)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("payment_method_${method.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(method.badgeColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (method) {
                                        SaudiPaymentMethod.MADA -> Icons.Default.CreditCard
                                        SaudiPaymentMethod.APPLE_PAY -> Icons.Default.Payment
                                        SaudiPaymentMethod.STC_PAY -> Icons.Default.AccountBalanceWallet
                                        else -> Icons.Default.CreditCard
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (method) {
                                    SaudiPaymentMethod.MADA -> "مدى"
                                    SaudiPaymentMethod.APPLE_PAY -> "Apple"
                                    SaudiPaymentMethod.STC_PAY -> "stc pay"
                                    else -> "بطاقة"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 3. Card Details Input if Card/Mada selected
            if (selectedPaymentMethod == SaudiPaymentMethod.MADA || selectedPaymentMethod == SaudiPaymentMethod.CREDIT_CARD) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("رقم بطاقة مدى / الائتمان") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = EmeraldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("card_number_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it },
                                label = { Text("الصلاحية MM/YY") },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = { cardCvv = it },
                                label = { Text("رمز الأمان CVV") },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = cardHolderName,
                            onValueChange = { cardHolderName = it },
                            label = { Text("اسم حامل البطاقة (كما هو مدون عليها)") },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(selectedPaymentMethod.badgeColorHex).copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(selectedPaymentMethod.badgeColorHex))
                        Text(
                            text = "سيتم إتمام العملية بنقرة واحدة وآمنة عبر ${selectedPaymentMethod.titleAr}.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 4. Tax and Subtotal Breakdown (ZATCA compliance)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المبلغ الخاضع للضريبة:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%.2f ر.س", subtotalSar), fontSize = 11.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ضريبة القيمة المضافة (15% ZATCA):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%.2f ر.س", vatAmountSar), fontSize = 11.sp)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الإجمالي الكلي المستحق:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(String.format("%.2f ر.س", totalAmountSar), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldPrimary)
                    }
                }
            }

            // 5. Pay Now Button
            Button(
                onClick = {
                    isProcessing = true
                    coroutineScope.launch {
                        delay(1200) // Simulate Saudi Payments / Mada verification
                        val cardLast4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else "4210"

                        if (mode == PaymentModalMode.TOP_UP) {
                            onProcessTopUp(selectedTopUpPackage, selectedPaymentMethod, cardLast4)
                            lastPaymentSummary = "تم شحن ${selectedTopUpPackage.points} نقطة بنجاح بمبلغ ${selectedTopUpPackage.priceSar} ر.س"
                            onPaymentSuccess(selectedTopUpPackage.priceSar, selectedTopUpPackage.points, "شحن رصيد المحفظة")
                        } else {
                            onProcessUpgrade(selectedPlan, selectedPaymentMethod, cardLast4)
                            lastPaymentSummary = "تمت ترقية الاشتراك إلى ${selectedPlan.titleAr} بنجاح"
                            onPaymentSuccess(selectedPlan.priceSar, 5000, selectedPlan.titleAr)
                        }

                        isProcessing = false
                        showSuccessDialog = true
                    }
                },
                enabled = !isProcessing,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_payment_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("جاري معالجة الدفع عبر شبكة مدى...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تأكيد ودفع ${totalAmountSar.toInt()} ر.س الآن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    if (showSuccessDialog) {
        Dialog(onDismissRequest = {
            showSuccessDialog = false
            onDismiss()
        }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(38.dp))
                    }

                    Text(
                        text = "تمت عملية الدفع بنجاح! 🎉",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = lastPaymentSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رقم العملية المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("TXN-ZHN-${(10000..99999).random()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("العودة إلى صفحة المزيد", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
