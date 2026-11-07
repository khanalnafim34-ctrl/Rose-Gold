package com.example.data.repository

import com.example.data.local.MonethakaDatabase
import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private data class BaseFlows(
  val accounts: List<AccountEntity>,
  val incomeExpenses: List<IncomeExpenseEntity>,
  val transfers: List<AccountTransferEntity>
)

private data class DebtFlows(
  val loans: List<LoanEntity>,
  val loanRepayments: List<LoanRepaymentEntity>,
  val receivableTransactions: List<ReceivableTransactionEntity>,
  val shopTransactions: List<ShopTransactionEntity>
)

class FinanceRepository(private val dao: FinanceDao) {

  val accounts: Flow<List<AccountEntity>> = dao.getAllAccounts()
  val persons: Flow<List<PersonEntity>> = dao.getAllPersons()
  val receivableTransactions: Flow<List<ReceivableTransactionEntity>> = dao.getAllReceivableTransactions()
  val shops: Flow<List<ShopEntity>> = dao.getAllShops()
  val shopTransactions: Flow<List<ShopTransactionEntity>> = dao.getAllShopTransactions()
  val loans: Flow<List<LoanEntity>> = dao.getAllLoans()
  val loanRepayments: Flow<List<LoanRepaymentEntity>> = dao.getAllLoanRepayments()
  val incomeExpenses: Flow<List<IncomeExpenseEntity>> = dao.getAllIncomeExpenses()
  val transfers: Flow<List<AccountTransferEntity>> = dao.getAllTransfers()

  private val baseFlow: Flow<BaseFlows> = combine(
    accounts,
    incomeExpenses,
    transfers
  ) { accs, incExp, trfs ->
    BaseFlows(accs, incExp, trfs)
  }

  private val debtFlow: Flow<DebtFlows> = combine(
    loans,
    loanRepayments,
    receivableTransactions,
    shopTransactions
  ) { lns, lRepays, recTxns, shopTxns ->
    DebtFlows(lns, lRepays, recTxns, shopTxns)
  }

  // Combined Financial Summary
  val financialSummary: Flow<FinancialSummary> = combine(
    baseFlow,
    debtFlow
  ) { base, debt ->
    calculateFinancialSummary(
      accounts = base.accounts,
      incomeExpenses = base.incomeExpenses,
      transfers = base.transfers,
      loans = debt.loans,
      loanRepayments = debt.loanRepayments,
      receivableTransactions = debt.receivableTransactions,
      shopTransactions = debt.shopTransactions
    )
  }

  // Persons with computed balance
  val personsWithBalance: Flow<List<PersonWithBalance>> = combine(
    persons,
    receivableTransactions
  ) { personList, txns ->
    personList.map { person ->
      val personTxns = txns.filter { it.personId == person.id }
      val totalGiven = personTxns.filter { it.type == "GIVEN" }.sumOf { it.amount }
      val totalReceived = personTxns.filter { it.type == "RECEIVED" }.sumOf { it.amount }
      val remaining = totalGiven - totalReceived
      val lastTxnDate = personTxns.maxOfOrNull { it.date }
      PersonWithBalance(
        person = person,
        totalGiven = totalGiven,
        totalReceived = totalReceived,
        remainingDue = remaining,
        lastTransactionDate = lastTxnDate
      )
    }
  }

  // Shops with computed balance
  val shopsWithBalance: Flow<List<ShopWithBalance>> = combine(
    shops,
    shopTransactions
  ) { shopList, txns ->
    shopList.map { shop ->
      val shopTxns = txns.filter { it.shopId == shop.id }
      val totalPurchases = shopTxns.filter { it.type == "PURCHASE" }.sumOf { it.amount }
      val totalPaid = shopTxns.filter { it.type == "PAYMENT" }.sumOf { it.amount }
      val remaining = totalPurchases - totalPaid
      val lastTxnDate = shopTxns.maxOfOrNull { it.date }
      ShopWithBalance(
        shop = shop,
        totalPurchases = totalPurchases,
        totalPaid = totalPaid,
        remainingDue = remaining,
        lastTransactionDate = lastTxnDate
      )
    }
  }

