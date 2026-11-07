package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AccountEntity
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.MoneyGreenDark

@Composable
fun AddIncomeExpenseDialog(
  isIncome: Boolean,
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, category: String, accountId: Long, note: String?) -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }

  val defaultIncomeCategories = listOf("বেতন (Salary)", "ব্যবসা (Business)", "ফ্রিল্যান্সিং (Freelance)", "বিনিয়োগ (Investment)", "অন্যান্য আয় (Other)")
  val defaultExpenseCategories = listOf("খাবার ও মুদি (Food)", "যাতায়াত (Transport)", "বিল ও ইউটিলিটি (Bills)", "শপিং (Shopping)", "চিকিৎসা (Health)", "অন্যান্য খরচ (Other)")
  val categories = if (isIncome) defaultIncomeCategories else defaultExpenseCategories

  var selectedCategory by remember { mutableStateOf(categories.first()) }
  var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 1L) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isIncome) (if (isBengali) "নতুন আয় যোগ করুন" else "Add Income")
               else (if (isBengali) "নতুন খরচ যোগ করুন" else "Add Expense"),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Amount input
        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorText != null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        // Category dropdown
        var catExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "ক্যাটেগরি" else "Category") },
            trailingIcon = {
              IconButton(onClick = { catExpanded = !catExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat) },
                onClick = {
                  selectedCategory = cat
                  catExpanded = false
                }
              )
            }
          }
        }

        // Account / Wallet dropdown
        var accExpanded by remember { mutableStateOf(false) }
        val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = currentAccount?.name ?: "হিসাব নির্বাচন করুন",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "পেমেন্ট মাধ্যম / হিসাব" else "Wallet / Account") },
            trailingIcon = {
              IconButton(onClick = { accExpanded = !accExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = accExpanded,
            onDismissRequest = { accExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  selectedAccountId = acc.id
                  accExpanded = false
                }
              )
            }
          }
        }

        // Note input
        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text(if (isBengali) "বিবরণ বা নোট (ঐচ্ছিক)" else "Description (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          onConfirm(amt, selectedCategory, selectedAccountId, noteText.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = if (isIncome) MoneyGreenDark else DebtRed),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddPersonDialog(
  isBengali: Boolean,
  accounts: List<AccountEntity>,
  onDismiss: () -> Unit,
  onConfirm: (name: String, phone: String, address: String?, notes: String?, initialDue: Double?, accountId: Long?) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var initialDue by remember { mutableStateOf("") }
  var selectedAccountId by remember { mutableStateOf<Long?>(accounts.firstOrNull()?.id) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isBengali) "নতুন ব্যক্তি যোগ করুন (পাওনা)" else "Add Person (Receivable)",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            errorText = null
          },
          label = { Text(if (isBengali) "ব্যক্তির নাম *" else "Person Name *") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(if (isBengali) "মোবাইল নম্বর" else "Mobile Number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = initialDue,
          onValueChange = { initialDue = it },
          label = { Text(if (isBengali) "পূর্বের পাওনা টাকা (ঐচ্ছিক)" else "Initial Amount Owed (Optional)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text(if (isBengali) "ঠিকানা (ঐচ্ছিক)" else "Address (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Notes (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank()) {
            errorText = if (isBengali) "নাম আবশ্যক" else "Name is required"
            return@Button
          }
          val dueAmt = initialDue.toDoubleOrNull()
          onConfirm(name.trim(), phone.trim(), address.ifEmpty { null }, notes.ifEmpty { null }, dueAmt, selectedAccountId)
        },
        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "যোগ করুন" else "Add Person", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddReceivableTransactionDialog(
  personName: String,
  isGiving: Boolean, // true = give more money, false = receive repayment
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, accountId: Long?, note: String?) -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }
  var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isGiving) (if (isBengali) "$personName-কে টাকা ধার দেওয়া" else "Lend money to $personName")
               else (if (isBengali) "$personName-এর কাছ থেকে পাওনা আদায়" else "Receive payment from $personName"),
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorText != null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        var accExpanded by remember { mutableStateOf(false) }
        val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = currentAccount?.name ?: "হিসাব",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isGiving) (if (isBengali) "যে হিসাব থেকে দিলেন" else "Paid from wallet") else (if (isBengali) "যে হিসাবে গ্রহণ করলেন" else "Received into wallet")) },
            trailingIcon = {
              IconButton(onClick = { accExpanded = !accExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = accExpanded,
            onDismissRequest = { accExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  selectedAccountId = acc.id
                  accExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          onConfirm(amt, selectedAccountId, noteText.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = if (isGiving) DeepBlue else MoneyGreenDark),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "নিশ্চিত করুন" else "Confirm", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddShopDialog(
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (shopName: String, ownerName: String?, phone: String?, address: String?, notes: String?, initialDue: Double?) -> Unit
) {
  var shopName by remember { mutableStateOf("") }
  var ownerName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var initialDue by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isBengali) "নতুন দোকান যোগ করুন" else "Add New Shop",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = shopName,
          onValueChange = {
            shopName = it
            errorText = null
          },
          label = { Text(if (isBengali) "দোকানের নাম *" else "Shop Name *") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
          value = ownerName,
          onValueChange = { ownerName = it },
          label = { Text(if (isBengali) "দোকানদারের নাম" else "Owner Name") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(if (isBengali) "মোবাইল নম্বর" else "Phone Number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = initialDue,
          onValueChange = { initialDue = it },
          label = { Text(if (isBengali) "পূর্বের বাকি টাকা (ঐচ্ছিক)" else "Previous Due Amount (Optional)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text(if (isBengali) "দোকানের ঠিকানা" else "Shop Address") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (shopName.isBlank()) {
            errorText = if (isBengali) "দোকানের নাম আবশ্যক" else "Shop name is required"
            return@Button
          }
          val dueAmt = initialDue.toDoubleOrNull()
          onConfirm(shopName.trim(), ownerName.ifEmpty { null }, phone.ifEmpty { null }, address.ifEmpty { null }, notes.ifEmpty { null }, dueAmt)
        },
        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddShopTransactionDialog(
  shopName: String,
  isPurchase: Boolean, // true = purchase on credit, false = pay shop
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (productName: String?, quantity: String?, amount: Double, accountId: Long?, note: String?) -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var productText by remember { mutableStateOf("") }
  var quantityText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }
  var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isPurchase) (if (isBengali) "$shopName-এ বাকি কেনাকাটা" else "Credit Purchase at $shopName")
               else (if (isBengali) "$shopName-কে বাকি পরিশোধ" else "Pay Due to $shopName"),
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorText != null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        if (isPurchase) {
          OutlinedTextField(
            value = productText,
            onValueChange = { productText = it },
            label = { Text(if (isBengali) "পণ্যের নাম বা বিবরণ" else "Product / Items") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it },
            label = { Text(if (isBengali) "পরিমাণ (ঐচ্ছিক)" else "Quantity (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
        } else {
          // If payment: select wallet
          var accExpanded by remember { mutableStateOf(false) }
          val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
          Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = currentAccount?.name ?: "হিসাব নির্বাচন",
              onValueChange = {},
              readOnly = true,
              label = { Text(if (isBengali) "যে হিসাব থেকে পরিশোধ করলেন" else "Paid from wallet") },
              trailingIcon = {
                IconButton(onClick = { accExpanded = !accExpanded }) {
                  Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )
            DropdownMenu(
              expanded = accExpanded,
              onDismissRequest = { accExpanded = false }
            ) {
              accounts.forEach { acc ->
                DropdownMenuItem(
                  text = { Text(acc.name) },
                  onClick = {
                    selectedAccountId = acc.id
                    accExpanded = false
                  }
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          onConfirm(productText.ifEmpty { null }, quantityText.ifEmpty { null }, amt, if (!isPurchase) selectedAccountId else null, noteText.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = if (isPurchase) DebtRed else MoneyGreenDark),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "নিশ্চিত করুন" else "Confirm", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddLoanDialog(
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (lenderName: String, phone: String?, amount: Double, accountId: Long?, notes: String?) -> Unit
) {
  var lenderName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var selectedAccountId by remember { mutableStateOf<Long?>(accounts.firstOrNull()?.id) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isBengali) "নতুন ঋণ গ্রহণ হিসাব" else "Add New Personal Loan",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = lenderName,
          onValueChange = {
            lenderName = it
            errorText = null
          },
          label = { Text(if (isBengali) "ঋণদাতার নাম *" else "Lender Name *") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "ঋণের পরিমাণ (৳) *" else "Loan Amount (৳) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(if (isBengali) "মোবাইল নম্বর (ঐচ্ছিক)" else "Phone Number (Optional)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        // Wallet where money was received
        var accExpanded by remember { mutableStateOf(false) }
        val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = currentAccount?.name ?: "হিসাব",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "ঋণের টাকা জমা হওয়ার হিসাব" else "Deposit into wallet") },
            trailingIcon = {
              IconButton(onClick = { accExpanded = !accExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = accExpanded,
            onDismissRequest = { accExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  selectedAccountId = acc.id
                  accExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Notes (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (lenderName.isBlank()) {
            errorText = if (isBengali) "ঋণদাতার নাম আবশ্যক" else "Lender name required"
            return@Button
          }
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          onConfirm(lenderName.trim(), phone.ifEmpty { null }, amt, selectedAccountId, notes.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "সংরক্ষণ" else "Save", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun AddLoanRepaymentDialog(
  lenderName: String,
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double, accountId: Long?, note: String?) -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }
  var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isBengali) "$lenderName-এর ঋণ পরিশোধ" else "Repay Loan to $lenderName",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "পরিশোধের পরিমাণ (৳)" else "Repayment Amount (৳)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorText != null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        var accExpanded by remember { mutableStateOf(false) }
        val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = currentAccount?.name ?: "হিসাব",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "যে হিসাব থেকে পরিশোধ করলেন" else "Paid from wallet") },
            trailingIcon = {
              IconButton(onClick = { accExpanded = !accExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = accExpanded,
            onDismissRequest = { accExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  selectedAccountId = acc.id
                  accExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          onConfirm(amt, selectedAccountId, noteText.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = MoneyGreenDark),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "পরিশোধ করুন" else "Repay", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun TransferMoneyDialog(
  accounts: List<AccountEntity>,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (fromAccountId: Long, toAccountId: Long, amount: Double, note: String?) -> Unit
) {
  var fromId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 1L) }
  var toId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: (accounts.firstOrNull()?.id ?: 2L)) }
  var amountText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = if (isBengali) "হিসাব থেকে হিসাবে স্থানান্তর" else "Transfer Between Accounts",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = if (isBengali) "নিজস্ব হিসাবের মধ্যে স্থানান্তর কোনো আয় বা খরচ হিসেবে গণনা করা হয় না।"
                 else "Internal transfers are not counted as income or expenses.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = amountText,
          onValueChange = {
            amountText = it
            errorText = null
          },
          label = { Text(if (isBengali) "টাকার পরিমাণ (৳)" else "Amount (৳)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          isError = errorText != null,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
        if (errorText != null) {
          Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        // From Account
        var fromExpanded by remember { mutableStateOf(false) }
        val fromAcc = accounts.find { it.id == fromId } ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = fromAcc?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "যে হিসাব থেকে যাবে (From)" else "From Account") },
            trailingIcon = {
              IconButton(onClick = { fromExpanded = !fromExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = fromExpanded,
            onDismissRequest = { fromExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  fromId = acc.id
                  fromExpanded = false
                }
              )
            }
          }
        }

        // To Account
        var toExpanded by remember { mutableStateOf(false) }
        val toAcc = accounts.find { it.id == toId } ?: accounts.getOrNull(1) ?: accounts.firstOrNull()
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = toAcc?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isBengali) "যে হিসাবে জমা হবে (To)" else "To Account") },
            trailingIcon = {
              IconButton(onClick = { toExpanded = !toExpanded }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
              }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = toExpanded,
            onDismissRequest = { toExpanded = false }
          ) {
            accounts.forEach { acc ->
              DropdownMenuItem(
                text = { Text(acc.name) },
                onClick = {
                  toId = acc.id
                  toExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text(if (isBengali) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorText = if (isBengali) "সঠিক পরিমাণ লিখুন" else "Enter a valid amount"
            return@Button
          }
          if (fromId == toId) {
            errorText = if (isBengali) "দুটি ভিন্ন হিসাব নির্বাচন করুন" else "Select two different accounts"
            return@Button
          }
          onConfirm(fromId, toId, amt, noteText.ifEmpty { null })
        },
        colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "স্থানান্তর করুন" else "Transfer", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}

@Composable
fun ConfirmDeleteDialog(
  title: String,
  message: String,
  isBengali: Boolean,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(18.dp),
    title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
    text = { Text(text = message, fontSize = 14.sp) },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = DebtRed),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text(if (isBengali) "মুছে ফেলুন" else "Delete", color = Color.White)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(if (isBengali) "বাতিল" else "Cancel")
      }
    }
  )
}
