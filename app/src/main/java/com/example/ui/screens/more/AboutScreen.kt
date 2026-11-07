package com.example.ui.screens.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.MoneyGreen
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val isBengali by viewModel.isBengali.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("about_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "অ্যাপ পরিচিতি" else "About Monethaka",
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
        .padding(20.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(DeepBlue),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Filled.AccountBalanceWallet,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(42.dp)
        )
      }

      Text(
        text = if (isBengali) "মনেথাকা (Monethaka)" else "Monethaka",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = if (isBengali) "সংস্করণ ১.০ • সম্পূর্ণ অফলাইন হিসাব খাতা" else "Version 1.0 • Offline Personal Finance & Ledger",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = if (isBengali) "আর্থিক সূত্র ও বৈশিষ্ট্যসমূহ:" else "Financial Formula & Features:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )

          FormulaItem(
            title = if (isBengali) "মোট সম্পদ (Total Assets)" else "Total Assets",
            desc = if (isBengali) "নগদ + বিকাশ + নগদ + ব্যাংক ব্যালেন্স" else "Cash + bKash + Nagad + Bank Balance"
          )
          FormulaItem(
            title = if (isBengali) "মোট দায় (Total Liabilities)" else "Total Liabilities",
            desc = if (isBengali) "ব্যক্তিগত ঋণ + দোকানের বাকি" else "Personal Loans + Shop Dues"
          )
          FormulaItem(
            title = if (isBengali) "নেট ব্যালেন্স (Net Balance)" else "Net Balance",
            desc = if (isBengali) "মোট সম্পদ - মোট দায়" else "Total Assets - Total Liabilities"
          )
        }
      }

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          FeatureHighlight(if (isBengali) "১০০% অফলাইন ও নিরাপদ (Room ডাটাবেজ)" else "100% Offline & Private (Room DB)")
          FeatureHighlight(if (isBengali) "পাওনাদার ও দোকানের স্বতন্ত্র হিসাব খাতা" else "Individual Receivables & Shop Ledgers")
          FeatureHighlight(if (isBengali) "আংশিক বা পূর্ণ পরিশোধ স্বয়ংক্রিয় গণনা" else "Automated Partial/Full Repayment Calculation")
          FeatureHighlight(if (isBengali) "৪ ডিজিটের পিন লক নিরাপত্তা" else "4-digit PIN Security Protection")
          FeatureHighlight(if (isBengali) "অফলাইন JSON ব্যাকআপ ও রিস্টোর" else "Offline JSON Backup & Restoration")
        }
      }
    }
  }
}

@Composable
private fun FormulaItem(title: String, desc: String) {
  Column {
    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DeepBlue)
    Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun FeatureHighlight(text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MoneyGreen, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
  }
}
