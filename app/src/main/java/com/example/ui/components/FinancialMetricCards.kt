package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinancialSummary
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter

@Composable
fun FinancialMetricCards(
  summary: FinancialSummary,
  isBengali: Boolean,
  isBalanceHidden: Boolean,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // 2x2 Grid of Summary Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Total Receivables (মোট পাওনা)
      SummaryMetricCard(
        title = if (isBengali) "মোট পাওনা" else "Total Receivables",
        subtitle = if (isBengali) "অন্যরা দেবে" else "Others owe me",
        amount = summary.totalReceivables,
        amountColor = MoneyGreenDark,
        bgColor = MoneyGreenLight,
        icon = Icons.Filled.TrendingUp,
        iconTint = MoneyGreen,
        isBengali = isBengali,
        isBalanceHidden = isBalanceHidden,
        testTag = "card_total_receivables",
        modifier = Modifier.weight(1f)
      )

      // 2. Total Outstanding Debt (মোট দেনা)
      SummaryMetricCard(
        title = if (isBengali) "মোট দেনা" else "Total Debt",
        subtitle = if (isBengali) "আমাকে দিতে হবে" else "Remaining to pay",
        amount = summary.totalLiabilities,
        amountColor = DebtRedDark,
        bgColor = DebtRedLight,
        icon = Icons.Filled.TrendingDown,
        iconTint = DebtRed,
        isBengali = isBengali,
        isBalanceHidden = isBalanceHidden,
        testTag = "card_total_debt",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 3. Total Loan Received (মোট ঋণ নিয়েছি)
      SummaryMetricCard(
        title = if (isBengali) "মোট ঋণ নিয়েছি" else "Total Borrowed",
        subtitle = if (isBengali) "সর্বমোট ধার" else "Historical loan",
        amount = summary.totalLoanReceived,
        amountColor = Color(0xFF1E3A8A),
        bgColor = Color(0xFFEFF6FF),
        icon = Icons.Filled.Download,
        iconTint = Color(0xFF2563EB),
        isBengali = isBengali,
        isBalanceHidden = isBalanceHidden,
        testTag = "card_total_loan_received",
        modifier = Modifier.weight(1f)
      )

      // 4. Total Loan Repaid (মোট ঋণ পরিশোধ করেছি)
      SummaryMetricCard(
        title = if (isBengali) "মোট ঋণ পরিশোধ" else "Total Repaid",
        subtitle = if (isBengali) "পরিশোধ সম্পন্ন" else "Loan paid back",
        amount = summary.totalLoanRepaid,
        amountColor = Color(0xFF047857),
        bgColor = Color(0xFFF0FDF4),
        icon = Icons.Filled.CheckCircle,
        iconTint = Color(0xFF10B981),
        isBengali = isBengali,
        isBalanceHidden = isBalanceHidden,
        testTag = "card_total_loan_repaid",
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun SummaryMetricCard(
  title: String,
  subtitle: String,
  amount: Double,
  amountColor: Color,
  bgColor: Color,
  icon: ImageVector,
  iconTint: Color,
  isBengali: Boolean,
  isBalanceHidden: Boolean,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .background(bgColor, RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = CurrencyFormatter.format(amount, isBengali, isBalanceHidden),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = amountColor
      )

      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
      )
    }
  }
}
