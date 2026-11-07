package com.example

import com.example.data.local.entity.*
import com.example.data.repository.FinanceRepository
import com.example.ui.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testExactPromptFinancialFormulaScenario() {
    // Exact prompt scenario:
    // Cash = 10,000, bKash = 5,000, Nagad = 5,000, Bank = 10,000 -> Total Assets = 30,000
    // Personal Loan Received = 20,000, Repaid = 5,000 -> Remaining Personal Loan = 15,000
    // Shop Purchases = 10,000, Payments = 5,000 -> Remaining Shop Due = 5,000
    // Total Liabilities = 15,000 + 5,000 = 20,000
    // Net Balance = 30,000 - 20,000 = 10,000

    val accounts = listOf(
      AccountEntity(id = 1, name = "Cash", type = "CASH", initialBalance = 10000.0),
      AccountEntity(id = 2, name = "bKash", type = "BKASH", initialBalance = 5000.0),
      AccountEntity(id = 3, name = "Nagad", type = "NAGAD", initialBalance = 5000.0),
      AccountEntity(id = 4, name = "Bank", type = "BANK", initialBalance = 10000.0)
    )

    val loans = listOf(
      LoanEntity(id = 1, lenderName = "Lender 1", initialAmount = 20000.0, dateReceived = 1000L)
    )
    val loanRepayments = listOf(
      LoanRepaymentEntity(id = 1, loanId = 1, amount = 5000.0, date = 2000L)
    )

    val shopTransactions = listOf(
      ShopTransactionEntity(id = 1, shopId = 1, type = "PURCHASE", amount = 10000.0, date = 1000L),
      ShopTransactionEntity(id = 2, shopId = 1, type = "PAYMENT", amount = 5000.0, date = 2000L)
    )

    // A dummy mock DAO isn't needed if we directly call repository calculate logic
    val dummyDao = object : com.example.data.local.dao.FinanceDao {
      override fun getAllAccounts() = kotlinx.coroutines.flow.flowOf(accounts)
      override suspend fun getAccountById(id: Long) = null
      override suspend fun insertAccount(account: AccountEntity) = 1L
      override suspend fun insertAccounts(accounts: List<AccountEntity>) {}
      override suspend fun updateAccount(account: AccountEntity) {}
      override suspend fun deleteAccount(account: AccountEntity) {}
      override fun getAllPersons() = kotlinx.coroutines.flow.flowOf(emptyList<PersonEntity>())
      override suspend fun getPersonById(id: Long) = null
      override suspend fun insertPerson(person: PersonEntity) = 1L
      override suspend fun updatePerson(person: PersonEntity) {}
      override suspend fun deletePerson(person: PersonEntity) {}
      override fun getAllReceivableTransactions() = kotlinx.coroutines.flow.flowOf(emptyList<ReceivableTransactionEntity>())
      override fun getReceivableTransactionsForPerson(personId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<ReceivableTransactionEntity>())
      override suspend fun insertReceivableTransaction(txn: ReceivableTransactionEntity) = 1L
      override suspend fun updateReceivableTransaction(txn: ReceivableTransactionEntity) {}
      override suspend fun deleteReceivableTransaction(txn: ReceivableTransactionEntity) {}
      override suspend fun deleteReceivableTransactionsForPerson(personId: Long) {}
      override fun getAllShops() = kotlinx.coroutines.flow.flowOf(emptyList<ShopEntity>())
      override suspend fun getShopById(id: Long) = null
      override suspend fun insertShop(shop: ShopEntity) = 1L
      override suspend fun updateShop(shop: ShopEntity) {}
      override suspend fun deleteShop(shop: ShopEntity) {}
      override fun getAllShopTransactions() = kotlinx.coroutines.flow.flowOf(shopTransactions)
      override fun getShopTransactionsForShop(shopId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<ShopTransactionEntity>())
      override suspend fun insertShopTransaction(txn: ShopTransactionEntity) = 1L
      override suspend fun updateShopTransaction(txn: ShopTransactionEntity) {}
      override suspend fun deleteShopTransaction(txn: ShopTransactionEntity) {}
      override suspend fun deleteShopTransactionsForShop(shopId: Long) {}
      override fun getAllLoans() = kotlinx.coroutines.flow.flowOf(loans)
      override suspend fun getLoanById(id: Long) = null
      override suspend fun insertLoan(loan: LoanEntity) = 1L
      override suspend fun updateLoan(loan: LoanEntity) {}
      override suspend fun deleteLoan(loan: LoanEntity) {}
      override fun getAllLoanRepayments() = kotlinx.coroutines.flow.flowOf(loanRepayments)
      override fun getRepaymentsForLoan(loanId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<LoanRepaymentEntity>())
      override suspend fun insertLoanRepayment(repayment: LoanRepaymentEntity) = 1L
      override suspend fun updateLoanRepayment(repayment: LoanRepaymentEntity) {}
      override suspend fun deleteLoanRepayment(repayment: LoanRepaymentEntity) {}
      override suspend fun deleteRepaymentsForLoan(loanId: Long) {}
      override fun getAllIncomeExpenses() = kotlinx.coroutines.flow.flowOf(emptyList<IncomeExpenseEntity>())
      override suspend fun insertIncomeExpense(txn: IncomeExpenseEntity) = 1L
      override suspend fun updateIncomeExpense(txn: IncomeExpenseEntity) {}
      override suspend fun deleteIncomeExpense(txn: IncomeExpenseEntity) {}
      override fun getAllTransfers() = kotlinx.coroutines.flow.flowOf(emptyList<AccountTransferEntity>())
      override suspend fun insertTransfer(transfer: AccountTransferEntity) = 1L
      override suspend fun updateTransfer(transfer: AccountTransferEntity) {}
      override suspend fun deleteTransfer(transfer: AccountTransferEntity) {}
      override suspend fun clearReceivableTransactions() {}
      override suspend fun clearPersons() {}
      override suspend fun clearShopTransactions() {}
      override suspend fun clearShops() {}
      override suspend fun clearLoanRepayments() {}
      override suspend fun clearLoans() {}
      override suspend fun clearIncomeExpenses() {}
      override suspend fun clearTransfers() {}
      override suspend fun clearAccounts() {}
    }

    val repository = FinanceRepository(dummyDao)
    val summary = repository.calculateFinancialSummary(
      accounts = accounts,
      incomeExpenses = emptyList(),
      transfers = emptyList(),
      loans = loans,
      loanRepayments = loanRepayments,
      receivableTransactions = emptyList(),
      shopTransactions = shopTransactions
    )

    assertEquals(30000.0, summary.totalAssets, 0.001)
    assertEquals(15000.0, summary.remainingPersonalLoans, 0.001)
    assertEquals(5000.0, summary.remainingShopDues, 0.001)
    assertEquals(20000.0, summary.totalLiabilities, 0.001)
    assertEquals(10000.0, summary.netBalance, 0.001)
  }

  @Test
  fun testNegativeNetBalanceScenario() {
    // If Total Assets = 10,000 and Total Liabilities = 20,000:
    // Net Balance should be -10,000
    val accounts = listOf(
      AccountEntity(id = 1, name = "Cash", type = "CASH", initialBalance = 10000.0)
    )
    val loans = listOf(
      LoanEntity(id = 1, lenderName = "Lender", initialAmount = 20000.0, dateReceived = 1000L)
    )

    val dummyDao = object : com.example.data.local.dao.FinanceDao {
      override fun getAllAccounts() = kotlinx.coroutines.flow.flowOf(emptyList<AccountEntity>())
      override suspend fun getAccountById(id: Long) = null
      override suspend fun insertAccount(account: AccountEntity) = 1L
      override suspend fun insertAccounts(accounts: List<AccountEntity>) {}
      override suspend fun updateAccount(account: AccountEntity) {}
      override suspend fun deleteAccount(account: AccountEntity) {}
      override fun getAllPersons() = kotlinx.coroutines.flow.flowOf(emptyList<PersonEntity>())
      override suspend fun getPersonById(id: Long) = null
      override suspend fun insertPerson(person: PersonEntity) = 1L
      override suspend fun updatePerson(person: PersonEntity) {}
      override suspend fun deletePerson(person: PersonEntity) {}
      override fun getAllReceivableTransactions() = kotlinx.coroutines.flow.flowOf(emptyList<ReceivableTransactionEntity>())
      override fun getReceivableTransactionsForPerson(personId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<ReceivableTransactionEntity>())
      override suspend fun insertReceivableTransaction(txn: ReceivableTransactionEntity) = 1L
      override suspend fun updateReceivableTransaction(txn: ReceivableTransactionEntity) {}
      override suspend fun deleteReceivableTransaction(txn: ReceivableTransactionEntity) {}
      override suspend fun deleteReceivableTransactionsForPerson(personId: Long) {}
      override fun getAllShops() = kotlinx.coroutines.flow.flowOf(emptyList<ShopEntity>())
      override suspend fun getShopById(id: Long) = null
      override suspend fun insertShop(shop: ShopEntity) = 1L
      override suspend fun updateShop(shop: ShopEntity) {}
      override suspend fun deleteShop(shop: ShopEntity) {}
      override fun getAllShopTransactions() = kotlinx.coroutines.flow.flowOf(emptyList<ShopTransactionEntity>())
      override fun getShopTransactionsForShop(shopId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<ShopTransactionEntity>())
      override suspend fun insertShopTransaction(txn: ShopTransactionEntity) = 1L
      override suspend fun updateShopTransaction(txn: ShopTransactionEntity) {}
      override suspend fun deleteShopTransaction(txn: ShopTransactionEntity) {}
      override suspend fun deleteShopTransactionsForShop(shopId: Long) {}
      override fun getAllLoans() = kotlinx.coroutines.flow.flowOf(loans)
      override suspend fun getLoanById(id: Long) = null
      override suspend fun insertLoan(loan: LoanEntity) = 1L
      override suspend fun updateLoan(loan: LoanEntity) {}
      override suspend fun deleteLoan(loan: LoanEntity) {}
      override fun getAllLoanRepayments() = kotlinx.coroutines.flow.flowOf(emptyList<LoanRepaymentEntity>())
      override fun getRepaymentsForLoan(loanId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<LoanRepaymentEntity>())
      override suspend fun insertLoanRepayment(repayment: LoanRepaymentEntity) = 1L
      override suspend fun updateLoanRepayment(repayment: LoanRepaymentEntity) {}
      override suspend fun deleteLoanRepayment(repayment: LoanRepaymentEntity) {}
      override suspend fun deleteRepaymentsForLoan(loanId: Long) {}
      override fun getAllIncomeExpenses() = kotlinx.coroutines.flow.flowOf(emptyList<IncomeExpenseEntity>())
      override suspend fun insertIncomeExpense(txn: IncomeExpenseEntity) = 1L
      override suspend fun updateIncomeExpense(txn: IncomeExpenseEntity) {}
      override suspend fun deleteIncomeExpense(txn: IncomeExpenseEntity) {}
      override fun getAllTransfers() = kotlinx.coroutines.flow.flowOf(emptyList<AccountTransferEntity>())
      override suspend fun insertTransfer(transfer: AccountTransferEntity) = 1L
      override suspend fun updateTransfer(transfer: AccountTransferEntity) {}
      override suspend fun deleteTransfer(transfer: AccountTransferEntity) {}
      override suspend fun clearReceivableTransactions() {}
      override suspend fun clearPersons() {}
      override suspend fun clearShopTransactions() {}
      override suspend fun clearShops() {}
      override suspend fun clearLoanRepayments() {}
      override suspend fun clearLoans() {}
      override suspend fun clearIncomeExpenses() {}
      override suspend fun clearTransfers() {}
      override suspend fun clearAccounts() {}
    }

    val repository = FinanceRepository(dummyDao)
    val summary = repository.calculateFinancialSummary(
      accounts = accounts,
      incomeExpenses = emptyList(),
      transfers = emptyList(),
      loans = loans,
      loanRepayments = emptyList(),
      receivableTransactions = emptyList(),
      shopTransactions = emptyList()
    )

    assertEquals(10000.0, summary.totalAssets, 0.001)
    assertEquals(20000.0, summary.totalLiabilities, 0.001)
    assertEquals(-10000.0, summary.netBalance, 0.001)

    // Formatter check
    val formatted = CurrencyFormatter.format(summary.netBalance, isBengali = false, hideBalance = false)
    assertEquals("-৳ 10,000", formatted)
  }
}
