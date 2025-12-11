package com.project.wirebarley_android.models

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyApiResponse(
    val success: Boolean,
    val terms: String? = null,
    val privacy: String? = null,
    val timestamp: Long? = null,
    val source: String? = null,
    val quotes: Map<String, Double>? = null
)