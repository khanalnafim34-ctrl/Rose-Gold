package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_expenses")
data class IncomeExpenseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String, // "INCOME" or "EXPENSE"
  val category: String,
  val amount: Double,
  val date: Long,
  val accountId: Long,
  val description: String? = null
)

@Entity(tableName = "account_transfers")
data class AccountTransferEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val fromAccountId: Long,
  val toAccountId: Long,
  val amount: Double,
  val date: Long,
  val note: String? = null
)
