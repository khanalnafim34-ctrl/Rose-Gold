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
import com.example.model.TransactionType
import com.example.model.UnifiedTransaction
import com.example.ui.components.AddIncomeExpenseDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeExpenseScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val transactions by viewModel.unifiedTransactions.collectAsState()
  val accounts by viewModel.accounts.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Income, 2 = Expense
  var showIncomeDialog by remember { mutableStateOf(false) }
  var showExpenseDialog by remember { mutableStateOf(false) }
  var txnToDelete by remember { mutableStateOf<UnifiedTransaction?>(null) }

  val filteredList = remember(transactions, selectedTab) {
    transactions.filter {
      when (selectedTab) {
        1 -> it.type == TransactionType.INCOME
        2 -> it.type == TransactionType.EXPENSE
        else -> it.type == TransactionType.INCOME || it.type == TransactionType.EXPENSE
      }
    }
  }

  val totalIncome = remember(transactions) {
    transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
  }

  val totalExpense = remember(transactions) {
    transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("income_expense_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "আয় ও খরচ হিসাব" else "Income & Expenses",
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
      // Summary Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(text = if (isBengali) "মোট আয় (Income)" else "Total Income", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = CurrencyFormatter.format(totalIncome, isBengali, false), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MoneyGreenDark)
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(text = if (isBengali) "মোট খরচ (Expense)" else "Total Expense", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = CurrencyFormatter.format(totalExpense, isBengali, false), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DebtRedDark)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab selector: All / Income / Expense
      PrimaryTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text(if (isBengali) "সব লেনদেন" else "All") }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text(if (isBengali) "আয় (Income)" else "Income") }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text(if (isBengali) "খরচ (Expense)" else "Expense") }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Add Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { showIncomeDialog = true },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = MoneyGreenDark),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "আয় যোগ" else "Add Income", fontSize = 13.sp, color = Color.White)
        }

        Button(
          onClick = { showExpenseDialog = true },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = DebtRedDark),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isBengali) "খরচ যোগ" else "Add Expense", fontSize = 13.sp, color = Color.White)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // List
      if (filteredList.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxWidth().weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (isBengali) "কোনো আয় বা খরচের রেকর্ড নেই" else "No income or expense records",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxWidth().weight(1f),
          contentPadding = PaddingValues(bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredList, key = { "inc_exp_${it.id}_${it.type}" }) { txn ->
            TransactionItemCard(
              transaction = txn,
              isBengali = isBengali,
              onDelete = { txnToDelete = txn }
            )
          }
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

  txnToDelete?.let { txn ->
    ConfirmDeleteDialog(
      title = if (isBengali) "লেনদেন মুছবেন?" else "Delete Transaction?",
      message = if (isBengali) "এই রেকর্ড মুছে ফেললে সম্পর্কিত ওয়ালেট বা অ্যাকাউন্টের ব্যালেন্স স্বয়ংক্রিয়ভাবে সমন্বয় করা হবে।"
                else "Deleting this transaction will automatically adjust related wallet balances.",
      isBengali = isBengali,
      onDismiss = { txnToDelete = null },
      onConfirm = {
        viewModel.deleteIncomeExpense(txn.originalEntityId)
        txnToDelete = null
      }
    )
  }
}
