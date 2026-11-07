package com.example.data.repository

import com.example.data.local.MonethakaDatabase
import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.*
import com.example.data.preferences.UserPreferencesRepository

class DemoDataSeeder(
  private val dao: FinanceDao,
  private val prefs: UserPreferencesRepository
) {

  suspend fun seedDemoData() {
    // Accounts
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L

    // Ensure accounts
    val accounts = listOf(
      AccountEntity(id = 1, name = "নগদ (Cash)", type = "CASH", initialBalance = 10000.0, isDefault = true, colorHex = "#10B981"),
      AccountEntity(id = 2, name = "bKash", type = "BKASH", initialBalance = 5000.0, isDefault = false, colorHex = "#E2136E"),
      AccountEntity(id = 3, name = "Nagad", type = "NAGAD", initialBalance = 5000.0, isDefault = false, colorHex = "#F7941D"),
      AccountEntity(id = 4, name = "Bank Account", type = "BANK", initialBalance = 10000.0, isDefault = false, colorHex = "#1E3A8A")
    )
    dao.insertAccounts(accounts)

    // Personal Loan: ৳20,000 received, ৳5,000 repaid -> Remaining ৳15,000
    val loanId = dao.insertLoan(
      LoanEntity(
        lenderName = "হাসান মাহমুদ (Hasan Mahmud)",
        phone = "01711223344",
        initialAmount = 20000.0,
        dateReceived = now - 15 * day,
        dueDate = now + 45 * day,
        notes = "ব্যবসায়িক জরুরি প্রয়োজনে নেওয়া",
        accountId = null // balance already calibrated in accounts
      )
    )
    dao.insertLoanRepayment(
      LoanRepaymentEntity(
        loanId = loanId,
        amount = 5000.0,
        date = now - 5 * day,
        accountId = null,
        note = "প্রথম কিস্তি পরিশোধ"
      )
    )

    // Shop Credit: Bismillah General Store
    val shopId = dao.insertShop(
      ShopEntity(
        shopName = "বিসমিল্লাহ জেনারেল স্টোর",
        ownerName = "আব্দুল হক",
        phone = "01819988776",
        address = "বাজার মোড়, দোকান নং ১২",
        notes = "মাসিক মুদি বাজার বাকি খাতা",
        createdAt = now - 30 * day
      )
    )
    dao.insertShopTransaction(
      ShopTransactionEntity(
        shopId = shopId,
        type = "PURCHASE",
        productName = "মাসিক চাল, ডাল ও তেল",
        quantity = "১ বস্তা ও ৫ লিটার",
        amount = 10000.0,
        date = now - 12 * day,
        dueDate = now + 15 * day,
        note = "বাকি কেনাকাটা",
        accountId = null
      )
    )
    dao.insertShopTransaction(
      ShopTransactionEntity(
        shopId = shopId,
        type = "PAYMENT",
        amount = 5000.0,
        date = now - 3 * day,
        note = "আংশিক বাকি পরিশোধ",
        accountId = null
      )
    )

    // Receivables: Money others owe me
    val person1Id = dao.insertPerson(
      PersonEntity(
        name = "তানভীর আহমেদ",
        phone = "01912345678",
        address = "মিরপুর-১০, ঢাকা",
        notes = "বন্ধুকে জরুরি ধার দেওয়া",
        createdAt = now - 20 * day
      )
    )
    dao.insertReceivableTransaction(
      ReceivableTransactionEntity(
        personId = person1Id,
        type = "GIVEN",
        amount = 12000.0,
        date = now - 20 * day,
        dueDate = now + 10 * day,
        note = "জরুরি ব্যক্তিগত ধার",
        accountId = null
      )
    )
    dao.insertReceivableTransaction(
      ReceivableTransactionEntity(
        personId = person1Id,
        type = "RECEIVED",
        amount = 4000.0,
        date = now - 7 * day,
        note = "বিকাশের মাধ্যমে আংশিক ফেরত",
        accountId = null
      )
    )

    val person2Id = dao.insertPerson(
      PersonEntity(
        name = "রফিকুল ইসলাম",
        phone = "01678112233",
        address = "ধানমন্ডি, ঢাকা",
        notes = "অফিস কলিগ",
        createdAt = now - 10 * day
      )
    )
    dao.insertReceivableTransaction(
      ReceivableTransactionEntity(
        personId = person2Id,
        type = "GIVEN",
        amount = 5000.0,
        date = now - 10 * day,
        dueDate = now + 20 * day,
        note = "মেডিকেল প্রয়োজনে ধার",
        accountId = null
      )
    )

    // Recent income & expenses
    dao.insertIncomeExpense(
      IncomeExpenseEntity(
        type = "INCOME",
        category = "বেতন (Salary)",
        amount = 35000.0,
        date = now - 6 * day,
        accountId = 4,
        description = "অক্টোবর মাসের বেতন"
      )
    )
    dao.insertIncomeExpense(
      IncomeExpenseEntity(
        type = "EXPENSE",
        category = "খাবার ও মুদি (Food)",
        amount = 2500.0,
        date = now - 2 * day,
        accountId = 1,
        description = "সাপ্তাহিক বাজার"
      )
    )
    dao.insertIncomeExpense(
      IncomeExpenseEntity(
        type = "EXPENSE",
        category = "যাতায়াত (Transport)",
        amount = 800.0,
        date = now - 1 * day,
        accountId = 2,
        description = "উবার ও সিএনজি ভাড়া"
      )
    )

    prefs.setDemoLoaded(true)
  }

  suspend fun removeDemoData() {
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
    prefs.setDemoLoaded(false)
  }
}
