package com.example.utils

import com.example.data.entities.*
import java.text.DecimalFormat

data class VehicleSummary(
    val vehicle: VehicleEntity,
    val grossIncome: Double = 0.0,
    val fuelCost: Double = 0.0,
    val chargingCost: Double = 0.0,
    val maintenanceCost: Double = 0.0,
    val otherExpenses: Double = 0.0,
    val totalVehicleCost: Double = 0.0,
    val netVehicleIncome: Double = 0.0,
    val totalHours: Double = 0.0,
    val totalKm: Double = 0.0,
    val costPerKm: Double = 0.0,
    val incomePerHour: Double = 0.0,
    val netProfitPerKm: Double = 0.0,
    val profitMarginPercent: Double = 0.0
)

data class DashboardSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val vehicleExpenses: Double = 0.0,
    val homeExpenses: Double = 0.0,
    val maintenanceExpenses: Double = 0.0,
    val fuelExpenses: Double = 0.0,
    val chargingExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val savings: Double = 0.0,
    val profitMarginPercent: Double = 0.0,
    val previousTotalIncome: Double = 0.0,
    val previousTotalExpenses: Double = 0.0,
    val previousNetProfit: Double = 0.0,
    val incomeChangePercent: Double = 0.0,
    val expenseChangePercent: Double = 0.0,
    val profitChangePercent: Double = 0.0,
    val topEarningVehicleName: String = "",
    val topCostVehicleName: String = "",
    val topHomeExpenseCategory: String = ""
)

object FinancialEngine {
    private val formatter = DecimalFormat("#,##0.##")

    fun formatMoney(amount: Double, symbol: String = "Rs. "): String {
        return "$symbol${formatter.format(amount)}"
    }

    fun formatKm(km: Double): String {
        return "${formatter.format(km)} KM"
    }

    fun formatNumber(valNumber: Double): String {
        return formatter.format(valNumber)
    }

    fun calculateVehicleSummary(
        vehicle: VehicleEntity,
        incomes: List<VehicleIncomeEntity>,
        maintenances: List<MaintenanceEntity>,
        fuels: List<FuelRecordEntity>,
        chargings: List<ChargingRecordEntity>
    ): VehicleSummary {
        val vehicleIncomes = incomes.filter { it.vehicleId == vehicle.id }
        val vehicleMaint = maintenances.filter { it.vehicleId == vehicle.id }
        val vehicleFuel = fuels.filter { it.vehicleId == vehicle.id }
        val vehicleCharg = chargings.filter { it.vehicleId == vehicle.id }

        val gross = vehicleIncomes.sumOf { it.grossIncome }
        val directFuelFromIncome = vehicleIncomes.sumOf { it.fuelCost }
        val fuelFromRecords = vehicleFuel.sumOf { it.totalCost }
        val fuel = maxOf(directFuelFromIncome, fuelFromRecords)

        val directChargingFromIncome = vehicleIncomes.sumOf { it.chargingCost }
        val chargingFromRecords = vehicleCharg.sumOf { it.chargingCost }
        val charging = maxOf(directChargingFromIncome, chargingFromRecords)

        val directMaintFromIncome = vehicleIncomes.sumOf { it.maintenanceCost }
        val maintFromRecords = vehicleMaint.sumOf { it.totalCost }
        val maintenance = maxOf(directMaintFromIncome, maintFromRecords)

        val otherEx = vehicleIncomes.sumOf { it.otherExpense }

        val totalCost = fuel + charging + maintenance + otherEx
        val netIncome = gross - totalCost

        val totalHours = vehicleIncomes.sumOf { it.workingHours }
        val kmFromIncomes = vehicleIncomes.sumOf { it.kilometers }
        val kmFromFuel = vehicleFuel.sumOf { it.kilometers }
        val kmFromCharg = vehicleCharg.sumOf { it.kilometers }
        val totalKm = maxOf(kmFromIncomes, maxOf(kmFromFuel, kmFromCharg))

        val costPerKm = if (totalKm > 0) totalCost / totalKm else 0.0
        val incomePerHour = if (totalHours > 0) gross / totalHours else 0.0
        val netProfitPerKm = if (totalKm > 0) netIncome / totalKm else 0.0
        val profitMargin = if (gross > 0) (netIncome / gross) * 100.0 else 0.0

        return VehicleSummary(
            vehicle = vehicle,
            grossIncome = gross,
            fuelCost = fuel,
            chargingCost = charging,
            maintenanceCost = maintenance,
            otherExpenses = otherEx,
            totalVehicleCost = totalCost,
            netVehicleIncome = netIncome,
            totalHours = totalHours,
            totalKm = totalKm,
            costPerKm = costPerKm,
            incomePerHour = incomePerHour,
            netProfitPerKm = netProfitPerKm,
            profitMarginPercent = profitMargin
        )
    }

