package com.project.wirebarley_android.ui

import com.project.wirebarley_android.models.CurrencyApiResponse

data class MainUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val ratesResponse: CurrencyApiResponse? = null
)