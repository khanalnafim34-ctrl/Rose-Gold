package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

  // Accounts
  @Query("SELECT * FROM accounts ORDER BY id ASC")
  fun getAllAccounts(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM accounts WHERE id = :id")
  suspend fun getAccountById(id: Long): AccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccount(account: AccountEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccounts(accounts: List<AccountEntity>)

  @Update
  suspend fun updateAccount(account: AccountEntity)

  @Delete
  suspend fun deleteAccount(account: AccountEntity)

  // Persons & Receivables
  @Query("SELECT * FROM persons ORDER BY createdAt DESC")
  fun getAllPersons(): Flow<List<PersonEntity>>

  @Query("SELECT * FROM persons WHERE id = :id")
  suspend fun getPersonById(id: Long): PersonEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPerson(person: PersonEntity): Long

  @Update
  suspend fun updatePerson(person: PersonEntity)

  @Delete
  suspend fun deletePerson(person: PersonEntity)

  @Query("SELECT * FROM receivable_transactions ORDER BY date DESC")
  fun getAllReceivableTransactions(): Flow<List<ReceivableTransactionEntity>>

  @Query("SELECT * FROM receivable_transactions WHERE personId = :personId ORDER BY date DESC")
  fun getReceivableTransactionsForPerson(personId: Long): Flow<List<ReceivableTransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReceivableTransaction(txn: ReceivableTransactionEntity): Long

  @Update
  suspend fun updateReceivableTransaction(txn: ReceivableTransactionEntity)

  @Delete
  suspend fun deleteReceivableTransaction(txn: ReceivableTransactionEntity)

  @Query("DELETE FROM receivable_transactions WHERE personId = :personId")
  suspend fun deleteReceivableTransactionsForPerson(personId: Long)

  // Shops & Shop Transactions
  @Query("SELECT * FROM shops ORDER BY createdAt DESC")
  fun getAllShops(): Flow<List<ShopEntity>>

  @Query("SELECT * FROM shops WHERE id = :id")
  suspend fun getShopById(id: Long): ShopEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertShop(shop: ShopEntity): Long

  @Update
  suspend fun updateShop(shop: ShopEntity)

  @Delete
  suspend fun deleteShop(shop: ShopEntity)

  @Query("SELECT * FROM shop_transactions ORDER BY date DESC")
  fun getAllShopTransactions(): Flow<List<ShopTransactionEntity>>

  @Query("SELECT * FROM shop_transactions WHERE shopId = :shopId ORDER BY date DESC")
  fun getShopTransactionsForShop(shopId: Long): Flow<List<ShopTransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertShopTransaction(txn: ShopTransactionEntity): Long

  @Update
  suspend fun updateShopTransaction(txn: ShopTransactionEntity)

  @Delete
  suspend fun deleteShopTransaction(txn: ShopTransactionEntity)

  @Query("DELETE FROM shop_transactions WHERE shopId = :shopId")
  suspend fun deleteShopTransactionsForShop(shopId: Long)

  // Loans & Repayments
  @Query("SELECT * FROM loans ORDER BY dateReceived DESC")
  fun getAllLoans(): Flow<List<LoanEntity>>

  @Query("SELECT * FROM loans WHERE id = :id")
  suspend fun getLoanById(id: Long): LoanEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLoan(loan: LoanEntity): Long

  @Update
  suspend fun updateLoan(loan: LoanEntity)

  @Delete
  suspend fun deleteLoan(loan: LoanEntity)

  @Query("SELECT * FROM loan_repayments ORDER BY date DESC")
  fun getAllLoanRepayments(): Flow<List<LoanRepaymentEntity>>

  @Query("SELECT * FROM loan_repayments WHERE loanId = :loanId ORDER BY date DESC")
  fun getRepaymentsForLoan(loanId: Long): Flow<List<LoanRepaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLoanRepayment(repayment: LoanRepaymentEntity): Long

  @Update
  suspend fun updateLoanRepayment(repayment: LoanRepaymentEntity)

  @Delete
  suspend fun deleteLoanRepayment(repayment: LoanRepaymentEntity)

  @Query("DELETE FROM loan_repayments WHERE loanId = :loanId")
  suspend fun deleteRepaymentsForLoan(loanId: Long)

  // Income & Expenses
  @Query("SELECT * FROM income_expenses ORDER BY date DESC")
  fun getAllIncomeExpenses(): Flow<List<IncomeExpenseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIncomeExpense(txn: IncomeExpenseEntity): Long

  @Update
  suspend fun updateIncomeExpense(txn: IncomeExpenseEntity)

  @Delete
  suspend fun deleteIncomeExpense(txn: IncomeExpenseEntity)

  // Transfers
  @Query("SELECT * FROM account_transfers ORDER BY date DESC")
  fun getAllTransfers(): Flow<List<AccountTransferEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransfer(transfer: AccountTransferEntity): Long

  @Update
  suspend fun updateTransfer(transfer: AccountTransferEntity)

  @Delete
  suspend fun deleteTransfer(transfer: AccountTransferEntity)

  // Bulk deletion
  @Query("DELETE FROM receivable_transactions")
  suspend fun clearReceivableTransactions()

  @Query("DELETE FROM persons")
  suspend fun clearPersons()

  @Query("DELETE FROM shop_transactions")
  suspend fun clearShopTransactions()

  @Query("DELETE FROM shops")
  suspend fun clearShops()

  @Query("DELETE FROM loan_repayments")
  suspend fun clearLoanRepayments()

  @Query("DELETE FROM loans")
  suspend fun clearLoans()

  @Query("DELETE FROM income_expenses")
  suspend fun clearIncomeExpenses()

  @Query("DELETE FROM account_transfers")
  suspend fun clearTransfers()

  @Query("DELETE FROM accounts")
  suspend fun clearAccounts()
}
