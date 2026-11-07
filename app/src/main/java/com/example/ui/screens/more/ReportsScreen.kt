package com.example.ui.screens.more

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionType
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  val summary by viewModel.financialSummary.collectAsState()
  val transactions by viewModel.unifiedTransactions.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  // Monthly stats
  val now = System.currentTimeMillis()
  val cal = Calendar.getInstance()
  val thisMonthTxns = remember(transactions) {
    cal.timeInMillis = now
    val curM = cal.get(Calendar.MONTH)
    val curY = cal.get(Calendar.YEAR)
    transactions.filter {
      cal.timeInMillis = it.date
      cal.get(Calendar.MONTH) == curM && cal.get(Calendar.YEAR) == curY
    }
  }

  val monthlyIncome = remember(thisMonthTxns) {
    thisMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
  }
  val monthlyExpense = remember(thisMonthTxns) {
    thisMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
  }
  val monthlyReceived = remember(thisMonthTxns) {
    thisMonthTxns.filter { it.type == TransactionType.RECEIVABLE_COLLECTED }.sumOf { it.amount }
  }
  val monthlyDebtPaid = remember(thisMonthTxns) {
    thisMonthTxns.filter { it.type == TransactionType.LOAN_REPAID || it.type == TransactionType.SHOP_PAYMENT }.sumOf { it.amount }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("reports_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "রিপোর্ট ও আর্থিক বিশ্লেষণ" else "Financial Reports",
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
          IconButton(
            onClick = {
              exportAndShareReport(
                context = context,
                summaryText = generateReportSummary(
                  isBengali = isBengali,
                  summary = summary,
                  monthlyIncome = monthlyIncome,
                  monthlyExpense = monthlyExpense,
                  monthlyReceived = monthlyReceived,
                  monthlyDebtPaid = monthlyDebtPaid
                )
              )
            }
          ) {
            Icon(Icons.Filled.Share, contentDescription = "Export Report", tint = DeepBlue)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Assets vs Liabilities Visual Bar Chart Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Text(
              text = if (isBengali) "সম্পদ বনাম দায়ের অনুপাত" else "Assets vs Liabilities Breakdown",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            val total = summary.totalAssets + summary.totalLiabilities
            val assetRatio = if (total > 0) (summary.totalAssets / total).toFloat() else 0.5f

            // Ratio Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            ) {
              if (assetRatio > 0) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .weight(assetRatio.coerceIn(0.01f, 0.99f))
                    .background(MoneyGreen, RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                )
              }
              if (1f - assetRatio > 0) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - assetRatio).coerceIn(0.01f, 0.99f))
                    .background(DebtRed, RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(MoneyGreen, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${if (isBengali) "মোট সম্পদ:" else "Assets:"} ${CurrencyFormatter.format(summary.totalAssets, isBengali, false)}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(DebtRed, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${if (isBengali) "মোট দায়:" else "Liabilities:"} ${CurrencyFormatter.format(summary.totalLiabilities, isBengali, false)}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      // 2. Monthly Performance Cards
      item {
        Text(
          text = if (isBengali) "চলতি মাসের আর্থিক হিসাব" else "This Month's Metrics",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          ReportMetricRow(
            label = if (isBengali) "চলতি মাসে আয়" else "Monthly Income",
            amount = monthlyIncome,
            color = MoneyGreenDark,
            isBengali = isBengali
          )
          ReportMetricRow(
            label = if (isBengali) "চলতি মাসে খরচ" else "Monthly Expense",
            amount = monthlyExpense,
            color = DebtRedDark,
            isBengali = isBengali
          )
          ReportMetricRow(
            label = if (isBengali) "চলতি মাসে পাওনা আদায়" else "Money Collected",
            amount = monthlyReceived,
            color = MoneyGreenDark,
            isBengali = isBengali
          )
          ReportMetricRow(
            label = if (isBengali) "চলতি মাসে দেনা ও ঋণ পরিশোধ" else "Debt Repayments",
            amount = monthlyDebtPaid,
            color = DeepBlue,
            isBengali = isBengali
          )
        }
      }

      // 3. Financial Summary Table
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = if (isBengali) "সামগ্রিক আর্থিক বিবরণী" else "Overall Financial Summary",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            SummaryTableRow(if (isBengali) "মোট সম্পদ (Assets)" else "Total Assets", CurrencyFormatter.format(summary.totalAssets, isBengali, false), MoneyGreenDark)
            SummaryTableRow(if (isBengali) "মোট দায় (Liabilities)" else "Total Liabilities", CurrencyFormatter.format(summary.totalLiabilities, isBengali, false), DebtRedDark)
            SummaryTableRow(if (isBengali) "নেট ব্যালেন্স (Net Balance)" else "Net Balance", CurrencyFormatter.format(summary.netBalance, isBengali, false), if (summary.netBalance >= 0) MoneyGreenDark else DebtRedDark, isBold = true)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            SummaryTableRow(if (isBengali) "মোট পাওনা (Receivables)" else "Receivables", CurrencyFormatter.format(summary.totalReceivables, isBengali, false), MoneyGreenDark)
            SummaryTableRow(if (isBengali) "অবশিষ্ট ঋণ (Personal Loans)" else "Remaining Loans", CurrencyFormatter.format(summary.remainingPersonalLoans, isBengali, false), DebtRedDark)
            SummaryTableRow(if (isBengali) "দোকানের বাকি (Shop Dues)" else "Shop Dues", CurrencyFormatter.format(summary.remainingShopDues, isBengali, false), DebtRedDark)
          }
        }
      }

      // 4. Export Button
      item {
        Button(
          onClick = {
            exportAndShareReport(
              context = context,
              summaryText = generateReportSummary(
                isBengali = isBengali,
                summary = summary,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                monthlyReceived = monthlyReceived,
                monthlyDebtPaid = monthlyDebtPaid
              )
            )
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
        ) {
          Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = if (isBengali) "রিপোর্ট শেয়ার ও এক্সপোর্ট করুন" else "Export & Share Report", fontSize = 14.sp, color = Color.White)
        }
      }
    }
  }
}

