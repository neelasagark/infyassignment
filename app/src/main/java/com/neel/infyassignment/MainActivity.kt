package com.neel.infyassignment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.neel.infyassignment.domain.usecase.ClearHistoricalPoisUseCase
import com.neel.infyassignment.domain.usecase.GetCurrentPoisUseCase
import com.neel.infyassignment.domain.usecase.GetHistoricalPoisUseCase
import com.neel.infyassignment.ui.theme.InfyassignmentTheme
import com.neel.shared.model.PoiCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 * In a real project I would normally follow an MVVM pattern:
 * - ViewModel injected with use cases and repositories.
 * - Jetpack Compose screen that only talks to the ViewModel.
 *
 * For this assignment the UI is not important, so i kept it simple:
 * - Hilt injects the use cases directly into [MainActivity].
 * - The Activity collects the POI flows and prints them to Logcat.
 *
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getCurrentPoisUseCase: GetCurrentPoisUseCase

    @Inject
    lateinit var getHistoricalPoisUseCase: GetHistoricalPoisUseCase

    @Inject
    lateinit var clearHistoricalPoisUseCase: ClearHistoricalPoisUseCase

    private val logTag: String = "NearbyPoiAssignmentApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Log current POIs (no filter).
        lifecycleScope.launch {
            getCurrentPoisUseCase().collect { currentPois ->
                Log.d(logTag, "Current POIs: $currentPois")
            }
        }

        // Log historical POIs (full history).
        lifecycleScope.launch {
            getHistoricalPoisUseCase().collect { historicalPois ->
                Log.d(logTag, "Historical POIs: $historicalPois")
            }
        }

        // Log current POIs only for RESTAURANT.
        lifecycleScope.launch {
            getCurrentPoisUseCase(PoiCategory.RESTAURANT).collect { restaurantPois ->
                Log.d(logTag, "Current RESTAURANT POIs: $restaurantPois")
            }
        }

        // Log historical POIs only for CHARGING_STATION.
        lifecycleScope.launch {
            getHistoricalPoisUseCase(PoiCategory.CHARGING_STATION).collect { chargingPois ->
                Log.d(logTag, "Historical CHARGING_STATION POIs: $chargingPois")
            }
        }

        setContent {
            InfyassignmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PoiDemoText(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun PoiDemoText(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Nearby POI demo is running.\nCheck Logcat for output.",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun PoiDemoPreview() {
    InfyassignmentTheme {
        PoiDemoText()
    }
}
