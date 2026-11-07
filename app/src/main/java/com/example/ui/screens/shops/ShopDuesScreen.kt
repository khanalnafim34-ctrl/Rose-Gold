package com.example.ui.screens.shops

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ShopWithBalance
import com.example.ui.components.AddShopDialog
import com.example.ui.components.AddShopTransactionDialog
import com.example.ui.components.ShopCard
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DebtRedDark
import com.example.ui.theme.DeepBlue
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDuesScreen(
  viewModel: FinanceViewModel,
  onOpenShopDetail: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val shopsWithBalance by viewModel.shopsWithBalance.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val accounts by viewModel.accounts.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var showAddShopDialog by remember { mutableStateOf(false) }

  var selectedShopForTxn by remember { mutableStateOf<ShopWithBalance?>(null) }
  var isPurchaseForDialog by remember { mutableStateOf(true) }

  val filteredShops = remember(shopsWithBalance, searchQuery) {
    if (searchQuery.isBlank()) {
      shopsWithBalance.sortedByDescending { it.remainingDue }
    } else {
      shopsWithBalance.filter {
        it.shop.shopName.contains(searchQuery, ignoreCase = true) ||
        (it.shop.ownerName?.contains(searchQuery, ignoreCase = true) == true)
      }.sortedByDescending { it.remainingDue }
    }
  }

  val totalShopDueSum = remember(shopsWithBalance) {
    shopsWithBalance.sumOf { kotlin.math.max(0.0, it.remainingDue) }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("shops_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "দোকানের বাকি" else "Shop Dues",
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
        onClick = { showAddShopDialog = true },
        containerColor = DeepBlue,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_shop")
      ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Shop")
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
              text = if (isBengali) "দোকানে মোট বাকি দেনা" else "Total Shop Dues",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = CurrencyFormatter.format(totalShopDueSum, isBengali, false),
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
              text = "${shopsWithBalance.size} ${if (isBengali) "টি দোকান" else "Shops"}",
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
        placeholder = { Text(if (isBengali) "দোকানের নাম দিয়ে খুঁজুন..." else "Search shop name...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // List of Shops
      if (filteredShops.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.Store,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) {
                if (isBengali) "কোনো দোকান মেলেনি" else "No matching shops"
              } else {
                if (isBengali) "কোনো দোকানের বাকি নেই" else "No shop dues added yet"
              },
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isBengali) "+ বাটনে চাপ দিয়ে নতুন দোকান যোগ করুন" else "Tap + to add a shop",
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
          items(filteredShops, key = { it.shop.id }) { item ->
            ShopCard(
              shopWithBalance = item,
              isBengali = isBengali,
              onClick = { onOpenShopDetail(item.shop.id) },
              onPayDue = {
                selectedShopForTxn = item
                isPurchaseForDialog = false
              },
              onBuyCredit = {
                selectedShopForTxn = item
                isPurchaseForDialog = true
              }
            )
          }
        }
      }
    }
  }

  // Dialogs
  if (showAddShopDialog) {
    AddShopDialog(
      isBengali = isBengali,
      onDismiss = { showAddShopDialog = false },
      onConfirm = { shopName, ownerName, phone, address, notes, initialDue ->
        viewModel.addShop(shopName, ownerName, phone, address, notes, initialDue)
        showAddShopDialog = false
      }
    )
  }

  selectedShopForTxn?.let { s ->
    AddShopTransactionDialog(
      shopName = s.shop.shopName,
      isPurchase = isPurchaseForDialog,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { selectedShopForTxn = null },
      onConfirm = { product, quantity, amount, accountId, note ->
        viewModel.addShopTransaction(
          shopId = s.shop.id,
          type = if (isPurchaseForDialog) "PURCHASE" else "PAYMENT",
          productName = product,
          quantity = quantity,
          amount = amount,
          date = System.currentTimeMillis(),
          dueDate = null,
          note = note,
          accountId = accountId
        )
        selectedShopForTxn = null
      }
    )
  }
}
