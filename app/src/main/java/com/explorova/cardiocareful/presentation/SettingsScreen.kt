package com.explorova.cardiocareful.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TextField
import com.explorova.cardiocareful.R
import com.explorova.cardiocareful.theme.cardioCarefulTheme

@Composable
fun SettingsScreen(
    minHr: Int,
    maxHr: Int,
    alertsEnabled: Boolean,
    onMinHrChange: (Int) -> Unit,
    onMaxHrChange: (Int) -> Unit,
    onAlertsEnabledChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val minHrState = remember { mutableStateOf(minHr.toString()) }
    val maxHrState = remember { mutableStateOf(maxHr.toString()) }
    val alertsEnabledState = remember { mutableStateOf(alertsEnabled) }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Title
        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Min HR Input
        Text(text = stringResource(R.string.min_heart_rate))
        TextField(
            value = minHrState.value,
            onValueChange = {
                minHrState.value = it
                errorMessage.value = ""
            },
            modifier = Modifier.fillMaxWidth(0.8f),
        )

        // Max HR Input
        Text(text = stringResource(R.string.max_heart_rate))
        TextField(
            value = maxHrState.value,
            onValueChange = {
                maxHrState.value = it
                errorMessage.value = ""
            },
            modifier = Modifier.fillMaxWidth(0.8f),
        )

        // Error message
        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        // Alerts Enable/Disable Button
        Button(
            onClick = {
                alertsEnabledState.value = !alertsEnabledState.value
            },
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            Text(
                if (alertsEnabledState.value) {
                    stringResource(R.string.alerts_enabled)
                } else {
                    stringResource(R.string.alerts_disabled)
                }
            )
        }

        // Save Button
        Button(
            onClick = {
                try {
                    val minHrValue = minHrState.value.toIntOrNull()
                    val maxHrValue = maxHrState.value.toIntOrNull()

                    when {
                        minHrValue == null || maxHrValue == null -> {
                            errorMessage.value = stringResource(R.string.invalid_input)
                        }
                        minHrValue < 20 || minHrValue > 220 -> {
                            errorMessage.value = stringResource(R.string.min_hr_range_error)
                        }
                        maxHrValue < 20 || maxHrValue > 220 -> {
                            errorMessage.value = stringResource(R.string.max_hr_range_error)
                        }
                        minHrValue > maxHrValue -> {
                            errorMessage.value = stringResource(R.string.min_max_error)
                        }
                        else -> {
                            onMinHrChange(minHrValue)
                            onMaxHrChange(maxHrValue)
                            onAlertsEnabledChange(alertsEnabledState.value)
                            onSave()
                        }
                    }
                } catch (e: Exception) {
                    errorMessage.value = stringResource(R.string.settings_error)
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            Text(stringResource(R.string.save))
        }

        // Cancel Button
        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true,
)
@Composable
fun SettingsScreenPreview() {
    cardioCarefulTheme {
        SettingsScreen(
            minHr = 60,
            maxHr = 100,
            alertsEnabled = true,
            onMinHrChange = {},
            onMaxHrChange = {},
            onAlertsEnabledChange = {},
            onSave = {},
            onCancel = {},
        )
    }
}
