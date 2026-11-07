package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PinLockScreen
import com.example.ui.navigation.MainTab
import com.example.ui.navigation.SubScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.loans.LoanDetailScreen
import com.example.ui.screens.loans.LoansScreen
import com.example.ui.screens.more.*
import com.example.ui.screens.receivables.PersonDetailScreen
import com.example.ui.screens.receivables.ReceivablesScreen
import com.example.ui.screens.shops.ShopDetailScreen
import com.example.ui.screens.shops.ShopDuesScreen
import com.example.ui.theme.MonethakaTheme
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun MonethakaApp(viewModel: FinanceViewModel = viewModel()) {
  val darkModePref by viewModel.darkMode.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val isUnlocked by viewModel.isUnlocked.collectAsState()

  val isDarkTheme = when (darkModePref) {
    "DARK" -> true
    "LIGHT" -> false
    else -> isSystemInDarkTheme()
  }

  MonethakaTheme(darkTheme = isDarkTheme) {
    if (!isUnlocked) {
      PinLockScreen(
        isBengali = isBengali,
        onUnlockAttempt = { pin -> viewModel.unlockApp(pin) }
      )
    } else {
      var selectedTab by remember { mutableStateOf(MainTab.HOME) }
      var currentSubScreen by remember { mutableStateOf<SubScreen>(SubScreen.None) }

      Scaffold(
        modifier = Modifier.fillMaxSize().testTag("monethaka_root_scaffold"),
        bottomBar = {
          if (currentSubScreen == SubScreen.None) {
            NavigationBar(
              modifier = Modifier.navigationBarsPadding(),
              tonalElevation = 8.dp
            ) {
              MainTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                NavigationBarItem(
                  selected = selected,
                  onClick = {
                    selectedTab = tab
                    currentSubScreen = SubScreen.None
                  },
                  icon = {
                    Icon(
                      imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                      contentDescription = if (isBengali) tab.titleBn else tab.titleEn
                    )
                  },
                  label = {
                    Text(
                      text = if (isBengali) tab.titleBn else tab.titleEn,
                      fontSize = 11.sp,
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                )
              }
            }
          }
        }
      ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        AnimatedContent(
          targetState = Pair(selectedTab, currentSubScreen),
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "ScreenTransition"
        ) { (tab, subScreen) ->
          when (subScreen) {
            is SubScreen.PersonDetail -> {
              PersonDetailScreen(
                personId = subScreen.personId,
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.ShopDetail -> {
              ShopDetailScreen(
                shopId = subScreen.shopId,
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.LoanDetail -> {
              LoanDetailScreen(
                loanId = subScreen.loanId,
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.IncomeExpense -> {
              IncomeExpenseScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.AllTransactions -> {
              AllTransactionsScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.Accounts -> {
              AccountsScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.Reports -> {
              ReportsScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.BackupRestore -> {
              BackupRestoreScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.Settings -> {
              SettingsScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            is SubScreen.About -> {
              AboutScreen(
                viewModel = viewModel,
                onBack = { currentSubScreen = SubScreen.None },
                modifier = contentModifier
              )
            }
            SubScreen.None -> {
              when (tab) {
                MainTab.HOME -> {
                  DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTransactions = { currentSubScreen = SubScreen.AllTransactions },
                    modifier = contentModifier
                  )
                }
                MainTab.RECEIVABLES -> {
                  ReceivablesScreen(
                    viewModel = viewModel,
                    onOpenPersonDetail = { personId ->
                      currentSubScreen = SubScreen.PersonDetail(personId)
                    },
                    modifier = contentModifier
                  )
                }
                MainTab.SHOP_DUES -> {
                  ShopDuesScreen(
                    viewModel = viewModel,
                    onOpenShopDetail = { shopId ->
                      currentSubScreen = SubScreen.ShopDetail(shopId)
                    },
                    modifier = contentModifier
                  )
                }
                MainTab.LOANS -> {
                  LoansScreen(
                    viewModel = viewModel,
                    onOpenLoanDetail = { loanId ->
                      currentSubScreen = SubScreen.LoanDetail(loanId)
                    },
                    modifier = contentModifier
                  )
                }
                MainTab.MORE -> {
                  MoreScreen(
                    viewModel = viewModel,
                    onNavigate = { destination ->
                      currentSubScreen = destination
                    },
                    modifier = contentModifier
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
