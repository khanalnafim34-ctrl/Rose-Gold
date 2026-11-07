package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MonethakaDatabase
import com.example.data.local.entity.*
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.DemoDataSeeder
import com.example.data.repository.FinanceRepository
import com.example.model.*
import com.example.ui.util.BackupManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

  private val database = MonethakaDatabase.getDatabase(application)
  private val dao = database.financeDao()
  private val repository = FinanceRepository(dao)
  private val preferencesRepo = UserPreferencesRepository(application)
  private val demoSeeder = DemoDataSeeder(dao, preferencesRepo)
  private val backupManager = BackupManager(dao)

  // Preferences
  val darkMode: StateFlow<String> = preferencesRepo.darkModeFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

  val isBalanceHidden: StateFlow<Boolean> = preferencesRepo.hideBalanceFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val isBengali: StateFlow<Boolean> = preferencesRepo.isBengaliFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

  val appPin: StateFlow<String?> = preferencesRepo.appPinFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val isDemoLoaded: StateFlow<Boolean> = preferencesRepo.isDemoLoadedFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  // PIN Lock State
  private val _isUnlocked = MutableStateFlow(true)
  val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

  init {
    viewModelScope.launch {
      appPin.collect { pin ->
        if (!pin.isNullOrEmpty()) {
          // If a PIN is configured, lock on launch until entered
          _isUnlocked.value = false
        } else {
          _isUnlocked.value = true
        }
      }
    }
  }

  fun unlockApp(pin: String): Boolean {
    if (pin == appPin.value) {
      _isUnlocked.value = true
      return true
    }
    return false
  }

  fun lockApp() {
    if (!appPin.value.isNullOrEmpty()) {
      _isUnlocked.value = false
    }
  }

  // Reactive Data
  val financialSummary: StateFlow<FinancialSummary> = repository.financialSummary
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

  val accounts: StateFlow<List<AccountEntity>> = repository.accounts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val personsWithBalance: StateFlow<List<PersonWithBalance>> = repository.personsWithBalance
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shopsWithBalance: StateFlow<List<ShopWithBalance>> = repository.shopsWithBalance
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val loansWithBalance: StateFlow<List<LoanWithBalance>> = repository.loansWithBalance
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unifiedTransactions: StateFlow<List<UnifiedTransaction>> = repository.unifiedTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun getPersonTransactions(personId: Long): Flow<List<ReceivableTransactionEntity>> =
    repository.getPersonTransactions(personId)

  fun getShopTransactions(shopId: Long): Flow<List<ShopTransactionEntity>> =
    repository.getShopTransactions(shopId)

  fun getLoanRepayments(loanId: Long): Flow<List<LoanRepaymentEntity>> =
    repository.getLoanRepayments(loanId)

  // Preferences Actions
  fun toggleHideBalance() {
    viewModelScope.launch {
      preferencesRepo.setHideBalance(!isBalanceHidden.value)
    }
  }

  fun setDarkMode(mode: String) {
    viewModelScope.launch {
      preferencesRepo.setDarkMode(mode)
    }
  }

  fun setIsBengali(isBengali: Boolean) {
    viewModelScope.launch {
      preferencesRepo.setIsBengali(isBengali)
    }
  }

  fun setAppPin(pin: String?) {
    viewModelScope.launch {
      preferencesRepo.setAppPin(pin)
      if (pin.isNullOrEmpty()) {
        _isUnlocked.value = true
      }
    }
  }

  // Demo Data Actions
  fun loadDemoData() {
    viewModelScope.launch {
      demoSeeder.seedDemoData()
    }
  }

  fun removeDemoData() {
    viewModelScope.launch {
      demoSeeder.removeDemoData()
    }
  }

  // Reset all
  fun clearAllData() {
    viewModelScope.launch {
      repository.clearAllData()
    }
  }

  // Backup & Restore
  suspend fun exportJson(): String = backupManager.exportToJson()

  suspend fun restoreJson(json: String): Boolean = backupManager.restoreFromJson(json)

  // Income / Expense Actions
  fun addIncomeExpense(
    type: String,
    category: String,
    amount: Double,
    date: Long,
    accountId: Long,
    description: String?
  ) {
    viewModelScope.launch {
      repository.insertIncomeExpense(
        IncomeExpenseEntity(
          type = type,
          category = category,
          amount = amount,
          date = date,
          accountId = accountId,
          description = description
        )
      )
    }
  }

  fun updateIncomeExpense(entity: IncomeExpenseEntity) {
    viewModelScope.launch {
      repository.updateIncomeExpense(entity)
    }
  }

  fun deleteIncomeExpense(id: Long) {
    viewModelScope.launch {
      repository.deleteIncomeExpense(IncomeExpenseEntity(id = id, type = "", category = "", amount = 0.0, date = 0L, accountId = 0L))
    }
  }

  // Receivable Actions
  fun addPerson(name: String, phone: String, address: String?, notes: String?, initialDue: Double? = null, accountId: Long? = null) {
    viewModelScope.launch {
      val personId = repository.insertPerson(
        PersonEntity(name = name, phone = phone, address = address, notes = notes)
      )
      if (initialDue != null && initialDue > 0) {
        repository.insertReceivableTransaction(
          ReceivableTransactionEntity(
            personId = personId,
            type = "GIVEN",
            amount = initialDue,
            date = System.currentTimeMillis(),
            note = "প্রাথমিক পাওনা",
            accountId = accountId
          )
        )
      }
    }
  }

  fun updatePerson(person: PersonEntity) {
    viewModelScope.launch {
      repository.updatePerson(person)
    }
  }

  fun deletePerson(person: PersonEntity) {
    viewModelScope.launch {
      repository.deletePerson(person)
    }
  }

  fun addReceivableTransaction(
    personId: Long,
    type: String, // "GIVEN" or "RECEIVED"
    amount: Double,
    date: Long,
    dueDate: Long?,
    note: String?,
    accountId: Long?
  ) {
    viewModelScope.launch {
      repository.insertReceivableTransaction(
        ReceivableTransactionEntity(
          personId = personId,
          type = type,
          amount = amount,
          date = date,
          dueDate = dueDate,
          note = note,
          accountId = accountId
        )
      )
    }
  }

  fun deleteReceivableTransaction(txn: ReceivableTransactionEntity) {
    viewModelScope.launch {
      repository.deleteReceivableTransaction(txn)
    }
  }

  // Shop Actions
  fun addShop(
    shopName: String,
    ownerName: String?,
    phone: String?,
    address: String?,
    notes: String?,
    initialDue: Double? = null
  ) {
    viewModelScope.launch {
      val shopId = repository.insertShop(
        ShopEntity(shopName = shopName, ownerName = ownerName, phone = phone, address = address, notes = notes)
      )
      if (initialDue != null && initialDue > 0) {
        repository.insertShopTransaction(
          ShopTransactionEntity(
            shopId = shopId,
            type = "PURCHASE",
            amount = initialDue,
            date = System.currentTimeMillis(),
            productName = "প্রাথমিক বাকি",
            note = "পূর্বের বাকি হিসাব"
          )
        )
      }
    }
  }

  fun updateShop(shop: ShopEntity) {
    viewModelScope.launch {
      repository.updateShop(shop)
    }
  }

  fun deleteShop(shop: ShopEntity) {
    viewModelScope.launch {
      repository.deleteShop(shop)
    }
  }

  fun addShopTransaction(
    shopId: Long,
    type: String, // "PURCHASE" or "PAYMENT"
    productName: String?,
    quantity: String?,
    amount: Double,
    date: Long,
    dueDate: Long?,
    note: String?,
    accountId: Long?
  ) {
    viewModelScope.launch {
      repository.insertShopTransaction(
        ShopTransactionEntity(
          shopId = shopId,
          type = type,
          productName = productName,
          quantity = quantity,
          amount = amount,
          date = date,
          dueDate = dueDate,
          note = note,
          accountId = accountId
        )
      )
    }
  }

  fun deleteShopTransaction(txn: ShopTransactionEntity) {
    viewModelScope.launch {
      repository.deleteShopTransaction(txn)
    }
  }

  // Loan Actions
  fun addLoan(
    lenderName: String,
    phone: String?,
    amount: Double,
    dateReceived: Long,
    dueDate: Long?,
    notes: String?,
    accountId: Long?
  ) {
    viewModelScope.launch {
      repository.insertLoan(
        LoanEntity(
          lenderName = lenderName,
          phone = phone,
          initialAmount = amount,
          dateReceived = dateReceived,
          dueDate = dueDate,
          notes = notes,
          accountId = accountId
        )
      )
    }
  }

  fun updateLoan(loan: LoanEntity) {
    viewModelScope.launch {
      repository.updateLoan(loan)
    }
  }

  fun deleteLoan(loan: LoanEntity) {
    viewModelScope.launch {
      repository.deleteLoan(loan)
    }
  }

  fun addLoanRepayment(
    loanId: Long,
    amount: Double,
    date: Long,
    accountId: Long?,
    note: String?
  ) {
    viewModelScope.launch {
      repository.insertLoanRepayment(
        LoanRepaymentEntity(
          loanId = loanId,
          amount = amount,
          date = date,
          accountId = accountId,
          note = note
        )
      )
    }
  }

  fun deleteLoanRepayment(repayment: LoanRepaymentEntity) {
    viewModelScope.launch {
      repository.deleteLoanRepayment(repayment)
    }
  }

  // Accounts & Transfers Actions
  fun addAccount(name: String, type: String, initialBalance: Double, colorHex: String?) {
    viewModelScope.launch {
      repository.insertAccount(
        AccountEntity(name = name, type = type, initialBalance = initialBalance, colorHex = colorHex)
      )
    }
  }

  fun updateAccount(account: AccountEntity) {
    viewModelScope.launch {
      repository.updateAccount(account)
    }
  }

  fun deleteAccount(account: AccountEntity) {
    viewModelScope.launch {
      repository.deleteAccount(account)
    }
  }

  fun transferBetweenAccounts(
    fromAccountId: Long,
    toAccountId: Long,
    amount: Double,
    date: Long,
    note: String?
  ) {
    viewModelScope.launch {
      repository.insertTransfer(
        AccountTransferEntity(
          fromAccountId = fromAccountId,
          toAccountId = toAccountId,
          amount = amount,
          date = date,
          note = note
        )
      )
    }
  }

  fun deleteTransfer(transferId: Long) {
    viewModelScope.launch {
      repository.deleteTransfer(AccountTransferEntity(id = transferId, fromAccountId = 0, toAccountId = 0, amount = 0.0, date = 0))
    }
  }
}
