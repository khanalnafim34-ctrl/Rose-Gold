package com.example.ui.screens.more

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.DebtRed
import com.example.ui.theme.DeepBlue
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val isBengali by viewModel.isBengali.collectAsState()

  var exportedJson by remember { mutableStateOf<String?>(null) }
  var restoreJsonInput by remember { mutableStateOf("") }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  var showResetConfirm by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("backup_restore_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "ব্যাকআপ ও ডাটা রিস্টোর" else "Backup & Restore",
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
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Export Backup Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isBengali) "১. ডাটা ব্যাকআপ তৈরি করুন (Export JSON)" else "1. Export Data Backup",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = if (isBengali) "আপনার হিসাবের সকল তথ্য (পাওনা, বাকি, ঋণ, আয়-ব্যয়) একটি সুরক্ষিত JSON টেক্সট হিসেবে ব্যাকআপ রাখুন।"
                   else "Generate a secure offline JSON backup of all financial accounts and records.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = {
              coroutineScope.launch {
                val json = viewModel.exportJson()
                exportedJson = json
                statusMessage = if (isBengali) "ব্যাকআপ সফলভাবে তৈরি হয়েছে!" else "Backup created successfully!"
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isBengali) "ব্যাকআপ ফাইল তৈরি করুন" else "Generate Backup JSON", color = Color.White)
          }

          if (exportedJson != null) {
            OutlinedTextField(
              value = exportedJson!!,
              onValueChange = {},
              readOnly = true,
              label = { Text("Backup JSON") },
              modifier = Modifier.fillMaxWidth().height(140.dp),
              shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("Monethaka Backup", exportedJson))
                  Toast.makeText(context, if (isBengali) "ক্লিপবোর্ডে কপি হয়েছে!" else "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "কপি করুন" else "Copy", fontSize = 12.sp)
              }

              Button(
                onClick = {
                  val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Monethaka_Backup.json")
                    putExtra(Intent.EXTRA_TEXT, exportedJson)
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Share Monethaka Backup"))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DeepBlue)
              ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "শেয়ার করুন" else "Share", fontSize = 12.sp, color = Color.White)
              }
            }
          }
        }
      }

      // 2. Restore Backup Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Restore, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isBengali) "২. পূর্বে সংরক্ষিত ব্যাকআপ রিস্টোর করুন" else "2. Restore Backup",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = if (isBengali) "পূর্বে তৈরি করা JSON ব্যাকআপ কোড নিচে পেস্ট করে 'রিস্টোর করুন' বাটনে চাপ দিন।"
                   else "Paste previously exported backup JSON code below and tap restore.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = restoreJsonInput,
            onValueChange = { restoreJsonInput = it },
            placeholder = { Text(if (isBengali) "এখানে ব্যাকআপ JSON পেস্ট করুন..." else "Paste backup JSON here...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(10.dp)
          )

          Button(
            onClick = {
              if (restoreJsonInput.isBlank()) {
                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে JSON কোড পেস্ট করুন" else "Please paste JSON content", Toast.LENGTH_SHORT).show()
                return@Button
              }
              coroutineScope.launch {
                val success = viewModel.restoreJson(restoreJsonInput.trim())
                if (success) {
                  statusMessage = if (isBengali) "ডাটা সফলভাবে রিস্টোর হয়েছে!" else "Data restored successfully!"
                  Toast.makeText(context, statusMessage, Toast.LENGTH_LONG).show()
                  restoreJsonInput = ""
                } else {
                  Toast.makeText(context, if (isBengali) "রিস্টোর ব্যর্থ হয়েছে! সঠিক JSON দিন।" else "Restore failed! Invalid JSON.", Toast.LENGTH_LONG).show()
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isBengali) "ডাটা রিস্টোর করুন" else "Restore Data", color = Color.White)
          }
        }
      }

      // 3. Reset All Data Card
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
            text = if (isBengali) "সমস্ত ডেটা পরিষ্কার করুন (Reset All)" else "Reset All Data",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DebtRed
          )
          Text(
            text = if (isBengali) "অ্যাপের সমস্ত রেকর্ড মুছে ফেলে প্রাথমিক ডিফল্ট অবস্থায় নিয়ে যাওয়া হবে।"
                   else "Erase all accounts, records and start fresh.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedButton(
            onClick = { showResetConfirm = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DebtRed),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isBengali) "সকল ডাটা মুছে ফেলুন" else "Clear All Data")
          }
        }
      }
    }
  }

  if (showResetConfirm) {
    ConfirmDeleteDialog(
      title = if (isBengali) "সকল ডেটা মুছে ফেলবেন?" else "Reset Everything?",
      message = if (isBengali) "এই কাজটি সম্পূর্ণ অপরিবর্তনীয়। আপনার সমস্ত পাওনা, দোকানের বাকি, ঋণ ও লেনদেন চিরতরে মুছে যাবে।"
                else "This will permanently delete all records. Are you sure?",
      isBengali = isBengali,
      onDismiss = { showResetConfirm = false },
      onConfirm = {
        viewModel.clearAllData()
        showResetConfirm = false
        Toast.makeText(context, if (isBengali) "সকল ডেটা মুছে ফেলা হয়েছে" else "All data cleared", Toast.LENGTH_SHORT).show()
      }
    )
  }
}
