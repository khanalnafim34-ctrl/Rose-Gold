package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoanWithBalance
import com.example.model.PersonWithBalance
import com.example.model.ShopWithBalance
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter

@Composable
fun PersonCard(
  personWithBalance: PersonWithBalance,
  isBengali: Boolean,
  onClick: () -> Unit,
  onReceivePayment: () -> Unit,
  onGiveMore: () -> Unit,
  modifier: Modifier = Modifier
) {
  val person = personWithBalance.person
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("person_card_${person.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = person.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (person.phone.isNotEmpty()) {
            Text(
              text = person.phone,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Remaining due badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (personWithBalance.remainingDue > 0) MoneyGreenLight else Color(0xFFF1F5F9)
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isBengali) "বাকি পাওনা" else "Remaining",
              fontSize = 10.sp,
              color = if (personWithBalance.remainingDue > 0) MoneyGreenDark else Color(0xFF64748B)
            )
            Text(
              text = CurrencyFormatter.format(personWithBalance.remainingDue, isBengali, false),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (personWithBalance.remainingDue > 0) MoneyGreenDark else Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = if (isBengali) "মোট দেওয়া" else "Total Given",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(personWithBalance.totalGiven, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (isBengali) "মোট আদায়" else "Total Received",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(personWithBalance.totalReceived, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MoneyGreenDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onGiveMore,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "ধার দেওয়া" else "Lend More", fontSize = 12.sp)
        }

        Button(
          onClick = onReceivePayment,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MoneyGreenDark),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "টাকা আদায়" else "Receive", fontSize = 12.sp, color = Color.White)
        }
      }
    }
  }
}

@Composable
fun ShopCard(
  shopWithBalance: ShopWithBalance,
  isBengali: Boolean,
  onClick: () -> Unit,
  onPayDue: () -> Unit,
  onBuyCredit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shop = shopWithBalance.shop
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("shop_card_${shop.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = shop.shopName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (!shop.ownerName.isNullOrEmpty() || !shop.phone.isNullOrEmpty()) {
            Text(
              text = "${shop.ownerName ?: ""} ${if (!shop.phone.isNullOrEmpty()) "(${shop.phone})" else ""}".trim(),
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Remaining due badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (shopWithBalance.remainingDue > 0) DebtRedLight else Color(0xFFF1F5F9)
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isBengali) "বাকি দেনা" else "Remaining Due",
              fontSize = 10.sp,
              color = if (shopWithBalance.remainingDue > 0) DebtRedDark else Color(0xFF64748B)
            )
            Text(
              text = CurrencyFormatter.format(shopWithBalance.remainingDue, isBengali, false),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (shopWithBalance.remainingDue > 0) DebtRedDark else Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = if (isBengali) "মোট বাকি কেনাকাটা" else "Total Purchases",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(shopWithBalance.totalPurchases, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (isBengali) "মোট পরিশোধ" else "Total Paid",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(shopWithBalance.totalPaid, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MoneyGreenDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onBuyCredit,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Icon(Icons.Filled.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "বাকি ক্রয়" else "Buy Credit", fontSize = 12.sp)
        }

        Button(
          onClick = onPayDue,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = DebtRedDark),
          contentPadding = PaddingValues(vertical = 8.dp)
        ) {
          Icon(Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "বাকি পরিশোধ" else "Pay Due", fontSize = 12.sp, color = Color.White)
        }
      }
    }
  }
}

@Composable
fun LoanCard(
  loanWithBalance: LoanWithBalance,
  isBengali: Boolean,
  onClick: () -> Unit,
  onRepayLoan: () -> Unit,
  modifier: Modifier = Modifier
) {
  val loan = loanWithBalance.loan
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("loan_card_${loan.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = loan.lenderName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (!loan.phone.isNullOrEmpty()) {
            Text(
              text = loan.phone,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Remaining Loan badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (loanWithBalance.remainingAmount > 0) DebtRedLight else Color(0xFFF1F5F9)
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (isBengali) "অবশিষ্ট ঋণ" else "Remaining Loan",
              fontSize = 10.sp,
              color = if (loanWithBalance.remainingAmount > 0) DebtRedDark else Color(0xFF64748B)
            )
            Text(
              text = CurrencyFormatter.format(loanWithBalance.remainingAmount, isBengali, false),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (loanWithBalance.remainingAmount > 0) DebtRedDark else Color(0xFF64748B)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = if (isBengali) "মূল ঋণ গ্রহণ" else "Loan Borrowed",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(loanWithBalance.initialAmount, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (isBengali) "পরিশোধ করা হয়েছে" else "Total Repaid",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = CurrencyFormatter.format(loanWithBalance.totalRepaid, isBengali, false),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MoneyGreenDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = onRepayLoan,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
        contentPadding = PaddingValues(vertical = 8.dp)
      ) {
        Icon(Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = if (isBengali) "ঋণ পরিশোধ করুন" else "Repay Loan", fontSize = 13.sp, color = Color.White)
      }
    }
  }
}
