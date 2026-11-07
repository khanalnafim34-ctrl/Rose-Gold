package com.example.model

import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.LoanEntity
import com.example.data.local.entity.PersonEntity
import com.example.data.local.entity.ShopEntity

data class FinancialSummary(
  val totalAssets: Double = 0.0,
  val totalLiabilities: Double = 0.0,
  val netBalance: Double = 0.0,
  val totalReceivables: Double = 0.0,
  val totalLoanReceived: Double = 0.0,
  val totalLoanRepaid: Double = 0.0,
  val remainingPersonalLoans: Double = 0.0,
  val remainingShopDues: Double = 0.0,
  val accountBalances: Map<Long, Double> = emptyMap()
)

enum class TransactionType {
  INCOME,
  EXPENSE,
  RECEIVABLE_GIVEN,
  RECEIVABLE_COLLECTED,
  SHOP_PURCHASE,
  SHOP_PAYMENT,
  LOAN_BORROWED,
  LOAN_REPAID,
  TRANSFER
}

data class UnifiedTransaction(
  val id: Long,
  val type: TransactionType,
  val title: String,
  val subtitle: String?,
  val amount: Double,
  val date: Long,
  val accountName: String?,
  val accountId: Long?,
  val note: String? = null,
  val category: String? = null,
  val originalEntityId: Long = 0,
  val secondaryEntityId: Long? = null
)

data class PersonWithBalance(
  val person: PersonEntity,
  val totalGiven: Double,
  val totalReceived: Double,
  val remainingDue: Double,
  val lastTransactionDate: Long? = null
)

data class ShopWithBalance(
  val shop: ShopEntity,
  val totalPurchases: Double,
  val totalPaid: Double,
  val remainingDue: Double,
  val lastTransactionDate: Long? = null
)

data class LoanWithBalance(
  val loan: LoanEntity,
  val initialAmount: Double,
  val totalRepaid: Double,
  val remainingAmount: Double,
  val lastRepaymentDate: Long? = null
)

data class AccountWithBalance(
  val account: AccountEntity,
  val currentBalance: Double
)
