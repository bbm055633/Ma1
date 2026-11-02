package com.example.ui.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AgentDataDefaults
import com.example.domain.models.ProductServiceItem
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.generateGradientForColor
import com.example.ui.util.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.theme.zheenCardStyle

/**
 * Samsung One UI 9 - Products & Smart Documents Screen (مركز المنتجات والوثائق الذكية)
 *
 * Design:
 * - One UI 9 Reachability Header with 56x56 circular icon
 * - Liquid glass search pill container
 * - White 24px radius cards, soft shadow, 56x56 colorful circular icon on right with dynamic gradients
 * - 100% functional fidelity preserved
 */
@Composable
fun ProductsScreen(
    viewModel: MainViewModel,
    onNavigate: (ActiveScreen) -> Unit
) {
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            AgentDataDefaults.PRODUCTS
        } else {
            AgentDataDefaults.PRODUCTS.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Header Row: Screen Title with small search icon button beside it
            AnimatedVisibility(
                visible = !isSearchExpanded,
                enter = fadeIn(animationSpec = tween(180)),
                exit = fadeOut(animationSpec = tween(120))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "Products" else "المنتجات",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isEnglish) "Templates, feasibility studies & smart contracts" else "نماذج ودراسات وعقود ذكية جاهزة للتوليد",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Small search icon button beside title
                    Surface(
                        onClick = { isSearchExpanded = true },
                        shape = CircleShape,
                        color = userPrimaryColor.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, userPrimaryColor.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("product_search_toggle_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = if (isEnglish) "Search" else "بحث",
                                tint = userPrimaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Expandable Search Bar with smooth transition, text field, and close button
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = fadeIn(animationSpec = tween(220)) + expandVertically(animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(160)) + shrinkVertically(animationSpec = tween(160))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isEnglish) "Search products, documents..." else "ابحث في المنتجات والوثائق...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = userPrimaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = if (isEnglish) "Clear" else "مسح",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = userPrimaryColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )

                    // Close button that collapses the search bar back to icon
                    Surface(
                        onClick = {
                            isSearchExpanded = false
                            searchQuery = ""
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("product_search_close_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isEnglish) "Close search" else "إغلاق البحث",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Products Grid: One UI 9 White 24px Radius Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredProducts.isEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, userPrimaryColor.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = userPrimaryColor,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isEnglish) "No products matching '$searchQuery'" else "لا توجد وثائق مطابقة لـ '$searchQuery'",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isEnglish) "Try searching for business plan, invoice, or legal kit" else "جرّب البحث عن دراسة جدوى، هوية تجارية، أو صياغة عقود",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(filteredProducts) { product ->
                        OneUi9ProductCard(
                            product = product,
                            primaryColor = userPrimaryColor,
                            isEnglish = isEnglish,
                            onClick = { onNavigate(ActiveScreen.ProductGenerator(product.id)) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Samsung One UI 9 Product Item Card:
 * - White 24px radius, soft shadow
 * - 56x56 circular colorful icon on top-right with dynamic theme gradient
 * - Title Bold 14sp, Description Regular 12sp gray
 * - Action pill "توليد بالذكاء"
 */
@Composable
fun OneUi9ProductCard(
    product: ProductServiceItem,
    primaryColor: Color,
    isEnglish: Boolean = false,
    onClick: () -> Unit
) {
    val icon = when (product.iconName) {
        "Palette" -> Icons.Default.Palette
        "Campaign" -> Icons.Default.Campaign
        "QrCode" -> Icons.Default.QrCode2
        "FlightTakeoff" -> Icons.Default.FlightTakeoff
        "Business" -> Icons.Default.Business
        "Apartment" -> Icons.Default.Apartment
        "CalendarMonth" -> Icons.Default.CalendarMonth
        "Assignment" -> Icons.AutoMirrored.Filled.Assignment
        "TrendingUp" -> Icons.AutoMirrored.Filled.TrendingUp
        "Hotel" -> Icons.Default.Hotel
        "Description" -> Icons.Default.Description
        "Receipt" -> Icons.AutoMirrored.Filled.ReceiptLong
        else -> Icons.Default.AutoAwesome
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .zheenCardStyle(RoundedCornerShape(24.dp), primaryColor)
            .clickable { onClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 56x56 Circular Colorful Icon with Dynamic Gradient
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(generateGradientForColor(primaryColor, product.title.length)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Text(
                            text = product.localizedCategory(isEnglish),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = product.localizedTitle(isEnglish),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.localizedDescription(isEnglish),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = primaryColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "AI Generate" else "توليد بالذكاء",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = if (isEnglish) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
