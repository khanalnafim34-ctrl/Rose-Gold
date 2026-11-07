package com.example.ui.util

import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(private val dao: FinanceDao) {

  suspend fun exportToJson(): String {
    val root = JSONObject()
    root.put("version", 1)
    root.put("exportTimestamp", System.currentTimeMillis())
    root.put("appName", "Monethaka")

    // Accounts
    val accounts = dao.getAllAccounts().first()
    val accountsArr = JSONArray()
    accounts.forEach { acc ->
      val obj = JSONObject().apply {
        put("id", acc.id)
        put("name", acc.name)
        put("type", acc.type)
        put("initialBalance", acc.initialBalance)
        put("isDefault", acc.isDefault)
        put("colorHex", acc.colorHex ?: "")
      }
      accountsArr.put(obj)
    }
    root.put("accounts", accountsArr)

    // Persons & Receivables
    val persons = dao.getAllPersons().first()
    val personsArr = JSONArray()
    persons.forEach { p ->
      val obj = JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("phone", p.phone)
        put("address", p.address ?: "")
        put("notes", p.notes ?: "")
        put("createdAt", p.createdAt)
      }
      personsArr.put(obj)
    }
    root.put("persons", personsArr)

    val recTxns = dao.getAllReceivableTransactions().first()
    val recTxnsArr = JSONArray()
    recTxns.forEach { txn ->
      val obj = JSONObject().apply {
        put("id", txn.id)
        put("personId", txn.personId)
        put("type", txn.type)
        put("amount", txn.amount)
        put("date", txn.date)
        put("dueDate", txn.dueDate ?: 0L)
        put("note", txn.note ?: "")
        put("accountId", txn.accountId ?: 0L)
      }
      recTxnsArr.put(obj)
    }
    root.put("receivableTransactions", recTxnsArr)

    // Shops & Shop Transactions
    val shops = dao.getAllShops().first()
    val shopsArr = JSONArray()
    shops.forEach { s ->
      val obj = JSONObject().apply {
        put("id", s.id)
        put("shopName", s.shopName)
        put("ownerName", s.ownerName ?: "")
        put("phone", s.phone ?: "")
        put("address", s.address ?: "")
        put("notes", s.notes ?: "")
        put("createdAt", s.createdAt)
      }
      shopsArr.put(obj)
    }
    root.put("shops", shopsArr)

    val shopTxns = dao.getAllShopTransactions().first()
    val shopTxnsArr = JSONArray()
    shopTxns.forEach { txn ->
      val obj = JSONObject().apply {
        put("id", txn.id)
        put("shopId", txn.shopId)
        put("type", txn.type)
        put("productName", txn.productName ?: "")
        put("quantity", txn.quantity ?: "")
        put("amount", txn.amount)
        put("date", txn.date)
        put("dueDate", txn.dueDate ?: 0L)
        put("note", txn.note ?: "")
        put("accountId", txn.accountId ?: 0L)
      }
      shopTxnsArr.put(obj)
    }
    root.put("shopTransactions", shopTxnsArr)

    // Loans & Repayments
    val loans = dao.getAllLoans().first()
    val loansArr = JSONArray()
    loans.forEach { l ->
      val obj = JSONObject().apply {
        put("id", l.id)
        put("lenderName", l.lenderName)
        put("phone", l.phone ?: "")
        put("initialAmount", l.initialAmount)
        put("dateReceived", l.dateReceived)
        put("dueDate", l.dueDate ?: 0L)
        put("notes", l.notes ?: "")
        put("accountId", l.accountId ?: 0L)
      }
      loansArr.put(obj)
    }
    root.put("loans", loansArr)

    val loanRepays = dao.getAllLoanRepayments().first()
    val loanRepaysArr = JSONArray()
    loanRepays.forEach { r ->
      val obj = JSONObject().apply {
        put("id", r.id)
        put("loanId", r.loanId)
        put("amount", r.amount)
        put("date", r.date)
        put("accountId", r.accountId ?: 0L)
        put("note", r.note ?: "")
      }
      loanRepaysArr.put(obj)
    }
    root.put("loanRepayments", loanRepaysArr)

    // Income & Expenses
    val incExp = dao.getAllIncomeExpenses().first()
    val incExpArr = JSONArray()
    incExp.forEach { ie ->
      val obj = JSONObject().apply {
        put("id", ie.id)
        put("type", ie.type)
        put("category", ie.category)
        put("amount", ie.amount)
        put("date", ie.date)
        put("accountId", ie.accountId)
        put("description", ie.description ?: "")
      }
      incExpArr.put(obj)
    }
    root.put("incomeExpenses", incExpArr)

    // Transfers
    val trfs = dao.getAllTransfers().first()
    val trfsArr = JSONArray()
    trfs.forEach { t ->
      val obj = JSONObject().apply {
        put("id", t.id)
        put("fromAccountId", t.fromAccountId)
        put("toAccountId", t.toAccountId)
        put("amount", t.amount)
        put("date", t.date)
        put("note", t.note ?: "")
      }
      trfsArr.put(obj)
    }
    root.put("transfers", trfsArr)

    return root.toString(2)
  }

  suspend fun restoreFromJson(jsonString: String): Boolean {
    return try {
      val root = JSONObject(jsonString)

      // Clear existing
      dao.clearReceivableTransactions()
      dao.clearPersons()
      dao.clearShopTransactions()
      dao.clearShops()
      dao.clearLoanRepayments()
      dao.clearLoans()
      dao.clearIncomeExpenses()
      dao.clearTransfers()
      dao.clearAccounts()

      // Restore Accounts
      if (root.has("accounts")) {
        val arr = root.getJSONArray("accounts")
        val list = mutableListOf<AccountEntity>()
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          list.add(
            AccountEntity(
              id = obj.optLong("id", 0L),
              name = obj.getString("name"),
              type = obj.getString("type"),
              initialBalance = obj.optDouble("initialBalance", 0.0),
              isDefault = obj.optBoolean("isDefault", false),
              colorHex = obj.optString("colorHex").takeIf { it.isNotEmpty() }
            )
          )
        }
        dao.insertAccounts(list)
      }

      // Restore Persons
      if (root.has("persons")) {
        val arr = root.getJSONArray("persons")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertPerson(
            PersonEntity(
              id = obj.optLong("id", 0L),
              name = obj.getString("name"),
              phone = obj.optString("phone", ""),
              address = obj.optString("address").takeIf { it.isNotEmpty() },
              notes = obj.optString("notes").takeIf { it.isNotEmpty() },
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Restore Receivable Transactions
      if (root.has("receivableTransactions")) {
        val arr = root.getJSONArray("receivableTransactions")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertReceivableTransaction(
            ReceivableTransactionEntity(
              id = obj.optLong("id", 0L),
              personId = obj.getLong("personId"),
              type = obj.getString("type"),
              amount = obj.getDouble("amount"),
              date = obj.getLong("date"),
              dueDate = obj.optLong("dueDate").takeIf { it > 0 },
              note = obj.optString("note").takeIf { it.isNotEmpty() },
              accountId = obj.optLong("accountId").takeIf { it > 0 }
            )
          )
        }
      }

      // Restore Shops
      if (root.has("shops")) {
        val arr = root.getJSONArray("shops")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertShop(
            ShopEntity(
              id = obj.optLong("id", 0L),
              shopName = obj.getString("shopName"),
              ownerName = obj.optString("ownerName").takeIf { it.isNotEmpty() },
              phone = obj.optString("phone").takeIf { it.isNotEmpty() },
              address = obj.optString("address").takeIf { it.isNotEmpty() },
              notes = obj.optString("notes").takeIf { it.isNotEmpty() },
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      // Restore Shop Transactions
      if (root.has("shopTransactions")) {
        val arr = root.getJSONArray("shopTransactions")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertShopTransaction(
            ShopTransactionEntity(
              id = obj.optLong("id", 0L),
              shopId = obj.getLong("shopId"),
              type = obj.getString("type"),
              productName = obj.optString("productName").takeIf { it.isNotEmpty() },
              quantity = obj.optString("quantity").takeIf { it.isNotEmpty() },
              amount = obj.getDouble("amount"),
              date = obj.getLong("date"),
              dueDate = obj.optLong("dueDate").takeIf { it > 0 },
              note = obj.optString("note").takeIf { it.isNotEmpty() },
              accountId = obj.optLong("accountId").takeIf { it > 0 }
            )
          )
        }
      }

      // Restore Loans
      if (root.has("loans")) {
        val arr = root.getJSONArray("loans")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertLoan(
            LoanEntity(
              id = obj.optLong("id", 0L),
              lenderName = obj.getString("lenderName"),
              phone = obj.optString("phone").takeIf { it.isNotEmpty() },
              initialAmount = obj.getDouble("initialAmount"),
              dateReceived = obj.getLong("dateReceived"),
              dueDate = obj.optLong("dueDate").takeIf { it > 0 },
              notes = obj.optString("notes").takeIf { it.isNotEmpty() },
              accountId = obj.optLong("accountId").takeIf { it > 0 }
            )
          )
        }
      }

      // Restore Loan Repayments
      if (root.has("loanRepayments")) {
        val arr = root.getJSONArray("loanRepayments")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertLoanRepayment(
            LoanRepaymentEntity(
              id = obj.optLong("id", 0L),
              loanId = obj.getLong("loanId"),
              amount = obj.getDouble("amount"),
              date = obj.getLong("date"),
              accountId = obj.optLong("accountId").takeIf { it > 0 },
              note = obj.optString("note").takeIf { it.isNotEmpty() }
            )
          )
        }
      }

      // Restore Income & Expenses
      if (root.has("incomeExpenses")) {
        val arr = root.getJSONArray("incomeExpenses")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertIncomeExpense(
            IncomeExpenseEntity(
              id = obj.optLong("id", 0L),
              type = obj.getString("type"),
              category = obj.getString("category"),
              amount = obj.getDouble("amount"),
              date = obj.getLong("date"),
              accountId = obj.getLong("accountId"),
              description = obj.optString("description").takeIf { it.isNotEmpty() }
            )
          )
        }
      }

      // Restore Transfers
      if (root.has("transfers")) {
        val arr = root.getJSONArray("transfers")
        for (i in 0 until arr.length()) {
          val obj = arr.getJSONObject(i)
          dao.insertTransfer(
            AccountTransferEntity(
              id = obj.optLong("id", 0L),
              fromAccountId = obj.getLong("fromAccountId"),
              toAccountId = obj.getLong("toAccountId"),
              amount = obj.getDouble("amount"),
              date = obj.getLong("date"),
              note = obj.optString("note").takeIf { it.isNotEmpty() }
            )
          )
        }
      }

      true
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }
}
