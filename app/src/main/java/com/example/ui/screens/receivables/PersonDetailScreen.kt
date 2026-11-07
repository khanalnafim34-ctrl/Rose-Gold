package com.example.ui.screens.receivables

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
import com.example.data.local.entity.ReceivableTransactionEntity
import com.example.model.PersonWithBalance
import com.example.ui.components.AddReceivableTransactionDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
  personId: Long,
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val personsWithBalance by viewModel.personsWithBalance.collectAsState()
  val accounts by viewModel.accounts.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  val personWithBalance = personsWithBalance.find { it.person.id == personId }
  val transactionsFlow = remember(personId) { viewModel.getPersonTransactions(personId) }
  val transactions by transactionsFlow.collectAsState(initial = emptyList())

  var showAddTxnDialog by remember { mutableStateOf(false) }
  var isGivingForDialog by remember { mutableStateOf(false) }
  var txnToDelete by remember { mutableStateOf<ReceivableTransactionEntity?>(null) }
  var showDeletePersonConfirm by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("person_detail_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = personWithBalance?.person?.name ?: (if (isBengali) "পাওনাদার বিবরণ" else "Person Ledger"),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { showDeletePersonConfirm = true }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Person", tint = DebtRed)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    }
  ) { innerPadding ->
    if (personWithBalance == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    } else {
      val person = personWithBalance.person

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp)
      ) {
        // Person Info & Ledger Summary Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
              Column {
                Text(
                  text = person.name,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (person.phone.isNotEmpty()) {
                  Text(
                    text = person.phone,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                if (!person.address.isNullOrEmpty()) {
                  Text(
                    text = person.address,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (personWithBalance.remainingDue > 0) MoneyGreenLight else Color(0xFFF1F5F9)
              ) {
                Column(
                  horizontalAlignment = Alignment.End,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = if (isBengali) "বাকি পাওনা" else "Outstanding Due",
                    fontSize = 11.sp,
                    color = if (personWithBalance.remainingDue > 0) MoneyGreenDark else Color(0xFF64748B)
                  )
                  Text(
                    text = CurrencyFormatter.format(personWithBalance.remainingDue, isBengali, false),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (personWithBalance.remainingDue > 0) MoneyGreenDark else Color(0xFF64748B)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = if (isBengali) "মোট ধার দেওয়া" else "Total Lent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(personWithBalance.totalGiven, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = if (isBengali) "মোট আদায় হয়েছে" else "Total Repaid", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(personWithBalance.totalReceived, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MoneyGreenDark)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(
                onClick = {
                  isGivingForDialog = true
                  showAddTxnDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "আরও ধার দিন" else "Lend More", fontSize = 12.sp)
              }

              Button(
                onClick = {
                  isGivingForDialog = false
                  showAddTxnDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoneyGreenDark)
              ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "টাকা আদায়" else "Receive Payment", fontSize = 12.sp, color = Color.White)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (isBengali) "লেনদেন ইতিহাস (Payment History)" else "Transaction History",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (transactions.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isBengali) "কোনো লেনদেন রেকর্ড নেই" else "No transaction history",
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(transactions, key = { it.id }) { txn ->
              val isGiven = txn.type == "GIVEN"
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(if (isGiven) Color(0xFFF3E8FF) else MoneyGreenLight),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isGiven) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                      contentDescription = null,
                      tint = if (isGiven) Color(0xFF7E22CE) else MoneyGreenDark,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (isGiven) (if (isBengali) "ধার দেওয়া হয়েছে" else "Money Lent")
                             else (if (isBengali) "পাওনা আদায়" else "Payment Received"),
                      fontSize = 14.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = DateUtils.formatDateTime(txn.date),
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!txn.note.isNullOrEmpty()) {
                      Text(
                        text = txn.note,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                      )
                    }
                  }

                  Text(
                    text = (if (isGiven) "-" else "+") + CurrencyFormatter.format(txn.amount, isBengali, false),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGiven) DebtRed else MoneyGreenDark
                  )

                  IconButton(
                    onClick = { txnToDelete = txn },
                    modifier = Modifier.size(28.dp)
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

  // Dialogs
  if (showAddTxnDialog) {
    AddReceivableTransactionDialog(
      personName = personWithBalance?.person?.name ?: "",
      isGiving = isGivingForDialog,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showAddTxnDialog = false },
      onConfirm = { amount, accountId, note ->
        viewModel.addReceivableTransaction(
          personId = personId,
          type = if (isGivingForDialog) "GIVEN" else "RECEIVED",
          amount = amount,
          date = System.currentTimeMillis(),
          dueDate = null,
          note = note,
          accountId = accountId
        )
        showAddTxnDialog = false
      }
    )
  }

  txnToDelete?.let { txn ->
    ConfirmDeleteDialog(
      title = if (isBengali) "লেনদেন মুছবেন?" else "Delete Transaction?",
      message = if (isBengali) "এই লেনদেনটি মুছে ফেললে হিসাব স্বয়ংক্রিয়ভাবে পুনরায় গণনা করা হবে।"
                else "Deleting this transaction will automatically recalculate balances.",
      isBengali = isBengali,
      onDismiss = { txnToDelete = null },
      onConfirm = {
        viewModel.deleteReceivableTransaction(txn)
        txnToDelete = null
      }
    )
  }

  if (showDeletePersonConfirm) {
    ConfirmDeleteDialog(
      title = if (isBengali) "ব্যক্তি হিসাব মুছবেন?" else "Delete Person?",
      message = if (isBengali) "এই ব্যক্তির সমস্ত পাওনা ও লেনদেন ইতিহাস চিরতরে মুছে যাবে।"
                else "This person and all related receivable history will be permanently deleted.",
      isBengali = isBengali,
      onDismiss = { showDeletePersonConfirm = false },
      onConfirm = {
        personWithBalance?.person?.let { viewModel.deletePerson(it) }
        showDeletePersonConfirm = false
        onBack()
      }
    )
  }
}