@Composable
private fun ReportMetricRow(
  label: String,
  amount: Double,
  color: Color,
  isBengali: Boolean
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      Text(text = CurrencyFormatter.format(amount, isBengali, false), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
  }
}

@Composable
private fun SummaryTableRow(
  label: String,
  value: String,
  valueColor: Color,
  isBold: Boolean = false
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      fontSize = 14.sp,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
      color = valueColor
    )
  }
}

private fun generateReportSummary(
  isBengali: Boolean,
  summary: com.example.model.FinancialSummary,
  monthlyIncome: Double,
  monthlyExpense: Double,
  monthlyReceived: Double,
  monthlyDebtPaid: Double
): String {
  return buildString {
    appendLine("=== মনেথাকা (Monethaka) Financial Report ===")
    appendLine("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
    appendLine("--------------------------------------------")
    appendLine("Total Assets: ৳ ${summary.totalAssets}")
    appendLine("Total Liabilities: ৳ ${summary.totalLiabilities}")
    appendLine("Net Balance: ৳ ${summary.netBalance}")
    appendLine("Total Receivables: ৳ ${summary.totalReceivables}")
    appendLine("Remaining Personal Loans: ৳ ${summary.remainingPersonalLoans}")
    appendLine("Remaining Shop Dues: ৳ ${summary.remainingShopDues}")
    appendLine("--------------------------------------------")
    appendLine("This Month Income: ৳ $monthlyIncome")
    appendLine("This Month Expense: ৳ $monthlyExpense")
    appendLine("This Month Money Collected: ৳ $monthlyReceived")
    appendLine("This Month Debt Paid: ৳ $monthlyDebtPaid")
    appendLine("============================================")
  }
}

private fun exportAndShareReport(context: Context, summaryText: String) {
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "Monethaka Financial Report")
    putExtra(Intent.EXTRA_TEXT, summaryText)
  }
  context.startActivity(Intent.createChooser(intent, "Share Financial Report"))
}