  // Loans with computed balance
  val loansWithBalance: Flow<List<LoanWithBalance>> = combine(
    loans,
    loanRepayments
  ) { loanList, repays ->
    loanList.map { loan ->
      val repayments = repays.filter { it.loanId == loan.id }
      val totalRepaid = repayments.sumOf { it.amount }
      val remaining = loan.initialAmount - totalRepaid
      val lastRepaymentDate = repayments.maxOfOrNull { it.date }
      LoanWithBalance(
        loan = loan,
        initialAmount = loan.initialAmount,
        totalRepaid = totalRepaid,
        remainingAmount = remaining,
        lastRepaymentDate = lastRepaymentDate
      )
    }
  }

  // Unified Transactions Stream
  val unifiedTransactions: Flow<List<UnifiedTransaction>> = combine(
    baseFlow,
    debtFlow
  ) { base, debt ->
    val accMap = base.accounts.associateBy { it.id }
    val list = mutableListOf<UnifiedTransaction>()

    // Income & Expense
    base.incomeExpenses.forEach { item ->
      val isInc = item.type == "INCOME"
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = if (isInc) TransactionType.INCOME else TransactionType.EXPENSE,
          title = if (isInc) "আয়: ${item.category}" else "খরচ: ${item.category}",
          subtitle = item.description ?: accMap[item.accountId]?.name,
          amount = item.amount,
          date = item.date,
          accountName = accMap[item.accountId]?.name,
          accountId = item.accountId,
          note = item.description,
          category = item.category,
          originalEntityId = item.id
        )
      )
    }

    // Transfers
    base.transfers.forEach { item ->
      val fromName = accMap[item.fromAccountId]?.name ?: "হিসাব"
      val toName = accMap[item.toAccountId]?.name ?: "হিসাব"
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = TransactionType.TRANSFER,
          title = "স্থানান্তর: $fromName ➔ $toName",
          subtitle = item.note ?: "নিজস্ব স্থানান্তর",
          amount = item.amount,
          date = item.date,
          accountName = "$fromName ➔ $toName",
          accountId = item.fromAccountId,
          note = item.note,
          originalEntityId = item.id
        )
      )
    }

    // Loans received
    debt.loans.forEach { item ->
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = TransactionType.LOAN_BORROWED,
          title = "ঋণ গ্রহণ: ${item.lenderName}",
          subtitle = item.notes ?: accMap[item.accountId]?.name,
          amount = item.initialAmount,
          date = item.dateReceived,
          accountName = accMap[item.accountId]?.name,
          accountId = item.accountId,
          note = item.notes,
          originalEntityId = item.id
        )
      )
    }

    // Loan repayments
    debt.loanRepayments.forEach { item ->
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = TransactionType.LOAN_REPAID,
          title = "ঋণ পরিশোধ",
          subtitle = item.note ?: accMap[item.accountId]?.name,
          amount = item.amount,
          date = item.date,
          accountName = accMap[item.accountId]?.name,
          accountId = item.accountId,
          note = item.note,
          originalEntityId = item.id,
          secondaryEntityId = item.loanId
        )
      )
    }

    // Receivable transactions
    debt.receivableTransactions.forEach { item ->
      val isGiven = item.type == "GIVEN"
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = if (isGiven) TransactionType.RECEIVABLE_GIVEN else TransactionType.RECEIVABLE_COLLECTED,
          title = if (isGiven) "টাকা ধার দেওয়া" else "পাওনা টাকা আদায়",
          subtitle = item.note ?: accMap[item.accountId]?.name,
          amount = item.amount,
          date = item.date,
          accountName = accMap[item.accountId]?.name,
          accountId = item.accountId,
          note = item.note,
          originalEntityId = item.id,
          secondaryEntityId = item.personId
        )
      )
    }

    // Shop transactions
    debt.shopTransactions.forEach { item ->
      val isPurchase = item.type == "PURCHASE"
      list.add(
        UnifiedTransaction(
          id = item.id,
          type = if (isPurchase) TransactionType.SHOP_PURCHASE else TransactionType.SHOP_PAYMENT,
          title = if (isPurchase) "দোকানে বাকি ক্রয়: ${item.productName ?: "পণ্য"}" else "বাকি পরিশোধ",
          subtitle = item.note ?: accMap[item.accountId]?.name,
          amount = item.amount,
          date = item.date,
          accountName = accMap[item.accountId]?.name,
          accountId = item.accountId,
          note = item.note,
          originalEntityId = item.id,
          secondaryEntityId = item.shopId
        )
      )
    }

    list.sortedByDescending { it.date }
  }

  fun calculateFinancialSummary(
    accounts: List<AccountEntity>,
    incomeExpenses: List<IncomeExpenseEntity>,
    transfers: List<AccountTransferEntity>,
    loans: List<LoanEntity>,
    loanRepayments: List<LoanRepaymentEntity>,
    receivableTransactions: List<ReceivableTransactionEntity>,
    shopTransactions: List<ShopTransactionEntity>
  ): FinancialSummary {
    val accountBalances = mutableMapOf<Long, Double>()
    accounts.forEach { acc ->
      var balance = acc.initialBalance

      // Income & Expense
      incomeExpenses.filter { it.accountId == acc.id }.forEach {
        if (it.type == "INCOME") balance += it.amount
        else if (it.type == "EXPENSE") balance -= it.amount
      }

      // Transfers
      transfers.filter { it.toAccountId == acc.id }.forEach { balance += it.amount }
      transfers.filter { it.fromAccountId == acc.id }.forEach { balance -= it.amount }

      // Loans received into this account
      loans.filter { it.accountId == acc.id }.forEach { balance += it.initialAmount }

      // Loan repayments from this account
      loanRepayments.filter { it.accountId == acc.id }.forEach { balance -= it.amount }

      // Money given (receivable given) reduces this account
      receivableTransactions.filter { it.accountId == acc.id && it.type == "GIVEN" }.forEach {
        balance -= it.amount
      }

      // Money collected (receivable collected) adds to this account
      receivableTransactions.filter { it.accountId == acc.id && it.type == "RECEIVED" }.forEach {
        balance += it.amount
      }

      // Shop payments paid from this account
      shopTransactions.filter { it.accountId == acc.id && it.type == "PAYMENT" }.forEach {
        balance -= it.amount
      }

      accountBalances[acc.id] = balance
    }

    // Total Assets = Cash + bKash + Nagad + Bank Balance + Other accounts
    val totalAssets = accountBalances.values.sum()

    // Total Receivables = sum of remaining dues owed to me by each person
    val recTxnsByPerson = receivableTransactions.groupBy { it.personId }
    val totalReceivables = recTxnsByPerson.values.sumOf { txns ->
      val given = txns.filter { it.type == "GIVEN" }.sumOf { it.amount }
      val received = txns.filter { it.type == "RECEIVED" }.sumOf { it.amount }
      kotlin.math.max(0.0, given - received)
    }

    // Personal Loans
    val totalLoanReceived = loans.sumOf { it.initialAmount }
    val totalLoanRepaid = loanRepayments.sumOf { it.amount }
    val remainingPersonalLoans = kotlin.math.max(0.0, totalLoanReceived - totalLoanRepaid)

    // Shop Dues
    val shopTxnsByShop = shopTransactions.groupBy { it.shopId }
    val remainingShopDues = shopTxnsByShop.values.sumOf { txns ->
      val purchases = txns.filter { it.type == "PURCHASE" }.sumOf { it.amount }
      val payments = txns.filter { it.type == "PAYMENT" }.sumOf { it.amount }
      kotlin.math.max(0.0, purchases - payments)
    }

    // Total Liabilities = Outstanding Personal Loans + Outstanding Shop Dues
    val totalLiabilities = remainingPersonalLoans + remainingShopDues

    // Net Balance = Total Assets - Total Liabilities
    val netBalance = totalAssets - totalLiabilities

    return FinancialSummary(
      totalAssets = totalAssets,
      totalLiabilities = totalLiabilities,
      netBalance = netBalance,
      totalReceivables = totalReceivables,
      totalLoanReceived = totalLoanReceived,
      totalLoanRepaid = totalLoanRepaid,
      remainingPersonalLoans = remainingPersonalLoans,
      remainingShopDues = remainingShopDues,
      accountBalances = accountBalances
    )
  }

  // Account Operations
  suspend fun insertAccount(account: AccountEntity) = dao.insertAccount(account)
  suspend fun updateAccount(account: AccountEntity) = dao.updateAccount(account)
  suspend fun deleteAccount(account: AccountEntity) = dao.deleteAccount(account)

  // Person Operations
  suspend fun insertPerson(person: PersonEntity) = dao.insertPerson(person)
  suspend fun updatePerson(person: PersonEntity) = dao.updatePerson(person)
  suspend fun deletePerson(person: PersonEntity) {
    dao.deleteReceivableTransactionsForPerson(person.id)
    dao.deletePerson(person)
  }
  fun getPersonTransactions(personId: Long) = dao.getReceivableTransactionsForPerson(personId)
  suspend fun insertReceivableTransaction(txn: ReceivableTransactionEntity) = dao.insertReceivableTransaction(txn)
  suspend fun updateReceivableTransaction(txn: ReceivableTransactionEntity) = dao.updateReceivableTransaction(txn)
  suspend fun deleteReceivableTransaction(txn: ReceivableTransactionEntity) = dao.deleteReceivableTransaction(txn)

  // Shop Operations
  suspend fun insertShop(shop: ShopEntity) = dao.insertShop(shop)
  suspend fun updateShop(shop: ShopEntity) = dao.updateShop(shop)
  suspend fun deleteShop(shop: ShopEntity) {
    dao.deleteShopTransactionsForShop(shop.id)
    dao.deleteShop(shop)
  }
  fun getShopTransactions(shopId: Long) = dao.getShopTransactionsForShop(shopId)
  suspend fun insertShopTransaction(txn: ShopTransactionEntity) = dao.insertShopTransaction(txn)
  suspend fun updateShopTransaction(txn: ShopTransactionEntity) = dao.updateShopTransaction(txn)
  suspend fun deleteShopTransaction(txn: ShopTransactionEntity) = dao.deleteShopTransaction(txn)

  // Loan Operations
  suspend fun insertLoan(loan: LoanEntity) = dao.insertLoan(loan)
  suspend fun updateLoan(loan: LoanEntity) = dao.updateLoan(loan)
  suspend fun deleteLoan(loan: LoanEntity) {
    dao.deleteRepaymentsForLoan(loan.id)
    dao.deleteLoan(loan)
  }
  fun getLoanRepayments(loanId: Long) = dao.getRepaymentsForLoan(loanId)
  suspend fun insertLoanRepayment(repayment: LoanRepaymentEntity) = dao.insertLoanRepayment(repayment)
  suspend fun updateLoanRepayment(repayment: LoanRepaymentEntity) = dao.updateLoanRepayment(repayment)
  suspend fun deleteLoanRepayment(repayment: LoanRepaymentEntity) = dao.deleteLoanRepayment(repayment)

  // Income & Expense Operations
  suspend fun insertIncomeExpense(txn: IncomeExpenseEntity) = dao.insertIncomeExpense(txn)
  suspend fun updateIncomeExpense(txn: IncomeExpenseEntity) = dao.updateIncomeExpense(txn)
  suspend fun deleteIncomeExpense(txn: IncomeExpenseEntity) = dao.deleteIncomeExpense(txn)

  // Transfer Operations
  suspend fun insertTransfer(transfer: AccountTransferEntity) = dao.insertTransfer(transfer)
  suspend fun updateTransfer(transfer: AccountTransferEntity) = dao.updateTransfer(transfer)
  suspend fun deleteTransfer(transfer: AccountTransferEntity) = dao.deleteTransfer(transfer)

  // Clear all data
  suspend fun clearAllData() {
    dao.clearReceivableTransactions()
    dao.clearPersons()
    dao.clearShopTransactions()
    dao.clearShops()
    dao.clearLoanRepayments()
    dao.clearLoans()
    dao.clearIncomeExpenses()
    dao.clearTransfers()
    dao.clearAccounts()
    dao.insertAccounts(MonethakaDatabase.defaultAccounts)
  }
}
