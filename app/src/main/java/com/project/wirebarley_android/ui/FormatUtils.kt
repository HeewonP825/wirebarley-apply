package com.project.wirebarley_android.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatUtils {
    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    private val df = DecimalFormat("#,##0.00", symbols)

    fun formatAmount(value: Double): String = df.format(value)
}