package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
  val titleBn: String,
  val titleEn: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
) {
  HOME("হোম", "Home", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
  RECEIVABLES("পাওনা", "Receivables", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
  SHOP_DUES("দোকানের বাকি", "Shop Dues", Icons.Filled.Storefront, Icons.Outlined.Storefront),
  LOANS("ঋণ", "Loans", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
  MORE("অন্যান্য", "More", Icons.Filled.GridView, Icons.Outlined.GridView)
}

sealed class SubScreen {
  data object None : SubScreen()
  data class PersonDetail(val personId: Long) : SubScreen()
  data class ShopDetail(val shopId: Long) : SubScreen()
  data class LoanDetail(val loanId: Long) : SubScreen()
  data object IncomeExpense : SubScreen()
  data object AllTransactions : SubScreen()
  data object Reports : SubScreen()
  data object Accounts : SubScreen()
  data object BackupRestore : SubScreen()
  data object Settings : SubScreen()
  data object About : SubScreen()
}
