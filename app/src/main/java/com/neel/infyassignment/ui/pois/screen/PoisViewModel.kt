package com.neel.infyassignment.ui.pois.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neel.infyassignment.domain.usecase.ClearHistoricalPoisUseCase
import com.neel.infyassignment.domain.usecase.GetCurrentPoisUseCase
import com.neel.infyassignment.domain.usecase.GetHistoricalPoisUseCase
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the POIs screen.
 *
 * It exposes the current POIs, the history, and the selected category.
 */
@HiltViewModel
class PoisViewModel @Inject constructor(
    private val getCurrentPoisUseCase: GetCurrentPoisUseCase,
    private val getHistoricalPoisUseCase: GetHistoricalPoisUseCase,
    private val clearHistoricalPoisUseCase: ClearHistoricalPoisUseCase,
) : ViewModel() {

    private val selectedCategoryState: MutableStateFlow<PoiCategory?> =
        MutableStateFlow(null)

    private val currentPoisState: MutableStateFlow<List<Poi>> =
        MutableStateFlow(emptyList())

    private val historicalPoisState: MutableStateFlow<List<Poi>> =
        MutableStateFlow(emptyList())

    private val isHistoryLoadingState: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    val selectedCategory: StateFlow<PoiCategory?> = selectedCategoryState.asStateFlow()
    val currentPois: StateFlow<List<Poi>> = currentPoisState.asStateFlow()
    val historicalPois: StateFlow<List<Poi>> = historicalPoisState.asStateFlow()
    val isHistoryLoading: StateFlow<Boolean> = isHistoryLoadingState.asStateFlow()

    init {
        observeCurrentPois()
    }

    private fun observeCurrentPois() {
        viewModelScope.launch {
            selectedCategoryState
                .flatMapLatest { currentCategory ->
                    getCurrentPoisUseCase(currentCategory)
                }
                .collect { poiList ->
                    currentPoisState.value = poiList
                }
        }
    }

    /**
     * Called when the user picks a category.
     * Pass null to show all categories.
     */
    fun onCategorySelected(category: PoiCategory?) {
        selectedCategoryState.value = category
    }

    /**
     * Loads POI history for the currently selected category.
     */
    fun loadHistory() {
        viewModelScope.launch {
            val categoryFilter: PoiCategory? = selectedCategoryState.value
            val historyList: List<Poi> =
                getHistoricalPoisUseCase(categoryFilter).first()

            historicalPoisState.value = historyList
        }
    }

    /**
     * Clears stored history and updates the state.
     */
    fun clearHistory() {
        viewModelScope.launch {
            clearHistoricalPoisUseCase()
            historicalPoisState.value = emptyList()
        }
    }
}
