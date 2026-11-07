package com.example.ui.screens.shops

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
import com.example.data.local.entity.ShopTransactionEntity
import com.example.model.ShopWithBalance
import com.example.ui.components.AddShopTransactionDialog
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.*
import com.example.ui.util.CurrencyFormatter
import com.example.ui.util.DateUtils
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
  shopId: Long,
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val shopsWithBalance by viewModel.shopsWithBalance.collectAsState()
  val accounts by viewModel.accounts.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()

  val shopWithBalance = shopsWithBalance.find { it.shop.id == shopId }
  val transactionsFlow = remember(shopId) { viewModel.getShopTransactions(shopId) }
  val transactions by transactionsFlow.collectAsState(initial = emptyList())

  var showAddTxnDialog by remember { mutableStateOf(false) }
  var isPurchaseForDialog by remember { mutableStateOf(true) }
  var txnToDelete by remember { mutableStateOf<ShopTransactionEntity?>(null) }
  var showDeleteShopConfirm by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("shop_detail_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = shopWithBalance?.shop?.shopName ?: (if (isBengali) "দোকানের হিসাব খাতা" else "Shop Ledger"),
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
          IconButton(onClick = { showDeleteShopConfirm = true }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Shop", tint = DebtRed)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    }
  ) { innerPadding ->
    if (shopWithBalance == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    } else {
      val shop = shopWithBalance.shop

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp)
      ) {
        // Shop Info & Ledger Summary Card
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
                  text = shop.shopName,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (!shop.ownerName.isNullOrEmpty()) {
                  Text(
                    text = "দোকানদার: ${shop.ownerName}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                if (!shop.phone.isNullOrEmpty()) {
                  Text(
                    text = shop.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (shopWithBalance.remainingDue > 0) DebtRedLight else Color(0xFFF1F5F9)
              ) {
                Column(
                  horizontalAlignment = Alignment.End,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = if (isBengali) "বাকি দেনা" else "Outstanding Due",
                    fontSize = 11.sp,
                    color = if (shopWithBalance.remainingDue > 0) DebtRedDark else Color(0xFF64748B)
                  )
                  Text(
                    text = CurrencyFormatter.format(shopWithBalance.remainingDue, isBengali, false),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (shopWithBalance.remainingDue > 0) DebtRedDark else Color(0xFF64748B)
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
                Text(text = if (isBengali) "মোট বাকি কেনাকাটা" else "Total Purchases", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(shopWithBalance.totalPurchases, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = if (isBengali) "মোট পরিশোধ করা হয়েছে" else "Total Paid", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = CurrencyFormatter.format(shopWithBalance.totalPaid, isBengali, false), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MoneyGreenDark)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(
                onClick = {
                  isPurchaseForDialog = true
                  showAddTxnDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "বাকি ক্রয়" else "Buy Credit", fontSize = 12.sp)
              }

              Button(
                onClick = {
                  isPurchaseForDialog = false
                  showAddTxnDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DebtRedDark)
              ) {
                Icon(Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "বাকি পরিশোধ" else "Pay Due", fontSize = 12.sp, color = Color.White)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = if (isBengali) "দোকানের খাতা ও লেনদেন বিবরণী" else "Purchase & Payment History",
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
              text = if (isBengali) "কোনো লেনদেন রেকর্ড নেই" else "No transactions recorded",
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
              val isPurchase = txn.type == "PURCHASE"
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
                      .background(if (isPurchase) DebtRedLight else MoneyGreenLight),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isPurchase) Icons.Filled.ShoppingBag else Icons.Filled.Payment,
                      contentDescription = null,
                      tint = if (isPurchase) DebtRedDark else MoneyGreenDark,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (isPurchase) (txn.productName ?: "বাকি কেনাকাটা")
                             else (if (isBengali) "বাকি পরিশোধ" else "Payment"),
                      fontSize = 14.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = DateUtils.formatDateTime(txn.date),
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!txn.quantity.isNullOrEmpty() || !txn.note.isNullOrEmpty()) {
                      Text(
                        text = "${txn.quantity ?: ""} ${if (!txn.note.isNullOrEmpty()) "(${txn.note})" else ""}".trim(),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                      )
                    }
                  }

                  Text(
                    text = (if (isPurchase) "+" else "-") + CurrencyFormatter.format(txn.amount, isBengali, false),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPurchase) DebtRed else MoneyGreenDark
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
    AddShopTransactionDialog(
      shopName = shopWithBalance?.shop?.shopName ?: "",
      isPurchase = isPurchaseForDialog,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { showAddTxnDialog = false },
      onConfirm = { product, quantity, amount, accountId, note ->
        viewModel.addShopTransaction(
          shopId = shopId,
          type = if (isPurchaseForDialog) "PURCHASE" else "PAYMENT",
          productName = product,
          quantity = quantity,
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
                else "Deleting this record will automatically recalculate balances.",
      isBengali = isBengali,
      onDismiss = { txnToDelete = null },
      onConfirm = {
        viewModel.deleteShopTransaction(txn)
        txnToDelete = null
      }
    )
  }

  if (showDeleteShopConfirm) {
    ConfirmDeleteDialog(
      title = if (isBengali) "দোকানের হিসাব মুছবেন?" else "Delete Shop?",
      message = if (isBengali) "এই দোকানের সমস্ত বাকি ও পরিশোধের রেকর্ড চিরতরে মুছে যাবে।"
                else "This shop and all purchase/payment records will be permanently deleted.",
      isBengali = isBengali,
      onDismiss = { showDeleteShopConfirm = false },
      onConfirm = {
        shopWithBalance?.shop?.let { viewModel.deleteShop(it) }
        showDeleteShopConfirm = false
        onBack()
      }
    )
  }
}
