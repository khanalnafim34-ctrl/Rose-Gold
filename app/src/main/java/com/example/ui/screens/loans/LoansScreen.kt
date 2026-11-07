package com.example.ui.screens.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoanWithBalance
import com.example.ui.components.AddLoanDialog
import com.example.ui.components.AddLoanRepaymentDialog
import com.example.ui.components.LoanCard
import com.example.ui.theme.DebtRedDark
import com.example.ui.theme.DeepBlue
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
  viewModel: FinanceViewModel,
  onOpenLoanDetail: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val loansWithBalance by viewModel.loansWithBalance.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val accounts by viewModel.accounts.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var showAddLoanDialog by remember { mutableStateOf(false) }
  var selectedLoanForRepay by remember { mutableStateOf<LoanWithBalance?>(null) }

  val filteredLoans = remember(loansWithBalance, searchQuery) {
    if (searchQuery.isBlank()) {
      loansWithBalance.sortedByDescending { it.remainingAmount }
    } else {
      loansWithBalance.filter {
        it.loan.lenderName.contains(searchQuery, ignoreCase = true) ||
        (it.loan.phone?.contains(searchQuery, ignoreCase = true) == true)
      }.sortedByDescending { it.remainingAmount }
    }
  }

  val totalRemainingLoanSum = remember(loansWithBalance) {
    loansWithBalance.sumOf { kotlin.math.max(0.0, it.remainingAmount) }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("loans_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "ব্যক্তিগত ঋণ হিসাব" else "Personal Loans",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddLoanDialog = true },
        containerColor = DeepBlue,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_loan")
      ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Loan")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
    ) {
      // Header Total Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isBengali) "অবশিষ্ট ব্যক্তিগত ঋণ দেনা" else "Total Remaining Personal Loans",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = CurrencyFormatter.format(totalRemainingLoanSum, isBengali, false),
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = DebtRedDark
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
          ) {
            Text(
              text = "${loansWithBalance.size} ${if (isBengali) "টি ঋণ" else "Loans"}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(if (isBengali) "ঋণদাতার নাম দিয়ে খুঁজুন..." else "Search lender name...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // List of Loans
      if (filteredLoans.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.AccountBalance,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) {
                if (isBengali) "কোনো ঋণ মেলেনি" else "No matching loans"
              } else {
                if (isBengali) "কোনো ঋণ রেকর্ড নেই" else "No loans recorded"
              },
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isBengali) "+ বাটনে চাপ দিয়ে নতুন ঋণ যোগ করুন" else "Tap + to add a loan",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentPadding = PaddingValues(bottom = 90.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredLoans, key = { it.loan.id }) { item ->
            LoanCard(
              loanWithBalance = item,
              isBengali = isBengali,
              onClick = { onOpenLoanDetail(item.loan.id) },
              onRepayLoan = { selectedLoanForRepay = item }
            )
          }
        }
      }
    }
  }

  // Dialogs
  if (showAddLoanDialog) {
    AddLoanDialog(
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showAddLoanDialog = false },
      onConfirm = { lenderName, phone, amount, accountId, notes ->
        viewModel.addLoan(lenderName, phone, amount, System.currentTimeMillis(), null, notes, accountId)
        showAddLoanDialog = false
      }
    )
  }

  selectedLoanForRepay?.let { l ->
    AddLoanRepaymentDialog(
      lenderName = l.loan.lenderName,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { selectedLoanForRepay = null },
      onConfirm = { amount, accountId, note ->
        viewModel.addLoanRepayment(
          loanId = l.loan.id,
          amount = amount,
          date = System.currentTimeMillis(),
          accountId = accountId,
          note = note
        )
        selectedLoanForRepay = null
      }
    )
  }
}
