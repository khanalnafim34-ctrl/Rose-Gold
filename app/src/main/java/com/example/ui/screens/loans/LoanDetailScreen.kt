package com.example.ui.screens.loans

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Payment
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
import com.example.data.local.entity.LoanRepaymentEntity
import com.example.model.LoanWithBalance
import com.example.ui.components.AddLoanRepaymentDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
  loanId: Long,
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val loansWithBalance by viewModel.loansWithBalance.collectAsState()
  val accounts by viewModel.accounts.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  val loanWithBalance = loansWithBalance.find { it.loan.id == loanId }
  val repaymentsFlow = remember(loanId) { viewModel.getLoanRepayments(loanId) }
  val repayments by repaymentsFlow.collectAsState(initial = emptyList())

  var showAddRepayDialog by remember { mutableStateOf(false) }
  var repayToDelete by remember { mutableStateOf<LoanRepaymentEntity?>(null) }
  var showDeleteLoanConfirm by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("loan_detail_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = loanWithBalance?.loan?.lenderName ?: (if (isBengali) "ঋণের বিবরণী" else "Loan Details"),
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
          IconButton(onClick = { showDeleteLoanConfirm = true }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Loan", tint = DebtRed)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    }
  ) { innerPadding ->
    if (loanWithBalance == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    } else {
      val loan = loanWithBalance.loan

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp)
      ) {
        // Loan Summary Card
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
                  text = loan.lenderName,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (!loan.phone.isNullOrEmpty()) {
                  Text(
                    text = loan.phone,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                if (!loan.notes.isNullOrEmpty()) {
                  Text(
                    text = loan.notes,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (loanWithBalance.remainingAmount > 0) DebtRedLight else Color(0xFFF1F5F9)
              ) {
                Column(
                  horizontalAlignment = Alignment.End,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = if (isBengali) "অবশিষ্ট ঋণ" else "Remaining Debt",
                    fontSize = 11.sp,
                    color = if (loanWithBalance.remainingAmount > 0) DebtRedDark else Color(0xFF64748B)
                  )
                  Text(
                    text = CurrencyFormatter.format(loanWithBalance.remainingAmount, isBengali, false),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (loanWithBalance.remainingAmount > 0) DebtRedDark else Color(0xFF64748B)
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
                Text(text = if (isBengali) "মূল ঋণ গ্রহণ" else "Total Borrowed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(loanWithBalance.initialAmount, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "তারিখ: ${DateUtils.formatDate(loan.dateReceived)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = if (isBengali) "মোট পরিশোধ" else "Total Repaid", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(loanWithBalance.totalRepaid, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MoneyGreenDark)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { showAddRepayDialog = true },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
            ) {
              Icon(Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = if (isBengali) "ঋণ পরিশোধ করুন" else "Make Loan Repayment", fontSize = 13.sp, color = Color.White)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (isBengali) "পরিশোধের ইতিহাস (Repayment History)" else "Repayment History",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (repayments.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isBengali) "এখনো কোনো ঋণ পরিশোধ করা হয়নি" else "No repayments recorded yet",
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(repayments, key = { it.id }) { r ->
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
                      .background(MoneyGreenLight),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Check,
                      contentDescription = null,
                      tint = MoneyGreenDark,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (isBengali) "কিস্তি / ঋণ পরিশোধ" else "Loan Repayment",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = DateUtils.formatDateTime(r.date),
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!r.note.isNullOrEmpty()) {
                      Text(
                        text = r.note,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                      )
                    }
                  }

                  Text(
                    text = CurrencyFormatter.format(r.amount, isBengali, false),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoneyGreenDark
                  )

                  IconButton(
                    onClick = { repayToDelete = r },
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
  if (showAddRepayDialog) {
    AddLoanRepaymentDialog(
      lenderName = loanWithBalance?.loan?.lenderName ?: "",
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showAddRepayDialog = false },
      onConfirm = { amount, accountId, note ->
        viewModel.addLoanRepayment(
          loanId = loanId,
          amount = amount,
          date = System.currentTimeMillis(),
          accountId = accountId,
          note = note
        )
        showAddRepayDialog = false
      }
    )
  }

  repayToDelete?.let { r ->
    ConfirmDeleteDialog(
      title = if (isBengali) "পরিশোধ রেকর্ড মুছবেন?" else "Delete Repayment Record?",
      message = if (isBengali) "এই পরিশোধ রেকর্ড মুছে ফেললে অবশিষ্ট ঋণ আবার বৃদ্ধি পাবে।"
                else "Deleting this repayment will restore the outstanding debt.",
      isBengali = isBengali,
      onDismiss = { repayToDelete = null },
      onConfirm = {
        viewModel.deleteLoanRepayment(r)
        repayToDelete = null
      }
    )
  }

  if (showDeleteLoanConfirm) {
    ConfirmDeleteDialog(
      title = if (isBengali) "ঋণ হিসাব মুছবেন?" else "Delete Loan?",
      message = if (isBengali) "এই ঋণের সমস্ত তথ্য ও পরিশোধ ইতিহাস চিরতরে মুছে যাবে।"
                else "This loan and all repayment logs will be permanently deleted.",
      isBengali = isBengali,
      onDismiss = { showDeleteLoanConfirm = false },
      onConfirm = {
        loanWithBalance?.loan?.let { viewModel.deleteLoan(it) }
        showDeleteLoanConfirm = false
        onBack()
      }
    )
  }
}
