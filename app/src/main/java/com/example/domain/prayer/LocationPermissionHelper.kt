package com.example.domain.prayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Helper composable that handles runtime location permission requests.
 * Returns true when permission is granted, false otherwise.
 */
@Composable
fun LocationPermissionHelper(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
): Boolean {
    var hasPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasPermission = fineGranted || coarseGranted
            
            if (hasPermission) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    )
    
    LaunchedEffect(Unit) {
        // Check current permission state
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!fine && !coarse) {
            // Request permissions dynamically
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            hasPermission = true
            onPermissionGranted()
        }
    }
    
    return hasPermission
}
