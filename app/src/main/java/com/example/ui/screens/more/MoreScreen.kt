package com.example.ui.screens.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.SubScreen
import com.example.ui.theme.DeepBlue
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
  viewModel: FinanceViewModel,
  onNavigate: (SubScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val isBengali by viewModel.isBengali.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("more_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "অন্যান্য সেবা ও সেটিংস" else "More & Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
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
      contentPadding = PaddingValues(bottom = 100.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Financial Management Group
      item {
        Text(
          text = if (isBengali) "হিসাব ও লেনদেন সেবা" else "Finance Management",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column {
            MoreNavigationRow(
              title = if (isBengali) "আয় ও খরচ পরিচালনা" else "Income & Expenses",
              subtitle = if (isBengali) "ক্যাটেগরি ভিত্তিক আয় ও খরচের হিসাব" else "Manage income and expense categories",
              icon = Icons.Filled.SwapVert,
              iconTint = Color(0xFF10B981),
              onClick = { onNavigate(SubScreen.IncomeExpense) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            MoreNavigationRow(
              title = if (isBengali) "সকল লেনদেনের তালিকা" else "All Transactions",
              subtitle = if (isBengali) "তারিখ ও ক্যাটেগরি অনুযায়ী ফিল্টার ও সার্চ" else "Filter, search and review all transactions",
              icon = Icons.Filled.ReceiptLong,
              iconTint = Color(0xFF2563EB),
              onClick = { onNavigate(SubScreen.AllTransactions) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            MoreNavigationRow(
              title = if (isBengali) "হিসাব ও ওয়ালেট (Accounts)" else "Accounts & Wallets",
              subtitle = if (isBengali) "নগদ, বিকাশ, নগদ ও ব্যাংক ব্যালেন্স ও স্থানান্তর" else "Cash, bKash, Nagad, Bank and Transfers",
              icon = Icons.Filled.AccountBalanceWallet,
              iconTint = Color(0xFFE2136E),
              onClick = { onNavigate(SubScreen.Accounts) }
            )
          }
        }
      }

      // Analytics & Data Safety Group
      item {
        Text(
          text = if (isBengali) "রিপোর্ট ও ডাটা নিরাপত্তা" else "Reports & Data Security",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column {
            MoreNavigationRow(
              title = if (isBengali) "রিপোর্ট ও বিশ্লেষণ (Reports)" else "Reports & Analytics",
              subtitle = if (isBengali) "মাসিক চার্ট, মোট সম্পদ ও দায়ের গ্রাফ" else "Visual charts, monthly breakdown, and stats",
              icon = Icons.Filled.BarChart,
              iconTint = Color(0xFF7E22CE),
              onClick = { onNavigate(SubScreen.Reports) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            MoreNavigationRow(
              title = if (isBengali) "ব্যাকআপ ও রিস্টোর (Backup)" else "Backup & Restore",
              subtitle = if (isBengali) "JSON ফাইল ব্যাকআপ এক্সপোর্ট ও ডাটা রিস্টোর" else "Offline JSON backup and safe restoration",
              icon = Icons.Filled.CloudSync,
              iconTint = Color(0xFFF59E0B),
              onClick = { onNavigate(SubScreen.BackupRestore) }
            )
          }
        }
      }

      // App Settings & About
      item {
        Text(
          text = if (isBengali) "অ্যাপ সেটিংস ও তথ্য" else "App Settings",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column {
            MoreNavigationRow(
              title = if (isBengali) "সেটিংস ও নিরাপত্তা (PIN/Theme)" else "Settings & Security",
              subtitle = if (isBengali) "পিন লক, ডার্ক মোড, ভাষা ও ডেমো ডাটা" else "App PIN, Dark Mode, Language, Demo Data",
              icon = Icons.Filled.Settings,
              iconTint = DeepBlue,
              onClick = { onNavigate(SubScreen.Settings) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            MoreNavigationRow(
              title = if (isBengali) "অ্যাপ পরিচিতি (About Monethaka)" else "About Monethaka",
              subtitle = if (isBengali) "ভার্সন ১.০ ও উদ্দেশ্য" else "Version 1.0 details and mission",
              icon = Icons.Filled.Info,
              iconTint = Color(0xFF64748B),
              onClick = { onNavigate(SubScreen.About) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MoreNavigationRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = iconTint.copy(alpha = 0.12f),
      modifier = Modifier.size(44.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Icon(
      imageVector = Icons.Filled.ChevronRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.outline,
      modifier = Modifier.size(20.dp)
    )
  }
}
