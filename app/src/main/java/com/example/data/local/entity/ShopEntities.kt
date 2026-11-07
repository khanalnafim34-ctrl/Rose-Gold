package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class ShopEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val shopName: String,
  val ownerName: String? = null,
  val phone: String? = null,
  val address: String? = null,
  val notes: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shop_transactions")
data class ShopTransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val shopId: Long,
  val type: String, // "PURCHASE" (Credit purchase/due increased) or "PAYMENT" (Paid shop/due reduced)
  val productName: String? = null,
  val quantity: String? = null,
  val amount: Double,
  val date: Long,
  val dueDate: Long? = null,
  val note: String? = null,
  val accountId: Long? = null // Wallet used for payment
)
