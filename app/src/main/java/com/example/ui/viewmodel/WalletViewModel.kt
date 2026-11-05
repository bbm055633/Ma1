package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.domain.models.PaymentDefaults
import com.example.domain.models.PaymentTransaction
import com.example.domain.models.SaudiPaymentMethod
import com.example.domain.models.SubscriptionPlan
import com.example.domain.models.TopUpPackage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val _walletBalanceSar = MutableStateFlow(450.00)
    val walletBalanceSar: StateFlow<Double> = _walletBalanceSar.asStateFlow()

    private val _smartPoints = MutableStateFlow(8450)
    val smartPoints: StateFlow<Int> = _smartPoints.asStateFlow()

    private val _currentSubscription = MutableStateFlow("باقة الأعمال Enterprise Pro")
    val currentSubscription: StateFlow<String> = _currentSubscription.asStateFlow()

    private val _paymentTransactions = MutableStateFlow(PaymentDefaults.INITIAL_TRANSACTIONS)
    val paymentTransactions: StateFlow<List<PaymentTransaction>> = _paymentTransactions.asStateFlow()

    fun processTopUp(packageItem: TopUpPackage, method: SaudiPaymentMethod, cardLast4: String = "4210") {
        _walletBalanceSar.value += packageItem.priceSar
        _smartPoints.value += packageItem.points
        val randomNum = (10000..99999).random()
        val newTxn = PaymentTransaction(
            id = "TXN-ZHN-$randomNum",
            title = "شحن رصيد المحفظة (${packageItem.points} نقطة)",
            amountSar = packageItem.priceSar,
            pointsAdded = packageItem.points,
            paymentMethodName = "${method.titleAr} •••• $cardLast4",
            dateFormatted = "اليوم، الآن"
        )
        _paymentTransactions.value = listOf(newTxn) + _paymentTransactions.value
    }

    fun processSubscriptionUpgrade(plan: SubscriptionPlan, method: SaudiPaymentMethod, cardLast4: String = "4210") {
        _currentSubscription.value = plan.titleAr
        _smartPoints.value += 5000
        val randomNum = (10000..99999).random()
        val newTxn = PaymentTransaction(
            id = "TXN-ZHN-$randomNum",
            title = "ترقية الاشتراك: ${plan.titleAr}",
            amountSar = plan.priceSar,
            pointsAdded = 5000,
            paymentMethodName = "${method.titleAr} •••• $cardLast4",
            dateFormatted = "اليوم، الآن"
        )
        _paymentTransactions.value = listOf(newTxn) + _paymentTransactions.value
    }
}
