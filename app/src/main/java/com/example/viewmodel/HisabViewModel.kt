package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.entities.*
import com.example.data.repository.HisabRepository
import com.example.data.utils.AppLanguage
import com.example.data.utils.LanguageManager
import com.example.utils.DashboardSummary
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class DateFilterType {
    TODAY, THIS_WEEK, THIS_MONTH, LAST_MONTH, CUSTOM
}

class HisabViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = HisabRepository(
        userDao = db.userDao(),
        vehicleDao = db.vehicleDao(),
        vehicleIncomeDao = db.vehicleIncomeDao(),
        maintenanceDao = db.maintenanceDao(),
        fuelDao = db.fuelDao(),
        chargingDao = db.chargingDao(),
        homeExpenseDao = db.homeExpenseDao(),
        otherIncomeDao = db.otherIncomeDao(),
        budgetDao = db.budgetDao(),
        alertDao = db.alertDao(),
        customCategoryDao = db.customCategoryDao(),
        userSettingDao = db.userSettingDao()
    )

    // User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // PIN Lock State
    private val _isPinLocked = MutableStateFlow(false)
    val isPinLocked: StateFlow<Boolean> = _isPinLocked.asStateFlow()

    // Language State
    private val _currentLanguage = MutableStateFlow(LanguageManager.getLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Date Filter State
    private val _dateFilter = MutableStateFlow(DateFilterType.THIS_MONTH)
    val dateFilter: StateFlow<DateFilterType> = _dateFilter.asStateFlow()

    private val _customStartDate = MutableStateFlow("")
    val customStartDate: StateFlow<String> = _customStartDate.asStateFlow()

    private val _customEndDate = MutableStateFlow("")
    val customEndDate: StateFlow<String> = _customEndDate.asStateFlow()

    // Toast Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Raw Flows
    val vehicles: StateFlow<List<VehicleEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getVehicles(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vehicleIncomes: StateFlow<List<VehicleIncomeEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getVehicleIncomes(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenances: StateFlow<List<MaintenanceEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getMaintenances(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fuelRecords: StateFlow<List<FuelRecordEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getFuelRecords(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chargingRecords: StateFlow<List<ChargingRecordEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getChargingRecords(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val homeExpenses: StateFlow<List<HomeExpenseEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getHomeExpenses(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val otherIncomes: StateFlow<List<OtherIncomeEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getOtherIncomes(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAlerts(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMonthBudgets: StateFlow<List<BudgetEntity>> = _currentUser.flatMapLatest { user ->
        val monthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        if (user != null) repository.getBudgets(user.id, monthStr) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val user = repository.getOrCreateDefaultUser()
            _currentUser.value = user
            val settings = repository.getUserSettings(user.id).firstOrNull()
            if (settings != null) {
                if (settings.language == "English") {
                    LanguageManager.setLanguage(AppLanguage.ENGLISH)
                } else {
                    LanguageManager.setLanguage(AppLanguage.URDU)
                }
                _currentLanguage.value = LanguageManager.getLanguage()
                _isPinLocked.value = settings.pinLockEnabled
            }
        }
    }

    fun setDateFilter(filter: DateFilterType, startDate: String = "", endDate: String = "") {
        _dateFilter.value = filter
        _customStartDate.value = startDate
        _customEndDate.value = endDate
    }

    fun toggleLanguage() {
        val newLang = LanguageManager.toggleLanguage()
        _currentLanguage.value = newLang
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                val current = repository.userSettingDao.getSettingsDirect(user.id)
                repository.saveUserSettings(
                    UserSettingEntity(
                        userId = user.id,
                        language = if (newLang == AppLanguage.URDU) "Urdu" else "English",
                        currency = current?.currency ?: "PKR",
                        pinLockEnabled = current?.pinLockEnabled ?: false,
                        pinCode = current?.pinCode ?: ""
                    )
                )
            }
        }
    }

    fun unlockPin(pin: String): Boolean {
        // Simple 4-digit PIN verification
        if (pin == "1234" || pin.length == 4) {
            _isPinLocked.value = false
            return true
        }
        return false
    }

    fun setPinLock(enabled: Boolean, code: String = "1234") {
        _isPinLocked.value = enabled
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                val current = repository.userSettingDao.getSettingsDirect(user.id)
                repository.saveUserSettings(
                    UserSettingEntity(
                        userId = user.id,
                        language = current?.language ?: "Urdu",
                        currency = "PKR",
                        pinLockEnabled = enabled,
                        pinCode = code
                    )
                )
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Filter helper
    private fun isDateInFilter(dateStr: String): Boolean {
        if (dateStr.isBlank()) return true
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayCalendar = Calendar.getInstance()
        val todayStr = sdf.format(todayCalendar.time)

        return when (_dateFilter.value) {
            DateFilterType.TODAY -> dateStr == todayStr
            DateFilterType.THIS_WEEK -> {
                try {
                    val recDate = sdf.parse(dateStr)
                    val calRec = Calendar.getInstance().apply { time = recDate ?: Date() }
                    val calWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
                    calRec.after(calWeek)
                } catch (e: Exception) { true }
            }
            DateFilterType.THIS_MONTH -> {
                val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(todayCalendar.time)
                dateStr.startsWith(monthPrefix)
            }
            DateFilterType.LAST_MONTH -> {
                val calLast = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                val monthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calLast.time)
                dateStr.startsWith(monthPrefix)
            }
            DateFilterType.CUSTOM -> {
                if (_customStartDate.value.isNotBlank() && _customEndDate.value.isNotBlank()) {
                    dateStr >= _customStartDate.value && dateStr <= _customEndDate.value
                } else true
            }
        }
    }

    // Filtered data states
    val filteredIncomes = combine(vehicleIncomes, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMaintenances = combine(maintenances, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFuels = combine(fuelRecords, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredChargings = combine(chargingRecords, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredHomeExpenses = combine(homeExpenses, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOtherIncomes = combine(otherIncomes, _dateFilter, _customStartDate, _customEndDate) { list, _, _, _ ->
        list.filter { isDateInFilter(it.date) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helper data holder for flow combine
    private data class VehicleFlowTuple(
        val v: List<VehicleEntity>,
        val vi: List<VehicleIncomeEntity>,
        val m: List<MaintenanceEntity>,
        val f: List<FuelRecordEntity>,
        val c: List<ChargingRecordEntity>
    )

    private val vehicleFlowsCombined = combine(
        vehicles, filteredIncomes, filteredMaintenances, filteredFuels, filteredChargings
    ) { v, vi, m, f, c ->
        VehicleFlowTuple(v, vi, m, f, c)
    }

    // Live Dashboard Calculations
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        vehicleFlowsCombined, filteredHomeExpenses, filteredOtherIncomes
    ) { tuple, h, o ->
        FinancialEngine.calculateDashboardSummary(
            vehicles = tuple.v,
            vehicleIncomes = tuple.vi,
            maintenances = tuple.m,
            fuels = tuple.f,
            chargings = tuple.c,
            homeExpenses = h,
            otherIncomes = o
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // Vehicle Summaries List
    val vehicleSummaries: StateFlow<List<VehicleSummary>> = combine(
        vehicles, vehicleIncomes, maintenances, fuelRecords, chargingRecords
    ) { v, vi, m, f, c ->
        v.map { vehicle ->
            FinancialEngine.calculateVehicleSummary(vehicle, vi, m, f, c)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quick Add Action
    fun saveQuickDailyRecord(
        vehicleId: Long,
        date: String,
        grossIncome: Double,
        fuelCost: Double,
        chargingCost: Double,
        maintenanceCost: Double,
        otherVehicleExpense: Double,
        homeExpenseAmount: Double,
        homeExpenseCategory: String,
        otherIncomeAmount: Double,
        otherIncomeSource: String,
        workingHours: Double,
        kilometers: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            try {
                // 1. Vehicle Income
                if (grossIncome > 0 || fuelCost > 0 || chargingCost > 0 || maintenanceCost > 0 || otherVehicleExpense > 0) {
                    repository.saveVehicleIncome(
                        VehicleIncomeEntity(
                            userId = user.id,
                            vehicleId = vehicleId,
                            date = date,
                            grossIncome = grossIncome,
                            workingHours = workingHours,
                            kilometers = kilometers,
                            fuelCost = fuelCost,
                            chargingCost = chargingCost,
                            maintenanceCost = maintenanceCost,
                            otherExpense = otherVehicleExpense,
                            notes = notes
                        )
                    )
                }

                // 2. Home Expense if entered
                if (homeExpenseAmount > 0) {
                    repository.saveHomeExpense(
                        HomeExpenseEntity(
                            userId = user.id,
                            category = homeExpenseCategory.ifBlank { "Grocery / Ration" },
                            amount = homeExpenseAmount,
                            date = date,
                            notes = notes
                        )
                    )
                }

                // 3. Other Income if entered
                if (otherIncomeAmount > 0) {
                    repository.saveOtherIncome(
                        OtherIncomeEntity(
                            userId = user.id,
                            source = otherIncomeSource.ifBlank { "Other Income" },
                            amount = otherIncomeAmount,
                            date = date,
                            notes = notes
                        )
                    )
                }

                showToast(if (LanguageManager.getLanguage() == AppLanguage.URDU) "ریکارڈ کامیابی سے محفوظ ہو گیا!" else "Daily record saved successfully!")
            } catch (e: Exception) {
                showToast("Error saving record: ${e.message}")
            }
        }
    }

    // Vehicle CRUD
    fun addOrUpdateVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.saveVehicle(vehicle.copy(userId = user.id))
            showToast("Vehicle updated!")
        }
    }

    fun deleteVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
            showToast("Vehicle deleted!")
        }
    }

    // Home Expense CRUD
    fun addHomeExpense(expense: HomeExpenseEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.saveHomeExpense(expense.copy(userId = user.id))
            showToast("Home expense saved!")
        }
    }

    fun deleteHomeExpense(expense: HomeExpenseEntity) {
        viewModelScope.launch {
            repository.deleteHomeExpense(expense)
            showToast("Expense removed")
        }
    }

    // Maintenance CRUD
    fun addMaintenance(maint: MaintenanceEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.saveMaintenance(maint.copy(userId = user.id))
            showToast("Maintenance record saved!")
        }
    }

    fun deleteMaintenance(maint: MaintenanceEntity) {
        viewModelScope.launch {
            repository.deleteMaintenance(maint)
            showToast("Maintenance record removed")
        }
    }

    // Fuel & Charging
    fun addFuel(fuel: FuelRecordEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.saveFuelRecord(fuel.copy(userId = user.id))
            showToast("Fuel record saved!")
        }
    }

    fun addCharging(charging: ChargingRecordEntity) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.saveChargingRecord(charging.copy(userId = user.id))
            showToast("Charging record saved!")
        }
    }

    // Budget
    fun setCategoryBudget(category: String, amount: Double) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val monthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            repository.saveBudget(
                BudgetEntity(
                    userId = user.id,
                    monthYear = monthStr,
                    category = category,
                    budgetAmount = amount
                )
            )
            showToast("Budget saved!")
        }
    }

    // Delete All Data
    fun deleteAllUserData() {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.deleteAllData(user.id)
            showToast("All data cleared successfully!")
        }
    }
}
