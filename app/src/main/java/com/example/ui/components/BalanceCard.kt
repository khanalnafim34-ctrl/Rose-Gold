package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinancialSummary
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter

@Composable
fun BalanceCard(
  summary: FinancialSummary,
  isBalanceHidden: Boolean,
  isBengali: Boolean,
  onToggleHideBalance: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isNegative = summary.netBalance < 0
  val balanceColor = if (isNegative) DebtRed else MoneyGreen

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("main_balance_card"),
    shape = RoundedCornerShape(24.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(0xFF0F172A),
              Color(0xFF1E293B),
              Color(0xFF1E3A8A)
            )
          )
        )
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header Row: Title & Eye Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(balanceColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isBengali) "বর্তমান নেট ব্যালেন্স (Net Balance)" else "Current Net Balance",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFFCBD5E1)
            )
          }

          IconButton(
            onClick = onToggleHideBalance,
            modifier = Modifier
              .size(36.dp)
              .testTag("toggle_balance_button")
          ) {
            Icon(
              imageVector = if (isBalanceHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
              contentDescription = if (isBalanceHidden) "Show Balance" else "Hide Balance",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large Net Balance Display
        Text(
          text = CurrencyFormatter.format(summary.netBalance, isBengali, isBalanceHidden),
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = if (isBalanceHidden) Color.White else balanceColor,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
        Spacer(modifier = Modifier.height(14.dp))

        // Assets vs Liabilities
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Total Assets
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isBengali) "মোট সম্পদ (Assets)" else "Total Assets",
              fontSize = 12.sp,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = CurrencyFormatter.format(summary.totalAssets, isBengali, isBalanceHidden),
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold,
              color = MoneyGreen
            )
            Text(
              text = if (isBengali) "নগদ + ব্যাংক + মোবাইল" else "Cash + Bank + MFS",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          // Total Liabilities
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
          ) {
            Text(
              text = if (isBengali) "মোট দায় (Liabilities)" else "Total Liabilities",
              fontSize = 12.sp,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = CurrencyFormatter.format(summary.totalLiabilities, isBengali, isBalanceHidden),
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold,
              color = DebtRed
            )
            Text(
              text = if (isBengali) "ব্যক্তিগত ঋণ + দোকানের বাকি" else "Personal Loans + Dues",
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }
        }
      }
    }
  }
}
