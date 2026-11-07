package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val phone: String = "",
  val address: String? = null,
  val notes: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "receivable_transactions")
data class ReceivableTransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val personId: Long,
  val type: String, // "GIVEN" (I gave money/they owe me) or "RECEIVED" (they repaid me)
  val amount: Double,
  val date: Long,
  val dueDate: Long? = null,
  val note: String? = null,
  val accountId: Long? = null // Wallet used
)
