package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    AccountEntity::class,
    PersonEntity::class,
    ReceivableTransactionEntity::class,
    ShopEntity::class,
    ShopTransactionEntity::class,
    LoanEntity::class,
    LoanRepaymentEntity::class,
    IncomeExpenseEntity::class,
    AccountTransferEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class MonethakaDatabase : RoomDatabase() {

  abstract fun financeDao(): FinanceDao

  companion object {
    @Volatile
    private var INSTANCE: MonethakaDatabase? = null

    fun getDatabase(context: Context): MonethakaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MonethakaDatabase::class.java,
          "monethaka_database"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.financeDao()?.let { dao ->
                  dao.insertAccounts(defaultAccounts)
                }
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    val defaultAccounts = listOf(
      AccountEntity(
        id = 1,
        name = "নগদ (Cash)",
        type = "CASH",
        initialBalance = 0.0,
        isDefault = true,
        colorHex = "#10B981"
      ),
      AccountEntity(
        id = 2,
        name = "bKash",
        type = "BKASH",
        initialBalance = 0.0,
        isDefault = false,
        colorHex = "#E2136E"
      ),
      AccountEntity(
        id = 3,
        name = "Nagad",
        type = "NAGAD",
        initialBalance = 0.0,
        isDefault = false,
        colorHex = "#F7941D"
      ),
      AccountEntity(
        id = 4,
        name = "Bank Account",
        type = "BANK",
        initialBalance = 0.0,
        isDefault = false,
        colorHex = "#1E3A8A"
      )
    )
  }
}
