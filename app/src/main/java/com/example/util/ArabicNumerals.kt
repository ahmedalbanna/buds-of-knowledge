package com.example.util

object ArabicNumerals {
    private val englishToArabicMap = mapOf(
        '0' to '٠',
        '1' to '١',
        '2' to '٢',
        '3' to '٣',
        '4' to '٤',
        '5' to '٥',
        '6' to '٦',
        '7' to '٧',
        '8' to '٨',
        '9' to '٩'
    )

    fun convert(input: String): String =
        input.map { char -> englishToArabicMap[char] ?: char }.joinToString("")

    fun formatCurrency(amount: Double, symbol: String = "ر.ي"): String {
        val formatted = String.format("%,.0f", amount)
        return "${convert(formatted)} $symbol"
    }

    fun formatPercent(percent: Int): String {
        return "${convert(percent.toString())}%"
    }

    fun formatCount(count: Int, label: String): String {
        return "${convert(count.toString())} $label"
    }
}
