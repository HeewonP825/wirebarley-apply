package com.project.wirebarley_android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.wirebarley_android.data.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CurrencyRepository = CurrencyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(loading = true))
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        fetchRates()
    }

    fun fetchRates() {
        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
        viewModelScope.launch {
            val res = repository.fetchLiveRates()
            if (res.isSuccess) {
                val body = res.getOrNull()
                _uiState.value = MainUiState(loading = false, ratesResponse = body)
            } else {
                _uiState.value = MainUiState(loading = false, errorMessage = res.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun getRateFor(currency: String): Double? {
        val quotes = _uiState.value.ratesResponse?.quotes ?: return null
        return quotes["USD$currency"]
    }
}