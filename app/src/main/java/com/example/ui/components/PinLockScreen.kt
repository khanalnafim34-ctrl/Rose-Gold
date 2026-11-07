package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
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
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.NavyPrimary

@Composable
fun PinLockScreen(
  isBengali: Boolean,
  onUnlockAttempt: (String) -> Boolean,
  modifier: Modifier = Modifier
) {
  var enteredPin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("pin_lock_screen"),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 32.dp, vertical = 48.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Header
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp)
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .background(DeepBlue.copy(alpha = 0.1f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = "Lock",
            tint = DeepBlue,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = if (isBengali) "মনেথাকা অ্যাপ লক" else "Monethaka App Lock",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (isError) {
            if (isBengali) "ভুল পিন! পুনরায় চেষ্টা করুন" else "Incorrect PIN! Try again"
          } else {
            if (isBengali) "আপনার ৪ ডিজিটের পিন কোড দিন" else "Enter your 4-digit PIN"
          },
          fontSize = 14.sp,
          color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 4 Pin Dots
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
          repeat(4) { index ->
            val isFilled = index < enteredPin.length
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                  if (isFilled) DeepBlue
                  else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )
          }
        }
      }

      // Numeric Keypad
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        val keys = listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9"),
          listOf("", "0", "DEL")
        )

        keys.forEach { row ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            row.forEach { key ->
              if (key.isEmpty()) {
                Spacer(modifier = Modifier.size(72.dp))
              } else if (key == "DEL") {
                Box(
                  modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable {
                      if (enteredPin.isNotEmpty()) {
                        enteredPin = enteredPin.dropLast(1)
                        isError = false
                      }
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                  )
                }
              } else {
                Box(
                  modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .clickable {
                      if (enteredPin.length < 4) {
                        enteredPin += key
                        isError = false
                        if (enteredPin.length == 4) {
                          val success = onUnlockAttempt(enteredPin)
                          if (!success) {
                            isError = true
                            enteredPin = ""
                          }
                        }
                      }
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = key,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
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
