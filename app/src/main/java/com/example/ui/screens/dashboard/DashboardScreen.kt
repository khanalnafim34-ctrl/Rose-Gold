package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FinancialSummary
import com.example.model.UnifiedTransaction
import com.example.ui.components.*
import com.example.ui.theme.DeepBlue
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun DashboardScreen(
  viewModel: FinanceViewModel,
  onNavigateToTransactions: () -> Unit,
  modifier: Modifier = Modifier
) {
  val summary by viewModel.financialSummary.collectAsState()
  val isBalanceHidden by viewModel.isBalanceHidden.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val transactions by viewModel.unifiedTransactions.collectAsState()
  val accounts by viewModel.accounts.collectAsState()

  // Quick Action Dialogs
  var showIncomeDialog by remember { mutableStateOf(false) }
  var showExpenseDialog by remember { mutableStateOf(false) }
  var showReceivableDialog by remember { mutableStateOf(false) }
  var showLoanDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("dashboard_screen"),
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isBengali) "স্বাগতম" else "Welcome to",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = if (isBengali) "মনেথাকা (Monethaka)" else "Monethaka",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = DeepBlue
            )
          }

          Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "৳ BDT",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Main Balance Card
      item {
        BalanceCard(
          summary = summary,
          isBalanceHidden = isBalanceHidden,
          isBengali = isBengali,
          onToggleHideBalance = { viewModel.toggleHideBalance() }
        )
      }

      // 2. Four Summary Cards
      item {
        FinancialMetricCards(
          summary = summary,
          isBengali = isBengali,
          isBalanceHidden = isBalanceHidden
        )
      }

      // 3. Four Quick Action Buttons
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = if (isBengali) "দ্রুত কার্যক্রম (Quick Actions)" else "Quick Actions",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            QuickActionButtons(
              isBengali = isBengali,
              onAddIncome = { showIncomeDialog = true },
              onAddExpense = { showExpenseDialog = true },
              onAddReceivable = { showReceivableDialog = true },
              onAddLoan = { showLoanDialog = true }
            )
          }
        }
      }

      // 4. Permanently Visible "My Total Liabilities" Section
      item {
        LiabilitySummaryCard(
          summary = summary,
          isBengali = isBengali,
          isBalanceHidden = isBalanceHidden
        )
      }

      // 5. Recent Transactions Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isBengali) "সাম্প্রতিক লেনদেন" else "Recent Transactions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (transactions.isNotEmpty()) {
            TextButton(
              onClick = onNavigateToTransactions,
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = if (isBengali) "সব দেখুন" else "View All",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlue
              )
              Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = DeepBlue
              )
            }
          }
        }
      }

      // 6. Recent Transactions List (first 5)
      val recent = transactions.take(5)
      if (recent.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Filled.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (isBengali) "কোনো লেনদেন রেকর্ড পাওয়া যায়নি" else "No transactions found",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = if (isBengali) "উপরের বাটন দিয়ে আয়, খরচ, পাওনা বা ঋণ যোগ করুন" else "Use quick action buttons above to start",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      } else {
        items(recent, key = { "dashboard_txn_${it.id}_${it.type}" }) { txn ->
          TransactionItemCard(
            transaction = txn,
            isBengali = isBengali
          )
        }
      }
    }
  }

  // Dialogs
  if (showIncomeDialog) {
    AddIncomeExpenseDialog(
      isIncome = true,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showIncomeDialog = false },
      onConfirm = { amt, cat, accId, note ->
        viewModel.addIncomeExpense("INCOME", cat, amt, System.currentTimeMillis(), accId, note)
        showIncomeDialog = false
      }
    )
  }

  if (showExpenseDialog) {
    AddIncomeExpenseDialog(
      isIncome = false,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showExpenseDialog = false },
      onConfirm = { amt, cat, accId, note ->
        viewModel.addIncomeExpense("EXPENSE", cat, amt, System.currentTimeMillis(), accId, note)
        showExpenseDialog = false
      }
    )
  }

  if (showReceivableDialog) {
    AddPersonDialog(
      isBengali = isBengali,
      accounts = accounts,
      onDismiss = { showReceivableDialog = false },
      onConfirm = { name, phone, address, notes, initialDue, accId ->
        viewModel.addPerson(name, phone, address, notes, initialDue, accId)
        showReceivableDialog = false
      }
    )
  }

  if (showLoanDialog) {
    AddLoanDialog(
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showLoanDialog = false },
      onConfirm = { lenderName, phone, amount, accId, notes ->
        viewModel.addLoan(lenderName, phone, amount, System.currentTimeMillis(), null, notes, accId)
        showLoanDialog = false
      }
    )
  }
}
