package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.entities.*

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        VehicleIncomeEntity::class,
        MaintenanceEntity::class,
        FuelRecordEntity::class,
        ChargingRecordEntity::class,
        HomeExpenseEntity::class,
        OtherIncomeEntity::class,
        BudgetEntity::class,
        AlertEntity::class,
        CustomCategoryEntity::class,
        UserSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun vehicleIncomeDao(): VehicleIncomeDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun fuelDao(): FuelDao
    abstract fun chargingDao(): ChargingDao
    abstract fun homeExpenseDao(): HomeExpenseDao
    abstract fun otherIncomeDao(): OtherIncomeDao
    abstract fun budgetDao(): BudgetDao
    abstract fun alertDao(): AlertDao
    abstract fun customCategoryDao(): CustomCategoryDao
    abstract fun userSettingDao(): UserSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hisabpro_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
