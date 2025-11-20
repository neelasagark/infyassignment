package com.neel.infyassignment.ui.pois.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Basic POIs screen to show the flow.
 */
@Composable
fun PoisScreen(
    viewModel: PoisViewModel = hiltViewModel(),
) {
    val currentPoisList by viewModel.currentPois.collectAsState()

    val currentPoisCount: Int = currentPoisList.size

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Current places: $currentPoisCount",
                style = MaterialTheme.typography.titleMedium,
            )

            Button(
                onClick = {
                    viewModel.loadHistory()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Load history once")
            }
        }
    }
}
