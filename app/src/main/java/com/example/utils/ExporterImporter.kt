package com.example.utils

import com.example.data.entities.*
import org.json.JSONArray
import org.json.JSONObject

data class ExportBackupData(
    val vehicles: List<VehicleEntity>,
    val vehicleIncomes: List<VehicleIncomeEntity>,
    val maintenances: List<MaintenanceEntity>,
    val fuels: List<FuelRecordEntity>,
    val chargings: List<ChargingRecordEntity>,
    val homeExpenses: List<HomeExpenseEntity>,
    val otherIncomes: List<OtherIncomeEntity>,
    val budgets: List<BudgetEntity>
)

object ExporterImporter {

    fun exportToJson(data: ExportBackupData): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        // Vehicles
        val vehiclesArr = JSONArray()
        data.vehicles.forEach { v ->
            val obj = JSONObject()
            obj.put("id", v.id)
            obj.put("userId", v.userId)
            obj.put("name", v.name)
            obj.put("vehicleType", v.vehicleType)
            obj.put("fuelType", v.fuelType)
            obj.put("purchasePrice", v.purchasePrice)
            obj.put("purchaseDate", v.purchaseDate)
            obj.put("currentValue", v.currentValue)
            obj.put("registrationNumber", v.registrationNumber)
            obj.put("dailyIncomeTarget", v.dailyIncomeTarget)
            obj.put("monthlyIncomeTarget", v.monthlyIncomeTarget)
            obj.put("currentMileage", v.currentMileage)
            obj.put("notes", v.notes)
            obj.put("status", v.status)
            vehiclesArr.put(obj)
        }
        root.put("vehicles", vehiclesArr)

        // Vehicle Incomes
        val incomeArr = JSONArray()
        data.vehicleIncomes.forEach { i ->
            val obj = JSONObject()
            obj.put("id", i.id)
            obj.put("userId", i.userId)
            obj.put("vehicleId", i.vehicleId)
            obj.put("date", i.date)
            obj.put("grossIncome", i.grossIncome)
            obj.put("workingHours", i.workingHours)
            obj.put("kilometers", i.kilometers)
            obj.put("fuelCost", i.fuelCost)
            obj.put("chargingCost", i.chargingCost)
            obj.put("maintenanceCost", i.maintenanceCost)
            obj.put("otherExpense", i.otherExpense)
            obj.put("notes", i.notes)
            incomeArr.put(obj)
        }
        root.put("vehicleIncomes", incomeArr)

        // Maintenances
        val maintArr = JSONArray()
        data.maintenances.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("userId", m.userId)
            obj.put("vehicleId", m.vehicleId)
            obj.put("date", m.date)
            obj.put("maintenanceType", m.maintenanceType)
            obj.put("description", m.description)
            obj.put("partsCost", m.partsCost)
            obj.put("laborCost", m.laborCost)
            obj.put("totalCost", m.totalCost)
            obj.put("mileage", m.mileage)
            obj.put("notes", m.notes)
            maintArr.put(obj)
        }
        root.put("maintenances", maintArr)

        // Fuels
        val fuelArr = JSONArray()
        data.fuels.forEach { f ->
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("userId", f.userId)
            obj.put("vehicleId", f.vehicleId)
            obj.put("date", f.date)
            obj.put("litres", f.litres)
            obj.put("pricePerLitre", f.pricePerLitre)
            obj.put("totalCost", f.totalCost)
            obj.put("kilometers", f.kilometers)
            obj.put("notes", f.notes)
            fuelArr.put(obj)
        }
        root.put("fuels", fuelArr)

        // Chargings
        val chargArr = JSONArray()
        data.chargings.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("userId", c.userId)
            obj.put("vehicleId", c.vehicleId)
            obj.put("date", c.date)
            obj.put("unitsKwh", c.unitsKwh)
            obj.put("electricityRate", c.electricityRate)
            obj.put("chargingCost", c.chargingCost)
            obj.put("kilometers", c.kilometers)
            obj.put("notes", c.notes)
            chargArr.put(obj)
        }
        root.put("chargings", chargArr)

        // Home Expenses
        val homeArr = JSONArray()
        data.homeExpenses.forEach { h ->
            val obj = JSONObject()
            obj.put("id", h.id)
            obj.put("userId", h.userId)
            obj.put("category", h.category)
            obj.put("description", h.description)
            obj.put("amount", h.amount)
            obj.put("paymentMethod", h.paymentMethod)
            obj.put("date", h.date)
            obj.put("notes", h.notes)
            homeArr.put(obj)
        }
        root.put("homeExpenses", homeArr)

        // Other Incomes
        val otherArr = JSONArray()
        data.otherIncomes.forEach { o ->
            val obj = JSONObject()
            obj.put("id", o.id)
            obj.put("userId", o.userId)
            obj.put("source", o.source)
            obj.put("amount", o.amount)
            obj.put("description", o.description)
            obj.put("date", o.date)
            obj.put("notes", o.notes)
            otherArr.put(obj)
        }
        root.put("otherIncomes", otherArr)

        // Budgets
        val budgetArr = JSONArray()
        data.budgets.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("userId", b.userId)
            obj.put("monthYear", b.monthYear)
            obj.put("category", b.category)
            obj.put("budgetAmount", b.budgetAmount)
            budgetArr.put(obj)
        }
        root.put("budgets", budgetArr)

        return root.toString(2)
    }

    fun generateCsvReport(data: ExportBackupData): String {
        val sb = StringBuilder()
        sb.append("--- HISABPRO FINANCIAL REPORT ---\n\n")

        sb.append("1. VEHICLES SUMMARY\n")
        sb.append("ID,Name,Type,FuelType,RegNumber,DailyTarget,MonthlyTarget\n")
        data.vehicles.forEach { v ->
            sb.append("${v.id},\"${v.name}\",\"${v.vehicleType}\",\"${v.fuelType}\",\"${v.registrationNumber}\",${v.dailyIncomeTarget},${v.monthlyIncomeTarget}\n")
        }

        sb.append("\n2. VEHICLE INCOMES\n")
        sb.append("Date,VehicleID,GrossIncome,WorkingHours,KM,FuelCost,ChargingCost,MaintenanceCost,OtherCost,Notes\n")
        data.vehicleIncomes.forEach { i ->
            sb.append("${i.date},${i.vehicleId},${i.grossIncome},${i.workingHours},${i.kilometers},${i.fuelCost},${i.chargingCost},${i.maintenanceCost},${i.otherExpense},\"${i.notes}\"\n")
        }

        sb.append("\n3. HOME EXPENSES\n")
        sb.append("Date,Category,Amount,PaymentMethod,Description,Notes\n")
        data.homeExpenses.forEach { h ->
            sb.append("${h.date},\"${h.category}\",${h.amount},\"${h.paymentMethod}\",\"${h.description}\",\"${h.notes}\"\n")
        }

        sb.append("\n4. MAINTENANCE RECORDS\n")
        sb.append("Date,VehicleID,Type,PartsCost,LaborCost,TotalCost,Description\n")
        data.maintenances.forEach { m ->
            sb.append("${m.date},${m.vehicleId},\"${m.maintenanceType}\",${m.partsCost},${m.laborCost},${m.totalCost},\"${m.description}\"\n")
        }

        return sb.toString()
    }
}
