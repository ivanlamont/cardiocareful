package com.explorova.cardiocareful.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.explorova.cardiocareful.R
import com.explorova.cardiocareful.engine.HapticPatterns
import com.explorova.cardiocareful.theme.cardioCarefulTheme

@Composable
fun HapticPatternScreen(
    currentPattern: String,
    onPatternSelected: (String) -> Unit,
    onPreview: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            text = stringResource(R.string.haptic_patterns),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // List of patterns
        HapticPatterns.getAllPatterns().forEach { patternType ->
            Button(
                onClick = {
                    onPatternSelected(patternType.name)
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 4.dp),
            ) {
                Text(
                    text = "${patternType.displayName}" +
                        if (patternType.name == currentPattern) " ✓" else ""
                )
            }
        }

        // Preview Button
        Button(
            onClick = {
                onPreview(currentPattern)
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.preview))
        }

        // Close Button
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            Text(stringResource(R.string.done))
        }
    }
}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true,
)
@Composable
fun HapticPatternScreenPreview() {
    cardioCarefulTheme {
        HapticPatternScreen(
            currentPattern = "SHORT",
            onPatternSelected = {},
            onPreview = {},
            onClose = {},
        )
    }
}
