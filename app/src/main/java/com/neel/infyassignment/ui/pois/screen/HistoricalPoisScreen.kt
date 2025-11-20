package com.neel.infyassignment.ui.pois.screen

import androidx.compose.runtime.Composable

/**
 * Screen host for showing historical POIs using Jetpack Compose.
 *
 * This screen will:
 * get a [PoisViewModel] instance (for example using Hilt),
 * collect historical POIs from the ViewModel,
 * map them to UI models,
 * draw the content with Jetpack Compose.
 *
 * The real UI and state handling will be added later.
 */
class HistoricalPoisScreen {

    /**
     * view model can be injected via hilt.
     */
    @Composable
    fun Content(
        viewModel: PoisViewModel,
    ) {
        // TODO: Observe viewModel and build the UI with Jetpack Compose.
    }
}
