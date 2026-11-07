package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val lenderName: String,
  val phone: String? = null,
  val initialAmount: Double, // Total loan received
  val dateReceived: Long,
  val dueDate: Long? = null,
  val notes: String? = null,
  val accountId: Long? = null // Wallet where money was received
)

@Entity(tableName = "loan_repayments")
data class LoanRepaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val loanId: Long,
  val amount: Double,
  val date: Long,
  val accountId: Long? = null, // Wallet used to repay
  val note: String? = null
)
