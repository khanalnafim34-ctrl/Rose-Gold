package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val type: String, // CASH, BKASH, NAGAD, BANK, OTHER
  val initialBalance: Double = 0.0,
  val isDefault: Boolean = false,
  val colorHex: String? = null
)
