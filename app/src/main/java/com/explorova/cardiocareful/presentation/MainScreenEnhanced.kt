package com.explorova.cardiocareful.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.explorova.cardiocareful.R
import com.explorova.cardiocareful.theme.cardioCarefulTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted

/**
 * Enhanced main screen with animations and improved UI/UX.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun mainScreenEnhanced(
    hr: Double,
    availability: DataTypeAvailability,
    enabled: Boolean,
    onButtonClick: () -> Unit,
    permissionState: PermissionState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val haptics = rememberHapticFeedback(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Monitoring Status Indicator
        FadeInAnimation(visible = enabled) {
            PulsingIndicator(
                isActive = enabled,
                modifier = Modifier.padding(bottom = 8.dp),
                activeColor = androidx.compose.ui.graphics.Color.Green,
                inactiveColor = androidx.compose.ui.graphics.Color.Gray
            )
        }

        // Heart Rate Display with Animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedHeartRateDisplay(
                heartRate = hr,
                targetColor = when {
                    hr > 140 -> androidx.compose.ui.graphics.Color.Red
                    hr > 100 -> androidx.compose.ui.graphics.Color.Yellow
                    else -> androidx.compose.ui.graphics.Color.Green
                },
                isAlertActive = availability == DataTypeAvailability.AVAILABLE
            )
        }

        // Heart Rate Label
        Text(
            text = stringResource(
                R.string.heart_rate_value,
                hr.toInt()
            ),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .alpha(0.8f)
        )

        // Availability Status
        val statusText = when (availability) {
            DataTypeAvailability.AVAILABLE -> "Available"
            DataTypeAvailability.ACQUIRING -> "Acquiring..."
            DataTypeAvailability.UNAVAILABLE -> "Unavailable"
            else -> "Unknown"
        }

        Text(
            text = statusText,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .alpha(0.6f)
        )

        // Start/Stop Button with Haptic Feedback
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                haptics.click()
                if (permissionState.status.isGranted) {
                    onButtonClick()
                } else {
                    haptics.strongFeedback()
                    permissionState.launchPermissionRequest()
                }
            },
        ) {
            val buttonText = if (enabled) {
                stringResource(R.string.stop)
            } else {
                stringResource(R.string.start)
            }
            Text(buttonText)
        }
    }
}

@ExperimentalPermissionsApi
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true,
)
@Composable
fun mainScreenEnhancedPreview() {
    val permissionState =
        object : PermissionState {
            override val permission = "android.permission.BODY_SENSORS"
            override val status: PermissionStatus = PermissionStatus.Granted

            override fun launchPermissionRequest() {}
        }
    cardioCarefulTheme {
        mainScreenEnhanced(
            hr = 75.0,
            availability = DataTypeAvailability.AVAILABLE,
            enabled = true,
            onButtonClick = {},
            permissionState = permissionState,
        )
    }
}
