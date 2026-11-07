package com.example.ui.screens.more

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepBlue
import com.example.ui.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: FinanceViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current

  val darkMode by viewModel.darkMode.collectAsState()
  val isBengali by viewModel.isBengali.collectAsState()
  val appPin by viewModel.appPin.collectAsState()
  val isDemoLoaded by viewModel.isDemoLoaded.collectAsState()

  var showPinDialog by remember { mutableStateOf(false) }
  var pinInput by remember { mutableStateOf("") }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("settings_screen"),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isBengali) "সেটিংস ও নিরাপত্তা" else "Settings & Security",
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
      // 1. Language Option
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (isBengali) "অ্যাপের ভাষা (Language)" else "App Language",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilterChip(
              selected = isBengali,
              onClick = { viewModel.setIsBengali(true) },
              label = { Text("বাংলা (Bengali)") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
            FilterChip(
              selected = !isBengali,
              onClick = { viewModel.setIsBengali(false) },
              label = { Text("English") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 2. Dark Mode Options
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (isBengali) "থিম ও ডিসপ্লে (Theme)" else "Appearance & Theme",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = darkMode == "LIGHT",
              onClick = { viewModel.setDarkMode("LIGHT") },
              label = { Text(if (isBengali) "লাইট" else "Light") },
              shape = RoundedCornerShape(10.dp)
            )
            FilterChip(
              selected = darkMode == "DARK",
              onClick = { viewModel.setDarkMode("DARK") },
              label = { Text(if (isBengali) "ডার্ক" else "Dark") },
              shape = RoundedCornerShape(10.dp)
            )
            FilterChip(
              selected = darkMode == "SYSTEM",
              onClick = { viewModel.setDarkMode("SYSTEM") },
              label = { Text(if (isBengali) "সিস্টেম" else "System") },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }

      // 3. PIN Lock Security
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isBengali) "৪ ডিজিটের পিন লক (PIN Security)" else "4-digit PIN Security",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (appPin.isNullOrEmpty()) {
                  if (isBengali) "পিন লক নিষ্ক্রিয় রয়েছে" else "PIN lock is disabled"
                } else {
                  if (isBengali) "পিন লক সক্রিয় রয়েছে (সুরক্ষিত)" else "PIN lock is enabled"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Switch(
              checked = !appPin.isNullOrEmpty(),
              onCheckedChange = { checked ->
                if (checked) {
                  pinInput = ""
                  showPinDialog = true
                } else {
                  viewModel.setAppPin(null)
                  Toast.makeText(context, if (isBengali) "পিন লক সরানো হয়েছে" else "PIN lock disabled", Toast.LENGTH_SHORT).show()
                }
              }
            )
          }

          if (!appPin.isNullOrEmpty()) {
            OutlinedButton(
              onClick = {
                pinInput = ""
                showPinDialog = true
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(if (isBengali) "পিন কোড পরিবর্তন করুন" else "Change PIN")
            }
          }
        }
      }

      // 4. Sample Demo Data
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = if (isBengali) "ডেমো ডেটা ব্যবস্থাপনা (Sample Demo Data)" else "Demo Sample Data",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isBengali) "বাস্তবধর্মী নমুনা ডেটা লোড করে অ্যাপ্লিকেশনটি পরীক্ষা করুন। পরবর্তীতে প্রয়োজনমতো মুছে ফেলা যাবে।"
                   else "Load realistic sample finance records matching the official test scenario.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
              onClick = {
                viewModel.loadDemoData()
                Toast.makeText(context, if (isBengali) "ডেমো ডেটা লোড হয়েছে!" else "Demo data loaded!", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = if (isBengali) "ডেমো ডাটা লোড" else "Load Demo", fontSize = 12.sp, color = Color.White)
            }

            if (isDemoLoaded) {
              OutlinedButton(
                onClick = {
                  viewModel.removeDemoData()
                  Toast.makeText(context, if (isBengali) "ডেমো ডেটা মুছে ফেলা হয়েছে" else "Demo data removed", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isBengali) "ডেমো মুছুন" else "Remove Demo", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }

  // Set PIN Dialog
  if (showPinDialog) {
    var errorText by remember { mutableStateOf<String?>(null) }
    AlertDialog(
      onDismissRequest = { showPinDialog = false },
      shape = RoundedCornerShape(20.dp),
      title = { Text(if (isBengali) "৪ ডিজিটের পিন কোড সেট করুন" else "Set 4-digit PIN", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = pinInput,
            onValueChange = {
              if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                pinInput = it
                errorText = null
              }
            },
            label = { Text("PIN (৪ ডিজিট)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
          )
          if (errorText != null) {
            Text(text = errorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (pinInput.length != 4) {
              errorText = if (isBengali) "ঠিক ৪ ডিজিটের পিন আবশ্যক" else "Exactly 4 digits required"
              return@Button
            }
            viewModel.setAppPin(pinInput)
            showPinDialog = false
            Toast.makeText(context, if (isBengali) "পিন সফলভাবে সংরক্ষিত হয়েছে!" else "PIN saved successfully!", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text(if (isBengali) "সংরক্ষণ" else "Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showPinDialog = false }) {
          Text(if (isBengali) "বাতিল" else "Cancel")
        }
      }
    )
  }
}
