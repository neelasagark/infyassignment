package com.neel.infyassignment.ui.pois.screen

import androidx.lifecycle.ViewModel
import com.neel.infyassignment.domain.usecase.ClearHistoricalPoisUseCase
import com.neel.infyassignment.domain.usecase.GetCurrentPoisUseCase
import com.neel.infyassignment.domain.usecase.GetHistoricalPoisUseCase

/**
 * ViewModel for the POIs screen.
 *
 * It talks to the domain layer via useCase
 * to load and update the list of places around the user.
 *
 *
 * This is to keep the screen logic out of the Activity/Fragment and makes it
 * easier to test.
 */
class PoisViewModel(
    private val getCurrentPoisUseCase: GetCurrentPoisUseCase,
    private val getHistoricalPoisUseCase: GetHistoricalPoisUseCase,
    private val clearHistoricalPoisUseCase: ClearHistoricalPoisUseCase,
) : ViewModel() {}