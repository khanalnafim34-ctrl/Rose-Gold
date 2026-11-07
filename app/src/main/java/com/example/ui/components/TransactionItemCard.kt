package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionType
import com.example.model.UnifiedTransaction
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils

@Composable
fun TransactionItemCard(
  transaction: UnifiedTransaction,
  isBengali: Boolean,
  onDelete: (() -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val (icon, iconTint, iconBg, isPositive) = when (transaction.type) {
    TransactionType.INCOME -> Quad(Icons.Filled.ArrowDownward, MoneyGreenDark, MoneyGreenLight, true)
    TransactionType.EXPENSE -> Quad(Icons.Filled.ArrowUpward, DebtRedDark, DebtRedLight, false)
    TransactionType.RECEIVABLE_GIVEN -> Quad(Icons.Filled.PersonAdd, Color(0xFF7E22CE), Color(0xFFF3E8FF), false)
    TransactionType.RECEIVABLE_COLLECTED -> Quad(Icons.Filled.CheckCircle, MoneyGreenDark, MoneyGreenLight, true)
    TransactionType.SHOP_PURCHASE -> Quad(Icons.Filled.ShoppingBag, DebtRedDark, DebtRedLight, false)
    TransactionType.SHOP_PAYMENT -> Quad(Icons.Filled.Payment, MoneyGreenDark, MoneyGreenLight, false)
    TransactionType.LOAN_BORROWED -> Quad(Icons.Filled.AccountBalance, Color(0xFF1E3A8A), Color(0xFFDBEAFE), false)
    TransactionType.LOAN_REPAID -> Quad(Icons.Filled.Check, MoneyGreenDark, MoneyGreenLight, false)
    TransactionType.TRANSFER -> Quad(Icons.Filled.SwapHoriz, Color(0xFF2563EB), Color(0xFFEFF6FF), true)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("txn_item_${transaction.id}")
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Icon
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Title & Date
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = DateUtils.formatDate(transaction.date),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (!transaction.accountName.isNullOrEmpty()) {
            Text(
              text = " • ${transaction.accountName}",
              fontSize = 11.sp,
              color = DeepBlueAccent,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Amount & Delete Button
      Column(horizontalAlignment = Alignment.End) {
        val sign = if (transaction.type == TransactionType.INCOME || transaction.type == TransactionType.RECEIVABLE_COLLECTED) "+" else if (transaction.type == TransactionType.EXPENSE || transaction.type == TransactionType.RECEIVABLE_GIVEN) "-" else ""
        val amountColor = if (transaction.type == TransactionType.INCOME || transaction.type == TransactionType.RECEIVABLE_COLLECTED) MoneyGreenDark else if (transaction.type == TransactionType.EXPENSE || transaction.type == TransactionType.RECEIVABLE_GIVEN || transaction.type == TransactionType.SHOP_PURCHASE) DebtRed else DeepBlue

        Text(
          text = sign + CurrencyFormatter.format(transaction.amount, isBengali, false),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = amountColor
        )

        if (onDelete != null) {
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.DeleteOutline,
              contentDescription = "Delete",
              tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
