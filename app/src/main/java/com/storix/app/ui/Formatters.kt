package com.storix.app.ui

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

object Formatters {
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun formatCurrency(amount: Double, currencyCode: String): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE)
        runCatching {
            formatter.currency = Currency.getInstance(currencyCode.uppercase(Locale.ROOT))
        }
        return formatter.format(amount)
    }

    fun formatSignedCurrency(amount: Double, currencyCode: String): String {
        val prefix = if (amount > 0) "+" else ""
        return prefix + formatCurrency(amount, currencyCode)
    }

    fun formatDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(zoneId)
            .toLocalDate()
            .format(dateFormatter)
    }

    fun parseDate(value: String): Long? {
        return runCatching {
            LocalDate.parse(value.trim(), dateFormatter)
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }

    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun formatHoldingDays(days: Long): String {
        return if (days >= 365) {
            val years = days / 365
            val remain = days % 365
            "${years}年 ${remain}天"
        } else {
            "${days}天"
        }
    }

    fun formatPercent(value: Double): String {
        val prefix = if (value > 0) "+" else ""
        return prefix + String.format(Locale.US, "%.2f%%", value)
    }
}
