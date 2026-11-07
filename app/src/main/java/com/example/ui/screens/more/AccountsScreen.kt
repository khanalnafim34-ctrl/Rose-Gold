package com.example.ui.screens.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.model.FinancialSummary
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.TransferMoneyDialog
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.MoneyGreenDark
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val accounts by viewModel.accounts.collectAsState()
  val summary by viewModel.financialSummary.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val isBalanceHidden by viewModel.isBalanceHidden.collectAsState()

  var showTransferDialog by remember { mutableStateOf(false) }
  var showAddAccountDialog by remember { mutableStateOf(false) }
  var accountToDelete by remember { mutableStateOf<AccountEntity?>(null) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("accounts_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "হিসাব ও ওয়ালেট (Accounts)" else "Accounts & Wallets",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
    ) {
      // Total Available Assets Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Text(
            text = if (isBengali) "মোট সম্পদ ব্যালেন্স (Total Assets)" else "Total Assets Balance",
            fontSize = 13.sp,
            color = Color(0xFFDBEAFE)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = CurrencyFormatter.format(summary.totalAssets, isBengali, isBalanceHidden),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = if (isBengali) "নগদ + বিকাশ + নগদ + ব্যাংক ব্যালেন্সের যোগফল" else "Sum of all liquid accounts",
            fontSize = 11.sp,
            color = Color(0xFF93C5FD)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons: Transfer between accounts & Add account
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { showTransferDialog = true },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (isBengali) "টাকা স্থানান্তর" else "Transfer", fontSize = 13.sp, color = Color.White)
        }

        OutlinedButton(
          onClick = { showAddAccountDialog = true },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (isBengali) "নতুন হিসাব" else "Add Account", fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isBengali) "সকল হিসাবের তালিকা ও বর্তমান ব্যালেন্স" else "Accounts & Current Balances",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(accounts, key = { it.id }) { acc ->
          val currentBalance = summary.accountBalances[acc.id] ?: acc.initialBalance
          val icon = when (acc.type) {
            "CASH" -> Icons.Filled.Money
            "BKASH" -> Icons.Filled.PhoneAndroid
            "NAGAD" -> Icons.Filled.Smartphone
            "BANK" -> Icons.Filled.AccountBalance
            else -> Icons.Filled.CreditCard
          }
          val brandColor = try {
            if (!acc.colorHex.isNullOrEmpty()) Color(android.graphics.Color.parseColor(acc.colorHex)) else DeepBlue
          } catch (e: Exception) {
            DeepBlue
          }

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = brandColor,
                  modifier = Modifier.size(22.dp)
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = acc.name,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (acc.isDefault) (if (isBengali) "মূল হিসাব" else "Default") else acc.type,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = CurrencyFormatter.format(currentBalance, isBengali, isBalanceHidden),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (currentBalance >= 0) MoneyGreenDark else Color(0xFFEF4444)
                )

                if (!acc.isDefault && accounts.size > 1) {
                  IconButton(
                    onClick = { accountToDelete = acc },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      Icons.Filled.DeleteOutline,
                      contentDescription = "Delete",
                      tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Transfer Dialog
  if (showTransferDialog) {
    TransferMoneyDialog(
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showTransferDialog = false },
      onConfirm = { fromId, toId, amt, note ->
        viewModel.transferBetweenAccounts(fromId, toId, amt, System.currentTimeMillis(), note)
        showTransferDialog = false
      }
    )
  }

  // Add Account Dialog
  if (showAddAccountDialog) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("OTHER") }
    var initBalText by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddAccountDialog = false },
      shape = RoundedCornerShape(20.dp),
      title = { Text(if (isBengali) "নতুন হিসাব বা ওয়ালেট যোগ" else "Add New Account", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (isBengali) "হিসাবের নাম (যেমন: ব্র্যাক ব্যাংক, রকেট)" else "Account Name") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = initBalText,
            onValueChange = { initBalText = it },
            label = { Text(if (isBengali) "শুরুর ব্যালেন্স (ঐচ্ছিক)" else "Initial Balance") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank()) {
              val bal = initBalText.toDoubleOrNull() ?: 0.0
              viewModel.addAccount(name.trim(), type, bal, "#1E3A8A")
              showAddAccountDialog = false
            }
          }
        ) {
          Text(if (isBengali) "যোগ করুন" else "Add")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddAccountDialog = false }) {
          Text(if (isBengali) "বাতিল" else "Cancel")
        }
      }
    )
  }

  // Delete Confirm Dialog
  accountToDelete?.let { acc ->
    ConfirmDeleteDialog(
      title = if (isBengali) "হিসাব মুছবেন?" else "Delete Account?",
      message = if (isBengali) "${acc.name} হিসাবটি মুছে ফেলবেন? এটি মুছে ফেললে হিসাব সংক্রান্ত ডেটা সমন্বয় করা হবে।"
                else "Delete ${acc.name}?",
      isBengali = isBengali,
      onDismiss = { accountToDelete = null },
      onConfirm = {
        viewModel.deleteAccount(acc)
        accountToDelete = null
      }
    )
  }
}
