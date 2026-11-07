package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun QuickActionButtons(
  isBengali: Boolean,
  onAddIncome: () -> Unit,
  onAddExpense: () -> Unit,
  onAddReceivable: () -> Unit,
  onAddLoan: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // 1. Add Income (আয় যোগ)
    ActionButtonItem(
      label = if (isBengali) "আয় যোগ" else "Add Income",
      icon = Icons.Filled.ArrowDownward,
      bgColor = MoneyGreenLight,
      iconTint = MoneyGreenDark,
      testTag = "btn_quick_add_income",
      onClick = onAddIncome,
      modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(8.dp))

    // 2. Add Expense (খরচ যোগ)
    ActionButtonItem(
      label = if (isBengali) "খরচ যোগ" else "Add Expense",
      icon = Icons.Filled.ArrowUpward,
      bgColor = DebtRedLight,
      iconTint = DebtRedDark,
      testTag = "btn_quick_add_expense",
      onClick = onAddExpense,
      modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(8.dp))

    // 3. Add Receivable (পাওনা যোগ)
    ActionButtonItem(
      label = if (isBengali) "পাওনা যোগ" else "Add Receivable",
      icon = Icons.Filled.Handshake,
      bgColor = Color(0xFFF3E8FF),
      iconTint = Color(0xFF7E22CE),
      testTag = "btn_quick_add_receivable",
      onClick = onAddReceivable,
      modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(8.dp))

    // 4. Add Loan (ঋণ যোগ)
    ActionButtonItem(
      label = if (isBengali) "ঋণ যোগ" else "Add Loan",
      icon = Icons.Filled.ReceiptLong,
      bgColor = Color(0xFFDBEAFE),
      iconTint = Color(0xFF1E3A8A),
      testTag = "btn_quick_add_loan",
      onClick = onAddLoan,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun ActionButtonItem(
  label: String,
  icon: ImageVector,
  bgColor: Color,
  iconTint: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .background(bgColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconTint,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      maxLines = 1
    )
  }
}
