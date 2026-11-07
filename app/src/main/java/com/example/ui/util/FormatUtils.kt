package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {

  private val englishDecimalFormat = DecimalFormat("#,##,##0.##")

  fun format(
    amount: Double,
    isBengali: Boolean = true,
    hideBalance: Boolean = false
  ): String {
    if (hideBalance) {
      return "৳ ••••••"
    }

    val isNegative = amount < 0
    val absAmount = kotlin.math.abs(amount)
    val formattedNumber = englishDecimalFormat.format(absAmount)

    val finalNumber = if (isBengali) {
      toBengaliDigits(formattedNumber)
    } else {
      formattedNumber
    }

    return if (isNegative) {
      "-৳ $finalNumber"
    } else {
      "৳ $finalNumber"
    }
  }

  fun toBengaliDigits(input: String): String {
    val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val builder = StringBuilder()
    for (ch in input) {
      if (ch in '0'..'9') {
        builder.append(bnDigits[ch - '0'])
      } else {
        builder.append(ch)
      }
    }
    return builder.toString()
  }
}

object DateUtils {

  private val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
  private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
  private val dateTimeFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())

  fun formatDate(timestamp: Long): String {
    return dateFormat.format(Date(timestamp))
  }

  fun formatTime(timestamp: Long): String {
    return timeFormat.format(Date(timestamp))
  }

  fun formatDateTime(timestamp: Long): String {
    return dateTimeFormat.format(Date(timestamp))
  }

  fun isToday(timestamp: Long): Boolean {
    val now = System.currentTimeMillis()
    return isSameDay(timestamp, now)
  }

  fun isYesterday(timestamp: Long): Boolean {
    val yesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
    return isSameDay(timestamp, yesterday)
  }

  private fun isSameDay(t1: Long, t2: Long): Boolean {
    val f = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return f.format(Date(t1)) == f.format(Date(t2))
  }
}
