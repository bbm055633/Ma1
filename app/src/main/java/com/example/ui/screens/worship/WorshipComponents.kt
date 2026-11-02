package com.example.ui.screens.worship

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Error
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.Shapes2027

/**
 * شريط العد التنازلي للصلاة القادمة
 */
@Composable
fun NextPrayerCountdownBanner(
    prayerTimes: PrayerTimes,
    modifier: Modifier = Modifier
) {
    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        shape = Shapes2027.Large,
        color = surfaceColor,
        shadowElevation = if (isLiveSky) 0.dp else 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("next_prayer_countdown_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "الصلاة القادمة: صلاة ${prayerTimes.nextPrayerName ?: "الظهر"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "موعد الأذان: ${prayerTimes.nextPrayerTime ?: "--:--"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = Shapes2027.Pill,
                color = primaryColor
            ) {
                Text(
                    text = "متبقي ${prayerTimes.minutesUntilNext} دقيقة",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * شريط التبويب العلوي لرحلة الإيمان (صلاتي، صيامي، قرآني، أذكاري)
 */
@Composable
fun FaithJourneyTopTabBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Pair("صلاتي", "🕌"),
        Pair("صيامي", "🌙"),
        Pair("قرآني", "📖"),
        Pair("أذكاري", "📿")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, (title, icon) ->
            val isSelected = selectedTab == index
            val primaryColor = MaterialTheme.colorScheme.primary

            Surface(
                onClick = { onTabSelect(index) },
                shape = Shapes2027.Pill,
                color = if (isSelected) primaryColor else Color.Transparent,
                modifier = Modifier.height(38.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(icon, fontSize = 14.sp)
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * نافذة حوار بوصلة القبلة المتكاملة والدقيقة
 */
@Composable
fun QiblaCompassDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    var hasLocationPermission by remember {
        mutableStateOf(
            android.content.pm.PackageManager.PERMISSION_GRANTED ==
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
        )
    }

    var isLoadingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var userLat by remember { mutableStateOf(24.7136.toDouble()) } // Default Riyadh
    var userLon by remember { mutableStateOf(46.6753.toDouble()) }
    var locationName by remember { mutableStateOf("الرياض، المملكة العربية السعودية") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            isLoadingLocation = true
            locationError = null
            fetchUserLocation(context) { lat, lon, name, err ->
                isLoadingLocation = false
                if (err != null) {
                    locationError = err
                } else {
                    userLat = lat
                    userLon = lon
                    locationName = name
                }
            }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            isLoadingLocation = true
            locationError = null
            fetchUserLocation(context) { lat, lon, name, err ->
                isLoadingLocation = false
                if (err != null) {
                    locationError = err
                } else {
                    userLat = lat
                    userLon = lon
                    locationName = name
                }
            }
        }
    }

    // Calculate Qibla Bearing from user location to Kaaba (21.4225° N, 39.8262° E)
    val kaabaLat = 21.4225
    val kaabaLon = 39.8262

    val qiblaBearing = remember(userLat, userLon) {
        calculateQiblaBearing(userLat, userLon, kaabaLat, kaabaLon)
    }

    val distanceKm = remember(userLat, userLon) {
        calculateDistanceKm(userLat, userLon, kaabaLat, kaabaLon)
    }

    // Compass sensor listener for device rotation
    var azimuth by remember { mutableStateOf(0f) }
    DisposableEffect(context) {
        val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as? android.hardware.SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD)

        var gravity: FloatArray? = null
        var geomagnetic: FloatArray? = null

        val listener = object : android.hardware.SensorEventListener {
            override fun onSensorChanged(event: android.hardware.SensorEvent) {
                if (event.sensor.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
                    gravity = event.values
                } else if (event.sensor.type == android.hardware.Sensor.TYPE_MAGNETIC_FIELD) {
                    geomagnetic = event.values
                }
                if (gravity != null && geomagnetic != null) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (android.hardware.SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        android.hardware.SensorManager.getOrientation(r, orientation)
                        val rad = orientation[0]
                        var deg = Math.toDegrees(rad.toDouble()).toFloat()
                        deg = (deg + 360) % 360
                        azimuth = deg
                    }
                }
            }
            override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
        }

        sensorManager?.registerListener(listener, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_UI)
        sensorManager?.registerListener(listener, magnetometer, android.hardware.SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val needleRotation = qiblaBearing - azimuth

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = Shapes2027.Large,
            color = if (isDark) Color(0xFF141923) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اتجاه القبلة الشريفة 🕋",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                if (!hasLocationPermission) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = SaudiGold, modifier = Modifier.size(48.dp))
                        Text(
                            text = "نحتاج إذن الوصول إلى موقعك الجغرافي لتحديد اتجاه القبلة والمسافة بدقة.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = Shapes2027.Pill
                        ) {
                            Text("منح إذن الموقع", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (isLoadingLocation) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "جاري تحديد موقعك الجغرافي...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (locationError != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(42.dp))
                        Text(
                            text = locationError ?: "تعذر تحديد الموقع",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                isLoadingLocation = true
                                locationError = null
                                fetchUserLocation(context) { lat, lon, name, err ->
                                    isLoadingLocation = false
                                    if (err != null) locationError = err
                                    else {
                                        userLat = lat
                                        userLon = lon
                                        locationName = name
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = Shapes2027.Pill
                        ) {
                            Text("إعادة المحاولة", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Compass Visual with Needle rotation
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(2.dp, SaudiGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Compass Ring Marks
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(needleRotation),
                            contentAlignment = Alignment.Center
                        ) {
                            // Kaaba / Needle Indicator pointing towards Qibla
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = SaudiGold,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🕋", fontSize = 16.sp)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(45.dp)
                                        .background(SaudiGold)
                                )
                            }
                        }

                        // Center Dot
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        ) {}
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "الزاوية: ${qiblaBearing.toInt()}° | المسافة: ${distanceKm.toInt()} كم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "الموقع: $locationName",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "وجّه هاتفك أفقياً حتى تتطابق أيقونة الكعبة مع أعلى الدائرة",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun calculateQiblaBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val phi1 = Math.toRadians(lat1)
    val phi2 = Math.toRadians(lat2)
    val deltaLambda = Math.toRadians(lon2 - lon1)

    val y = kotlin.math.sin(deltaLambda) * kotlin.math.cos(phi2)
    val x = kotlin.math.cos(phi1) * kotlin.math.sin(phi2) -
            kotlin.math.sin(phi1) * kotlin.math.cos(phi2) * kotlin.math.cos(deltaLambda)

    var theta = kotlin.math.atan2(y, x)
    theta = Math.toDegrees(theta)
    return ((theta + 360) % 360).toFloat()
}

private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadiusKm * c
}

private fun fetchUserLocation(
    context: android.content.Context,
    onResult: (lat: Double, lon: Double, name: String, error: String?) -> Unit
) {
    try {
        val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as? android.location.LocationManager
        val isGpsEnabled = locationManager?.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) == true

        if (!isGpsEnabled && !isNetworkEnabled) {
            onResult(24.7136, 46.6753, "الرياض (افتراضي - خدمات الموقع معطلة)", "خدمات الموقع (GPS) معطلة على الجهاز.")
            return
        }

        val fusedClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                try {
                    val geocoder = android.location.Geocoder(context, java.util.Locale("ar"))
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val cityName = addresses?.firstOrNull()?.let { "${it.locality ?: it.subAdminArea ?: ""}, ${it.countryName ?: ""}" } ?: "الموقع الحالي"
                    onResult(lat, lon, cityName, null)
                } catch (_: Exception) {
                    onResult(lat, lon, "الموقع الحالي (${String.format(java.util.Locale.US, "%.2f", lat)}, ${String.format(java.util.Locale.US, "%.2f", lon)})", null)
                }
            } else {
                onResult(24.7136, 46.6753, "الرياض (افتراضي)", "تعذر جلب آخر موقع، تأكد من تفعيل GPS.")
            }
        }.addOnFailureListener { exception: Exception ->
            onResult(24.7136, 46.6753, "الرياض (افتراضي)", "فشل تحديد الموقع: ${exception.localizedMessage}")
        }
    } catch (e: Exception) {
        onResult(24.7136, 46.6753, "الرياض (افتراضي)", "خطأ في الاتصال بخدمات الموقع: ${e.localizedMessage}")
    }
}

/**
 * نافذة تفاصيل إنجازات الالتزام بالعبادات
 */
@Composable
fun AchievementsFullDialog(onDismiss: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = Shapes2027.Large,
            color = if (isDark) Color(0xFF141923) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سجل الإنجازات والالتزام 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AchievementItem(
                        icon = "🕌",
                        title = "المحافظة على صلاة الجماعة",
                        subtitle = "سلسلة متواصلة لمدة 14 يوماً",
                        progress = 1.0f
                    )
                    AchievementItem(
                        icon = "📖",
                        title = "الورد اليومي للقرآن",
                        subtitle = "قراءة حزبين يومياً بانتظام",
                        progress = 0.85f
                    )
                    AchievementItem(
                        icon = "🤲",
                        title = "أذكار الصباح والمساء",
                        subtitle = "إتمام الأذكار بنسبة 95% هذا الشهر",
                        progress = 0.95f
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    title: String,
    subtitle: String,
    progress: Float
) {
    Surface(
        shape = Shapes2027.Medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(Shapes2027.Pill),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }
    }
}