    fun calculateDashboardSummary(
        vehicles: List<VehicleEntity>,
        vehicleIncomes: List<VehicleIncomeEntity>,
        maintenances: List<MaintenanceEntity>,
        fuels: List<FuelRecordEntity>,
        chargings: List<ChargingRecordEntity>,
        homeExpenses: List<HomeExpenseEntity>,
        otherIncomes: List<OtherIncomeEntity>,
        prevIncomes: Double = 0.0,
        prevExpenses: Double = 0.0,
        prevNetProfit: Double = 0.0
    ): DashboardSummary {
        val totalVehicleGross = vehicleIncomes.sumOf { it.grossIncome }
        val totalOtherIncome = otherIncomes.sumOf { it.amount }
        val totalIncome = totalVehicleGross + totalOtherIncome

        val fuelFromRecords = fuels.sumOf { it.totalCost }
        val fuelFromIncome = vehicleIncomes.sumOf { it.fuelCost }
        val totalFuel = maxOf(fuelFromRecords, fuelFromIncome)

        val chargingFromRecords = chargings.sumOf { it.chargingCost }
        val chargingFromIncome = vehicleIncomes.sumOf { it.chargingCost }
        val totalCharging = maxOf(chargingFromRecords, chargingFromIncome)

        val maintFromRecords = maintenances.sumOf { it.totalCost }
        val maintFromIncome = vehicleIncomes.sumOf { it.maintenanceCost }
        val totalMaint = maxOf(maintFromRecords, maintFromIncome)

        val totalOtherVehicleEx = vehicleIncomes.sumOf { it.otherExpense }

        val vehicleExpenses = totalFuel + totalCharging + totalMaint + totalOtherVehicleEx
        val homeEx = homeExpenses.sumOf { it.amount }

        val totalExpenses = vehicleExpenses + homeEx
        val netProfit = totalIncome - totalExpenses
        val savings = maxOf(0.0, netProfit)

        val profitMargin = if (totalIncome > 0) (netProfit / totalIncome) * 100.0 else 0.0

        val incomeChange = calculatePercentageChange(prevIncomes, totalIncome)
        val expenseChange = calculatePercentageChange(prevExpenses, totalExpenses)
        val profitChange = calculatePercentageChange(prevNetProfit, netProfit)

        // Find top vehicle
        val vehicleSummaries = vehicles.map {
            calculateVehicleSummary(it, vehicleIncomes, maintenances, fuels, chargings)
        }
        val topEarning = vehicleSummaries.maxByOrNull { it.grossIncome }?.vehicle?.name ?: "-"
        val topCostVehicle = vehicleSummaries.maxByOrNull { it.totalVehicleCost }?.vehicle?.name ?: "-"

        // Top home category
        val topHomeCategory = homeExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .maxByOrNull { it.value }?.key ?: "-"

        return DashboardSummary(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            vehicleExpenses = vehicleExpenses,
            homeExpenses = homeEx,
            maintenanceExpenses = totalMaint,
            fuelExpenses = totalFuel,
            chargingExpenses = totalCharging,
            netProfit = netProfit,
            savings = savings,
            profitMarginPercent = profitMargin,
            previousTotalIncome = prevIncomes,
            previousTotalExpenses = prevExpenses,
            previousNetProfit = prevNetProfit,
            incomeChangePercent = incomeChange,
            expenseChangePercent = expenseChange,
            profitChangePercent = profitChange,
            topEarningVehicleName = topEarning,
            topCostVehicleName = topCostVehicle,
            topHomeExpenseCategory = topHomeCategory
        )
    }

    private fun calculatePercentageChange(oldValue: Double, newValue: Double): Double {
        if (oldValue == 0.0) return if (newValue > 0) 100.0 else 0.0
        return ((newValue - oldValue) / oldValue) * 100.0
    }
}
