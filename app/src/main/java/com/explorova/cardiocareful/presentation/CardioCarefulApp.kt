package com.explorova.cardiocareful.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.explorova.cardiocareful.PERMISSION
import com.explorova.cardiocareful.data.HealthServicesRepository
import com.explorova.cardiocareful.theme.cardioCarefulTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun cardioCarefulApp(healthServicesRepository: HealthServicesRepository) {
    cardioCarefulTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = { TimeText() },
        ) {
            val viewModel: CardioDataViewModel =
                viewModel(
                    factory =
                        CardioDataViewModelFactory(
                            healthServicesRepository = healthServicesRepository,
                        ),
                )

            val enabled by viewModel.enabled.collectAsState()
            val hr by viewModel.hr
            val availability by viewModel.availability
            val uiState by viewModel.uiState

            if (uiState == UiState.Supported) {
                val permissionState =
                    rememberPermissionState(
                        permission = PERMISSION,
                        onPermissionResult = { granted ->
                            if (granted) viewModel.toggleEnabled()
                        },
                    )
                mainScreen(
                    hr = hr,
                    availability = availability,
                    enabled = enabled,
                    onButtonClick = { viewModel.toggleEnabled() },
                    permissionState = permissionState,
                )
            } else if (uiState == UiState.NotSupported) {
                notSupportedScreen()
            }
        }
    }
}
