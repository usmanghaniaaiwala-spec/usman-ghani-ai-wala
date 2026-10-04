package com.example.data.repository

import com.example.data.dao.*
import com.example.data.entities.*
import com.example.data.utils.LanguageManager
import com.example.utils.ExportBackupData
import com.example.utils.ExporterImporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class HisabRepository(
    val userDao: UserDao,
    val vehicleDao: VehicleDao,
    val vehicleIncomeDao: VehicleIncomeDao,
    val maintenanceDao: MaintenanceDao,
    val fuelDao: FuelDao,
    val chargingDao: ChargingDao,
    val homeExpenseDao: HomeExpenseDao,
    val otherIncomeDao: OtherIncomeDao,
    val budgetDao: BudgetDao,
    val alertDao: AlertDao,
    val customCategoryDao: CustomCategoryDao,
    val userSettingDao: UserSettingDao
) {
    suspend fun getOrCreateDefaultUser(): UserEntity {
        var user = userDao.getFirstUser()
        if (user == null) {
            val newUserId = userDao.insertUser(
                UserEntity(
                    username = "user",
                    passwordHash = "123456",
                    fullName = "Pakistani Driver / Owner"
                )
            )
            user = userDao.getUserById(newUserId) ?: UserEntity(id = newUserId, username = "user", passwordHash = "123456", fullName = "Pakistani Driver / Owner")

            // Create default settings
            userSettingDao.saveSettings(UserSettingEntity(userId = newUserId, language = "Urdu", currency = "PKR"))

            // Pre-populate default vehicles for Pakistani users
            createDefaultVehiclesForUser(newUserId)

            // Pre-populate default budget categories
            createDefaultBudgetsForUser(newUserId)
        }
        return user
    }

    private suspend fun createDefaultVehiclesForUser(userId: Long) {
        val defaultList = listOf(
            VehicleEntity(userId = userId, name = LanguageManager.getString("preset_bike125"), vehicleType = "Motorcycle", fuelType = "Petrol", purchasePrice = 185000.0, registrationNumber = "LEK-1250", dailyIncomeTarget = 3000.0, monthlyIncomeTarget = 90000.0),
            VehicleEntity(userId = userId, name = LanguageManager.getString("preset_cd70"), vehicleType = "Motorcycle", fuelType = "Petrol", purchasePrice = 145000.0, registrationNumber = "LHR-7070", dailyIncomeTarget = 2500.0, monthlyIncomeTarget = 75000.0),
            VehicleEntity(userId = userId, name = LanguageManager.getString("preset_ev_bike"), vehicleType = "Motorcycle", fuelType = "Electric", purchasePrice = 210000.0, registrationNumber = "EV-9910", dailyIncomeTarget = 3500.0, monthlyIncomeTarget = 100000.0),
            VehicleEntity(userId = userId, name = LanguageManager.getString("preset_ev_rickshaw"), vehicleType = "Rickshaw", fuelType = "Electric", purchasePrice = 380000.0, registrationNumber = "RIC-5520", dailyIncomeTarget = 5000.0, monthlyIncomeTarget = 150000.0),
            VehicleEntity(userId = userId, name = LanguageManager.getString("preset_ev_scooter"), vehicleType = "Scooter", fuelType = "Electric", purchasePrice = 175000.0, registrationNumber = "SCO-1100", dailyIncomeTarget = 2000.0, monthlyIncomeTarget = 60000.0)
        )
        defaultList.forEach { vehicleDao.insertVehicle(it) }
    }

    private suspend fun createDefaultBudgetsForUser(userId: Long) {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val currentMonth = sdf.format(Date())
        val defaults = mapOf(
            "Grocery / Ration" to 35000.0,
            "Electricity Bill" to 20000.0,
            "Gas Bill" to 3000.0,
            "Fuel" to 15000.0,
            "Maintenance" to 5000.0,
            "Rent" to 25000.0,
            "Medical Expenses" to 8000.0,
            "School Fees" to 12000.0,
            "Other Expenses" to 10000.0
        )
        defaults.forEach { (cat, amount) ->
            budgetDao.insertBudget(
                BudgetEntity(
                    userId = userId,
                    monthYear = currentMonth,
                    category = cat,
                    budgetAmount = amount
                )
            )
        }
    }

    // Vehicle operations
    fun getVehicles(userId: Long): Flow<List<VehicleEntity>> = vehicleDao.getVehiclesByUser(userId)
    fun getActiveVehicles(userId: Long): Flow<List<VehicleEntity>> = vehicleDao.getActiveVehiclesByUser(userId)
    suspend fun saveVehicle(vehicle: VehicleEntity): Long = vehicleDao.insertVehicle(vehicle)
    suspend fun deleteVehicle(vehicle: VehicleEntity) = vehicleDao.deleteVehicle(vehicle)

    // Vehicle Income operations
    fun getVehicleIncomes(userId: Long): Flow<List<VehicleIncomeEntity>> = vehicleIncomeDao.getIncomesByUser(userId)
    fun getVehicleIncomesByRange(userId: Long, start: String, end: String): Flow<List<VehicleIncomeEntity>> = vehicleIncomeDao.getIncomesByDateRange(userId, start, end)
    suspend fun saveVehicleIncome(income: VehicleIncomeEntity): Long = vehicleIncomeDao.insertIncome(income)
    suspend fun deleteVehicleIncome(income: VehicleIncomeEntity) = vehicleIncomeDao.deleteIncome(income)
    suspend fun deleteVehicleIncomeById(id: Long) = vehicleIncomeDao.deleteIncomeById(id)

    // Maintenance operations
    fun getMaintenances(userId: Long): Flow<List<MaintenanceEntity>> = maintenanceDao.getMaintenancesByUser(userId)
    fun getMaintenancesByRange(userId: Long, start: String, end: String): Flow<List<MaintenanceEntity>> = maintenanceDao.getMaintenancesByDateRange(userId, start, end)
    suspend fun saveMaintenance(maint: MaintenanceEntity): Long = maintenanceDao.insertMaintenance(maint)
    suspend fun deleteMaintenance(maint: MaintenanceEntity) = maintenanceDao.deleteMaintenance(maint)

    // Fuel operations
    fun getFuelRecords(userId: Long): Flow<List<FuelRecordEntity>> = fuelDao.getFuelRecordsByUser(userId)
    fun getFuelRecordsByRange(userId: Long, start: String, end: String): Flow<List<FuelRecordEntity>> = fuelDao.getFuelRecordsByDateRange(userId, start, end)
    suspend fun saveFuelRecord(fuel: FuelRecordEntity): Long = fuelDao.insertFuelRecord(fuel)
    suspend fun deleteFuelRecord(fuel: FuelRecordEntity) = fuelDao.deleteFuelRecord(fuel)

    // Charging operations
    fun getChargingRecords(userId: Long): Flow<List<ChargingRecordEntity>> = chargingDao.getChargingRecordsByUser(userId)
    fun getChargingRecordsByRange(userId: Long, start: String, end: String): Flow<List<ChargingRecordEntity>> = chargingDao.getChargingRecordsByDateRange(userId, start, end)
    suspend fun saveChargingRecord(charging: ChargingRecordEntity): Long = chargingDao.insertChargingRecord(charging)
    suspend fun deleteChargingRecord(charging: ChargingRecordEntity) = chargingDao.deleteChargingRecord(charging)

    // Home Expenses
    fun getHomeExpenses(userId: Long): Flow<List<HomeExpenseEntity>> = homeExpenseDao.getHomeExpensesByUser(userId)
    fun getHomeExpensesByRange(userId: Long, start: String, end: String): Flow<List<HomeExpenseEntity>> = homeExpenseDao.getHomeExpensesByDateRange(userId, start, end)
    suspend fun saveHomeExpense(expense: HomeExpenseEntity): Long = homeExpenseDao.insertHomeExpense(expense)
    suspend fun deleteHomeExpense(expense: HomeExpenseEntity) = homeExpenseDao.deleteHomeExpense(expense)

    // Other Income
    fun getOtherIncomes(userId: Long): Flow<List<OtherIncomeEntity>> = otherIncomeDao.getOtherIncomesByUser(userId)
    fun getOtherIncomesByRange(userId: Long, start: String, end: String): Flow<List<OtherIncomeEntity>> = otherIncomeDao.getOtherIncomesByDateRange(userId, start, end)
    suspend fun saveOtherIncome(income: OtherIncomeEntity): Long = otherIncomeDao.insertOtherIncome(income)
    suspend fun deleteOtherIncome(income: OtherIncomeEntity) = otherIncomeDao.deleteOtherIncome(income)

    // Budgets
    fun getBudgets(userId: Long, monthYear: String): Flow<List<BudgetEntity>> = budgetDao.getBudgetsByMonth(userId, monthYear)
    suspend fun saveBudget(budget: BudgetEntity): Long = budgetDao.insertBudget(budget)

    // Alerts
    fun getAlerts(userId: Long): Flow<List<AlertEntity>> = alertDao.getAlertsByUser(userId)
    suspend fun addAlert(alert: AlertEntity) = alertDao.insertAlert(alert)

    // Settings
    fun getUserSettings(userId: Long): Flow<UserSettingEntity?> = userSettingDao.getSettingsByUser(userId)
    suspend fun saveUserSettings(settings: UserSettingEntity) = userSettingDao.saveSettings(settings)

    // Export & Backup
    suspend fun getBackupData(userId: Long): ExportBackupData {
        val v = vehicleDao.getVehiclesByUser(userId).firstOrNull() ?: emptyList()
        val vi = vehicleIncomeDao.getIncomesByUser(userId).firstOrNull() ?: emptyList()
        val m = maintenanceDao.getMaintenancesByUser(userId).firstOrNull() ?: emptyList()
        val f = fuelDao.getFuelRecordsByUser(userId).firstOrNull() ?: emptyList()
        val c = chargingDao.getChargingRecordsByUser(userId).firstOrNull() ?: emptyList()
        val h = homeExpenseDao.getHomeExpensesByUser(userId).firstOrNull() ?: emptyList()
        val o = otherIncomeDao.getOtherIncomesByUser(userId).firstOrNull() ?: emptyList()
        val b = budgetDao.getAllBudgetsByUser(userId).firstOrNull() ?: emptyList()
        return ExportBackupData(v, vi, m, f, c, h, o, b)
    }

    suspend fun exportDataJson(userId: Long): String {
        val backup = getBackupData(userId)
        return ExporterImporter.exportToJson(backup)
    }

    suspend fun deleteAllData(userId: Long) {
        vehicleDao.deleteAllByUser(userId)
        vehicleIncomeDao.deleteAllByUser(userId)
        maintenanceDao.deleteAllByUser(userId)
        fuelDao.deleteAllByUser(userId)
        chargingDao.deleteAllByUser(userId)
        homeExpenseDao.deleteAllByUser(userId)
        otherIncomeDao.deleteAllByUser(userId)
        budgetDao.deleteAllByUser(userId)
        alertDao.deleteAllByUser(userId)
    }
}
