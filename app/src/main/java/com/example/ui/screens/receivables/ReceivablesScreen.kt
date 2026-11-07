package com.example.ui.screens.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
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
import com.example.model.PersonWithBalance
import com.example.ui.components.AddPersonDialog
import com.example.ui.components.AddReceivableTransactionDialog
import com.example.ui.components.PersonCard
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.MoneyGreenDark
import com.example.ui.util.CurrencyFormatter
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivablesScreen(
  viewModel: FinanceViewModel,
  onOpenPersonDetail: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val personsWithBalance by viewModel.personsWithBalance.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val accounts by viewModel.accounts.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var sortByRemainingDesc by remember { mutableStateOf(true) }

  var showAddPersonDialog by remember { mutableStateOf(false) }
  var selectedPersonForPayment by remember { mutableStateOf<PersonWithBalance?>(null) }
  var isGivingForDialog by remember { mutableStateOf(false) }

  val filteredPersons = remember(personsWithBalance, searchQuery, sortByRemainingDesc) {
    val list = if (searchQuery.isBlank()) {
      personsWithBalance
    } else {
      personsWithBalance.filter {
        it.person.name.contains(searchQuery, ignoreCase = true) ||
        it.person.phone.contains(searchQuery, ignoreCase = true)
      }
    }
    if (sortByRemainingDesc) {
      list.sortedByDescending { it.remainingDue }
    } else {
      list.sortedBy { it.person.name }
    }
  }

  val totalReceivableSum = remember(personsWithBalance) {
    personsWithBalance.sumOf { kotlin.math.max(0.0, it.remainingDue) }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("receivables_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "পাওনা হিসাব" else "Receivables Ledger",
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
        onClick = { showAddPersonDialog = true },
        containerColor = DeepBlue,
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_add_person")
      ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Person")
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
              text = if (isBengali) "সর্বমোট পাওনা (Others Owe Me)" else "Total Receivables",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = CurrencyFormatter.format(totalReceivableSum, isBengali, false),
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = MoneyGreenDark
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
          ) {
            Text(
              text = "${personsWithBalance.size} ${if (isBengali) "জন ব্যক্তি" else "Persons"}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search & Filter
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(if (isBengali) "নাম বা মোবাইল দিয়ে খুঁজুন..." else "Search by name or phone...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // List of Persons
      if (filteredPersons.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.People,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (searchQuery.isNotEmpty()) {
                if (isBengali) "কোনো তথ্য মেলেনি" else "No matching results"
              } else {
                if (isBengali) "কোনো পাওনা হিসাব নেই" else "No receivables added yet"
              },
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isBengali) "+ বাটনে চাপ দিয়ে নতুন ব্যক্তি যোগ করুন" else "Tap + to add a person",
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
          items(filteredPersons, key = { it.person.id }) { item ->
            PersonCard(
              personWithBalance = item,
              isBengali = isBengali,
              onClick = { onOpenPersonDetail(item.person.id) },
              onReceivePayment = {
                selectedPersonForPayment = item
                isGivingForDialog = false
              },
              onGiveMore = {
                selectedPersonForPayment = item
                isGivingForDialog = true
              }
            )
          }
        }
      }
    }
  }

  // Dialogs
  if (showAddPersonDialog) {
    AddPersonDialog(
      isBengali = isBengali,
      accounts = accounts,
      onDismiss = { showAddPersonDialog = false },
      onConfirm = { name, phone, address, notes, initialDue, accId ->
        viewModel.addPerson(name, phone, address, notes, initialDue, accId)
        showAddPersonDialog = false
      }
    )
  }

  selectedPersonForPayment?.let { p ->
    AddReceivableTransactionDialog(
      personName = p.person.name,
      isGiving = isGivingForDialog,
      accounts = accounts,
      isBengali = isBengali,
      onDismiss = { selectedPersonForPayment = null },
      onConfirm = { amount, accountId, note ->
        viewModel.addReceivableTransaction(
          personId = p.person.id,
          type = if (isGivingForDialog) "GIVEN" else "RECEIVED",
          amount = amount,
          date = System.currentTimeMillis(),
          dueDate = null,
          note = note,
          accountId = accountId
        )
        selectedPersonForPayment = null
      }
    )
  }
}
