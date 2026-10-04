package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getFirstUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE userId = :userId ORDER BY id DESC")
    fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE userId = :userId AND status = 'Active' ORDER BY id DESC")
    fun getActiveVehiclesByUser(userId: Long): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId LIMIT 1")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity): Long

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface VehicleIncomeDao {
    @Query("SELECT * FROM vehicle_income WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getIncomesByUser(userId: Long): Flow<List<VehicleIncomeEntity>>

    @Query("SELECT * FROM vehicle_income WHERE userId = :userId AND vehicleId = :vehicleId ORDER BY date DESC")
    fun getIncomesByVehicle(userId: Long, vehicleId: Long): Flow<List<VehicleIncomeEntity>>

    @Query("SELECT * FROM vehicle_income WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomesByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<VehicleIncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: VehicleIncomeEntity): Long

    @Delete
    suspend fun deleteIncome(income: VehicleIncomeEntity)

    @Query("DELETE FROM vehicle_income WHERE id = :id")
    suspend fun deleteIncomeById(id: Long)

    @Query("DELETE FROM vehicle_income WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getMaintenancesByUser(userId: Long): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance WHERE userId = :userId AND vehicleId = :vehicleId ORDER BY date DESC")
    fun getMaintenancesByVehicle(userId: Long, vehicleId: Long): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getMaintenancesByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: MaintenanceEntity): Long

    @Delete
    suspend fun deleteMaintenance(maintenance: MaintenanceEntity)

    @Query("DELETE FROM maintenance WHERE id = :id")
    suspend fun deleteMaintenanceById(id: Long)

    @Query("DELETE FROM maintenance WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface FuelDao {
    @Query("SELECT * FROM fuel_records WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getFuelRecordsByUser(userId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records WHERE userId = :userId AND vehicleId = :vehicleId ORDER BY date DESC")
    fun getFuelRecordsByVehicle(userId: Long, vehicleId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getFuelRecordsByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<FuelRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelRecord(fuelRecord: FuelRecordEntity): Long

    @Delete
    suspend fun deleteFuelRecord(fuelRecord: FuelRecordEntity)

    @Query("DELETE FROM fuel_records WHERE id = :id")
    suspend fun deleteFuelById(id: Long)

    @Query("DELETE FROM fuel_records WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface ChargingDao {
    @Query("SELECT * FROM charging_records WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getChargingRecordsByUser(userId: Long): Flow<List<ChargingRecordEntity>>

    @Query("SELECT * FROM charging_records WHERE userId = :userId AND vehicleId = :vehicleId ORDER BY date DESC")
    fun getChargingRecordsByVehicle(userId: Long, vehicleId: Long): Flow<List<ChargingRecordEntity>>

    @Query("SELECT * FROM charging_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getChargingRecordsByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<ChargingRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChargingRecord(chargingRecord: ChargingRecordEntity): Long

    @Delete
    suspend fun deleteChargingRecord(chargingRecord: ChargingRecordEntity)

    @Query("DELETE FROM charging_records WHERE id = :id")
    suspend fun deleteChargingById(id: Long)

    @Query("DELETE FROM charging_records WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface HomeExpenseDao {
    @Query("SELECT * FROM home_expenses WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getHomeExpensesByUser(userId: Long): Flow<List<HomeExpenseEntity>>

    @Query("SELECT * FROM home_expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getHomeExpensesByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<HomeExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeExpense(expense: HomeExpenseEntity): Long

    @Delete
    suspend fun deleteHomeExpense(expense: HomeExpenseEntity)

    @Query("DELETE FROM home_expenses WHERE id = :id")
    suspend fun deleteHomeExpenseById(id: Long)

    @Query("DELETE FROM home_expenses WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface OtherIncomeDao {
    @Query("SELECT * FROM other_income WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getOtherIncomesByUser(userId: Long): Flow<List<OtherIncomeEntity>>

    @Query("SELECT * FROM other_income WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getOtherIncomesByDateRange(userId: Long, startDate: String, endDate: String): Flow<List<OtherIncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtherIncome(income: OtherIncomeEntity): Long

    @Delete
    suspend fun deleteOtherIncome(income: OtherIncomeEntity)

    @Query("DELETE FROM other_income WHERE id = :id")
    suspend fun deleteOtherIncomeById(id: Long)

    @Query("DELETE FROM other_income WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear")
    fun getBudgetsByMonth(userId: Long, monthYear: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getAllBudgetsByUser(userId: Long): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE userId = :userId ORDER BY id DESC")
    fun getAlertsByUser(userId: Long): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity): Long

    @Query("UPDATE alerts SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)

    @Query("DELETE FROM alerts WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)
}

@Dao
interface CustomCategoryDao {
    @Query("SELECT * FROM custom_categories WHERE userId = :userId AND categoryType = :type")
    fun getCategoriesByType(userId: Long, type: String): Flow<List<CustomCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CustomCategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CustomCategoryEntity)
}

@Dao
interface UserSettingDao {
    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    fun getSettingsByUser(userId: Long): Flow<UserSettingEntity?>

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettingsDirect(userId: Long): UserSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingEntity)
}
