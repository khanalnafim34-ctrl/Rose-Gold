package com.example.ui.screens.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionType
import com.example.model.UnifiedTransaction
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.TransactionItemCard
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.FinanceViewModel
import java.util.Calendar

enum class DateFilterType(val labelBn: String, val labelEn: String) {
  ALL("সব", "All"),
  TODAY("আজ", "Today"),
  YESTERDAY("গতকাল", "Yesterday"),
  LAST_7_DAYS("গত ৭ দিন", "Last 7 Days"),
  THIS_MONTH("চলতি মাস", "This Month"),
  LAST_MONTH("গত মাস", "Last Month")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val transactions by viewModel.unifiedTransactions.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedDateFilter by remember { mutableStateOf(DateFilterType.ALL) }
  var txnToDelete by remember { mutableStateOf<UnifiedTransaction?>(null) }

  val filteredTransactions = remember(transactions, searchQuery, selectedDateFilter) {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    val dateFiltered = transactions.filter { txn ->
      when (selectedDateFilter) {
        DateFilterType.ALL -> true
        DateFilterType.TODAY -> DateUtils.isToday(txn.date)
        DateFilterType.YESTERDAY -> DateUtils.isYesterday(txn.date)
        DateFilterType.LAST_7_DAYS -> txn.date >= (now - 7 * 24 * 60 * 60 * 1000L)
        DateFilterType.THIS_MONTH -> {
          cal.timeInMillis = now
          val curMonth = cal.get(Calendar.MONTH)
          val curYear = cal.get(Calendar.YEAR)
          cal.timeInMillis = txn.date
          cal.get(Calendar.MONTH) == curMonth && cal.get(Calendar.YEAR) == curYear
        }
        DateFilterType.LAST_MONTH -> {
          cal.timeInMillis = now
          cal.add(Calendar.MONTH, -1)
          val lastMonth = cal.get(Calendar.MONTH)
          val lastMonthYear = cal.get(Calendar.YEAR)
          cal.timeInMillis = txn.date
          cal.get(Calendar.MONTH) == lastMonth && cal.get(Calendar.YEAR) == lastMonthYear
        }
      }
    }

    if (searchQuery.isBlank()) {
      dateFiltered
    } else {
      dateFiltered.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        (it.subtitle?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.accountName?.contains(searchQuery, ignoreCase = true) == true) ||
        (it.note?.contains(searchQuery, ignoreCase = true) == true)
      }
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("all_transactions_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "সকল লেনদেনের বিবরণী" else "All Transactions",
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
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(if (isBengali) "শিরোনাম বা হিসাব দিয়ে খুঁজুন..." else "Search transactions...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Filter Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DateFilterType.entries.forEach { filter ->
          FilterChip(
            selected = selectedDateFilter == filter,
            onClick = { selectedDateFilter = filter },
            label = {
              Text(
                text = if (isBengali) filter.labelBn else filter.labelEn,
                fontSize = 12.sp
              )
            },
            shape = RoundedCornerShape(10.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Transaction count indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isBengali) "মোট ${filteredTransactions.size} টি লেনদেন" else "${filteredTransactions.size} Transactions",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // List of Transactions
      if (filteredTransactions.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxWidth().weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.ReceiptLong,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (isBengali) "কোনো লেনদেন রেকর্ড পাওয়া যায়নি" else "No transactions found",
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxWidth().weight(1f),
          contentPadding = PaddingValues(bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredTransactions, key = { "all_txn_${it.id}_${it.type}" }) { txn ->
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

  // Delete Confirmation Dialog
  txnToDelete?.let { txn ->
    ConfirmDeleteDialog(
      title = if (isBengali) "লেনদেন মুছবেন?" else "Delete Transaction?",
      message = if (isBengali) "এই লেনদেনটি মুছে ফেললে হিসাব ও ব্যালেন্স স্বয়ংক্রিয়ভাবে পুনরায় গণনা করা হবে।"
                else "Deleting this transaction will automatically recalculate all related balances.",
      isBengali = isBengali,
      onDismiss = { txnToDelete = null },
      onConfirm = {
        when (txn.type) {
          TransactionType.INCOME, TransactionType.EXPENSE -> {
            viewModel.deleteIncomeExpense(txn.originalEntityId)
          }
          TransactionType.TRANSFER -> {
            viewModel.deleteTransfer(txn.originalEntityId)
          }
          else -> {
            // For receivable/shop/loan, items can also be deleted from their individual screens
            // If deleted here:
            if (txn.type == TransactionType.RECEIVABLE_GIVEN || txn.type == TransactionType.RECEIVABLE_COLLECTED) {
              viewModel.deleteReceivableTransaction(
                com.example.data.local.entity.ReceivableTransactionEntity(id = txn.originalEntityId, personId = txn.secondaryEntityId ?: 0L, type = "", amount = 0.0, date = 0L)
              )
            } else if (txn.type == TransactionType.SHOP_PURCHASE || txn.type == TransactionType.SHOP_PAYMENT) {
              viewModel.deleteShopTransaction(
                com.example.data.local.entity.ShopTransactionEntity(id = txn.originalEntityId, shopId = txn.secondaryEntityId ?: 0L, type = "", amount = 0.0, date = 0L)
              )
            } else if (txn.type == TransactionType.LOAN_REPAID) {
              viewModel.deleteLoanRepayment(
                com.example.data.local.entity.LoanRepaymentEntity(id = txn.originalEntityId, loanId = txn.secondaryEntityId ?: 0L, amount = 0.0, date = 0L)
              )
            }
          }
        }
        txnToDelete = null
      }
    )
  }
}
