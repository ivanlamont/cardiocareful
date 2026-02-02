package com.explorova.cardiocareful.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.explorova.cardiocareful.R
import com.explorova.cardiocareful.theme.cardioCarefulTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun mainScreen(
    hr: Double,
    availability: DataTypeAvailability,
    enabled: Boolean,
    onButtonClick: () -> Unit,
    permissionState: PermissionState,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        hrLabel(
            hr = hr,
            availability = availability,
        )
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                if (permissionState.status.isGranted) {
                    onButtonClick()
                } else {
                    permissionState.launchPermissionRequest()
                }
            },
        ) {
            val buttonTextId =
                if (enabled) {
                    R.string.stop
                } else {
                    R.string.start
                }
            Text(stringResource(buttonTextId))
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
fun mainScreenPreview() {
    val permissionState =
        object : PermissionState {
            override val permission = "android.permission.ACTIVITY_RECOGNITION"
            override val status: PermissionStatus = PermissionStatus.Granted

            override fun launchPermissionRequest() {}
        }
    cardioCarefulTheme {
        mainScreen(
            hr = 68.0,
            availability = DataTypeAvailability.AVAILABLE,
            enabled = false,
            onButtonClick = {},
            permissionState = permissionState,
        )
    }
}
