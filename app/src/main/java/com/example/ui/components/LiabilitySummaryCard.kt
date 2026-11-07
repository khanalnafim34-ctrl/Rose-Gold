package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinancialSummary
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter

@Composable
fun LiabilitySummaryCard(
  summary: FinancialSummary,
  isBengali: Boolean,
  isBalanceHidden: Boolean,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("pinned_liability_summary_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(DebtRed.copy(alpha = 0.35f))
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Title with Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .background(DebtRedLight, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Info,
              contentDescription = null,
              tint = DebtRed,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isBengali) "আমার মোট দায় (My Total Liabilities)" else "My Total Liabilities",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = DebtRedLight
        ) {
          Text(
            text = CurrencyFormatter.format(summary.totalLiabilities, isBengali, isBalanceHidden),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DebtRedDark,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(12.dp))

      // 5 Data points:
      // 1. Total loan amount ever received
      LiabilityRowItem(
        label = if (isBengali) "সর্বমোট ঋণ গ্রহণ" else "Total Loan Received",
        value = CurrencyFormatter.format(summary.totalLoanReceived, isBengali, isBalanceHidden),
        valueColor = MaterialTheme.colorScheme.onSurface
      )

      // 2. Total loan amount repaid
      LiabilityRowItem(
        label = if (isBengali) "সর্বমোট ঋণ পরিশোধ" else "Total Loan Repaid",
        value = CurrencyFormatter.format(summary.totalLoanRepaid, isBengali, isBalanceHidden),
        valueColor = MoneyGreenDark
      )

      // 3. Remaining personal loan
      LiabilityRowItem(
        label = if (isBengali) "অবশিষ্ট ব্যক্তিগত ঋণ" else "Remaining Personal Loan",
        value = CurrencyFormatter.format(summary.remainingPersonalLoans, isBengali, isBalanceHidden),
        valueColor = DebtRed,
        isBold = true
      )

      // 4. Total outstanding shop dues
      LiabilityRowItem(
        label = if (isBengali) "দোকানের মোট বাকি" else "Total Shop Dues",
        value = CurrencyFormatter.format(summary.remainingShopDues, isBengali, isBalanceHidden),
        valueColor = DebtRed,
        isBold = true
      )

      Spacer(modifier = Modifier.height(4.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(8.dp))

      // 5. Total Current Liabilities
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isBengali) "বর্তমান সর্বমোট দায়:" else "Total Current Liabilities:",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = DebtRedDark
        )
        Text(
          text = CurrencyFormatter.format(summary.totalLiabilities, isBengali, isBalanceHidden),
          fontSize = 17.sp,
          fontWeight = FontWeight.ExtraBold,
          color = DebtRedDark
        )
      }
    }
  }
}

@Composable
private fun LiabilityRowItem(
  label: String,
  value: String,
  valueColor: Color,
  isBold: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
      color = valueColor
    )
  }
}
