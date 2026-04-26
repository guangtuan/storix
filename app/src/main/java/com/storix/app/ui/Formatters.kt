package com.storix.app.ui

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

object Formatters {
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val monthDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M月d日")

    fun formatCurrency(amount: Double, currencyCode: String): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE)
        runCatching {
            formatter.currency = Currency.getInstance(currencyCode.uppercase(Locale.ROOT))
        }
        return formatter.format(amount)
    }

    fun formatDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(zoneId)
            .toLocalDate()
            .format(dateFormatter)
    }

    fun formatMonthDay(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(zoneId)
            .toLocalDate()
            .format(monthDayFormatter)
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

    fun formatHoldingPeriod(startEpochMillis: Long, endEpochMillis: Long = System.currentTimeMillis()): String {
        val startDate = Instant.ofEpochMilli(startEpochMillis).atZone(zoneId).toLocalDate()
        val endDate = Instant.ofEpochMilli(endEpochMillis).atZone(zoneId).toLocalDate()
        val normalizedPeriod = if (endDate.isBefore(startDate)) {
            Period.ZERO
        } else {
            Period.between(startDate, endDate)
        }

        val parts = buildList {
            if (normalizedPeriod.years > 0) add("${normalizedPeriod.years}年")
            if (normalizedPeriod.months > 0) add("${normalizedPeriod.months}月")
            if (normalizedPeriod.days > 0) add("${normalizedPeriod.days}天")
        }
        return if (parts.isEmpty()) "0天" else parts.joinToString("")
    }
}
