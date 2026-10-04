package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.utils.LanguageManager
import com.example.ui.screens.*
import com.example.ui.theme.HisabProTheme
import com.example.viewmodel.HisabViewModel
import kotlinx.coroutines.runBlocking

enum class ScreenRoute(val titleKey: String, val icon: ImageVector) {
    DASHBOARD("nav_dashboard", Icons.Default.Dashboard),
    QUICK_ADD("nav_quick_add", Icons.Default.AddCircle),
    VEHICLES("nav_vehicles", Icons.Default.DirectionsCar),
    MAINTENANCE("nav_maintenance", Icons.Default.Build),
    EXPENSES("nav_expenses", Icons.Default.Home),
    FUEL_CHARGING("nav_fuel_charging", Icons.Default.LocalGasStation),
    BUDGETS("nav_budgets", Icons.Default.AccountBalanceWallet),
    COMPARISON("nav_comparison", Icons.Default.CompareArrows),
    REPORTS("nav_reports", Icons.Default.BarChart),
    SETTINGS("nav_more", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: HisabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val isPinLocked by viewModel.isPinLocked.collectAsStateWithLifecycle()
            val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
            val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

            var currentScreen by remember { mutableStateOf(ScreenRoute.DASHBOARD) }

            // Observe toast
            LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            HisabProTheme {
                CompositionLocalProvider(LocalLayoutDirection provides currentLanguage.layoutDirection) {
                    if (isPinLocked) {
                        AuthScreen(
                            onUnlock = { pin -> viewModel.unlockPin(pin) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Handle back button when on sub-screens
                        if (currentScreen != ScreenRoute.DASHBOARD) {
                            BackHandler {
                                currentScreen = ScreenRoute.DASHBOARD
                            }
                        }

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                        .testTag("bottom_nav_bar")
                                ) {
                                    val bottomBarItems = listOf(
                                        ScreenRoute.DASHBOARD,
                                        ScreenRoute.QUICK_ADD,
                                        ScreenRoute.VEHICLES,
                                        ScreenRoute.REPORTS,
                                        ScreenRoute.SETTINGS
                                    )
                                    bottomBarItems.forEach { route ->
                                        val isSelected = currentScreen == route
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { currentScreen = route },
                                            icon = {
                                                Icon(
                                                    imageVector = route.icon,
                                                    contentDescription = LanguageManager.getString(route.titleKey)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = LanguageManager.getString(route.titleKey),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            },
                                            modifier = Modifier.testTag("nav_item_${route.name}")
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            val modifier = Modifier.padding(innerPadding)

                            val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()
                            val dashboardSummary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                            val vehicleSummaries by viewModel.vehicleSummaries.collectAsStateWithLifecycle()
                            val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()

                            val vehicleIncomes by viewModel.filteredIncomes.collectAsStateWithLifecycle()
                            val maintenances by viewModel.filteredMaintenances.collectAsStateWithLifecycle()
                            val fuels by viewModel.filteredFuels.collectAsStateWithLifecycle()
                            val chargings by viewModel.filteredChargings.collectAsStateWithLifecycle()
                            val homeExpenses by viewModel.filteredHomeExpenses.collectAsStateWithLifecycle()
                            val budgets by viewModel.currentMonthBudgets.collectAsStateWithLifecycle()

                            when (currentScreen) {
                                ScreenRoute.DASHBOARD -> DashboardScreen(
                                    summary = dashboardSummary,
                                    vehicleSummaries = vehicleSummaries,
                                    currentFilter = dateFilter,
                                    onFilterSelected = { filter -> viewModel.setDateFilter(filter) },
                                    onNavigateToQuickAdd = { currentScreen = ScreenRoute.QUICK_ADD },
                                    onNavigateToVehicles = { currentScreen = ScreenRoute.VEHICLES },
                                    onNavigateToReports = { currentScreen = ScreenRoute.REPORTS },
                                    modifier = modifier
                                )

                                ScreenRoute.QUICK_ADD -> QuickAddScreen(
                                    vehicles = vehicles,
                                    onSaveRecord = { vId, d, g, f, c, m, ov, h, hc, oi, os, wh, km, n ->
                                        viewModel.saveQuickDailyRecord(
                                            vId, d, g, f, c, m, ov, h, hc, oi, os, wh, km, n
                                        )
                                        currentScreen = ScreenRoute.DASHBOARD
                                    },
                                    modifier = modifier
                                )

                                ScreenRoute.VEHICLES -> VehiclesScreen(
                                    vehicles = vehicles,
                                    vehicleSummaries = vehicleSummaries,
                                    onAddOrUpdateVehicle = { v -> viewModel.addOrUpdateVehicle(v) },
                                    onDeleteVehicle = { v -> viewModel.deleteVehicle(v) },
                                    modifier = modifier
                                )

                                ScreenRoute.MAINTENANCE -> MaintenanceScreen(
                                    vehicles = vehicles,
                                    maintenances = maintenances,
                                    onAddMaintenance = { m -> viewModel.addMaintenance(m) },
                                    onDeleteMaintenance = { m -> viewModel.deleteMaintenance(m) },
                                    modifier = modifier
                                )

                                ScreenRoute.EXPENSES -> ExpenseScreen(
                                    homeExpenses = homeExpenses,
                                    onAddHomeExpense = { h -> viewModel.addHomeExpense(h) },
                                    onDeleteHomeExpense = { h -> viewModel.deleteHomeExpense(h) },
                                    modifier = modifier
                                )

                                ScreenRoute.FUEL_CHARGING -> FuelChargingScreen(
                                    vehicles = vehicles,
                                    fuels = fuels,
                                    chargings = chargings,
                                    onAddFuel = { f -> viewModel.addFuel(f) },
                                    onAddCharging = { c -> viewModel.addCharging(c) },
                                    modifier = modifier
                                )

                                ScreenRoute.BUDGETS -> BudgetScreen(
                                    budgets = budgets,
                                    homeExpenses = homeExpenses,
                                    onSaveBudget = { cat, amt -> viewModel.setCategoryBudget(cat, amt) },
                                    modifier = modifier
                                )

                                ScreenRoute.COMPARISON -> VehicleComparisonScreen(
                                    vehicleSummaries = vehicleSummaries,
                                    modifier = modifier
                                )

                                ScreenRoute.REPORTS -> ReportsScreen(
                                    summary = dashboardSummary,
                                    vehicleSummaries = vehicleSummaries,
                                    onExportCsv = {
                                        runBlocking {
                                            viewModel.currentUser.value?.let { u ->
                                                val backup = viewModel.repository.getBackupData(u.id)
                                                val csv = com.example.utils.ExporterImporter.generateCsvReport(backup)
                                                viewModel.showToast("Report exported to clipboard / text!")
                                            }
                                        }
                                    },
                                    modifier = modifier
                                )

                                ScreenRoute.SETTINGS -> SettingsScreen(
                                    currentLanguage = currentLanguage,
                                    isPinLocked = isPinLocked,
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onTogglePinLock = { enabled -> viewModel.setPinLock(enabled) },
                                    onBackupJson = {
                                        runBlocking {
                                            viewModel.currentUser.value?.let { u ->
                                                viewModel.repository.exportDataJson(u.id)
                                            } ?: "{}"
                                        }
                                    },
                                    onDeleteAllData = { viewModel.deleteAllUserData() },
                                    modifier = modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
