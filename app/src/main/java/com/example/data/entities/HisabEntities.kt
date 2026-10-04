package com.example.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val pinHash: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "vehicles",
    indices = [Index(value = ["userId"])]
)
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val vehicleType: String, // Motorcycle, Rickshaw, Scooter, Car, Other
    val fuelType: String, // Petrol, Electric, Hybrid, Other
    val purchasePrice: Double = 0.0,
    val purchaseDate: String = "",
    val currentValue: Double = 0.0,
    val registrationNumber: String = "",
    val dailyIncomeTarget: Double = 0.0,
    val monthlyIncomeTarget: Double = 0.0,
    val currentMileage: Double = 0.0,
    val notes: String = "",
    val status: String = "Active" // Active, Archived
)

@Entity(
    tableName = "vehicle_income",
    indices = [Index(value = ["userId"]), Index(value = ["vehicleId"]), Index(value = ["date"])]
)
data class VehicleIncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val vehicleId: Long,
    val date: String, // YYYY-MM-DD
    val grossIncome: Double,
    val workingHours: Double = 0.0,
    val kilometers: Double = 0.0,
    val fuelCost: Double = 0.0,
    val chargingCost: Double = 0.0,
    val maintenanceCost: Double = 0.0,
    val otherExpense: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "maintenance",
    indices = [Index(value = ["userId"]), Index(value = ["vehicleId"]), Index(value = ["date"])]
)
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val vehicleId: Long,
    val date: String,
    val maintenanceType: String, // Engine Oil, Tyre, Brake, Motor, Controller, Battery, Service/Labor, Spare Parts, Other
    val description: String = "",
    val partsCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val mileage: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "fuel_records",
    indices = [Index(value = ["userId"]), Index(value = ["vehicleId"]), Index(value = ["date"])]
)
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val vehicleId: Long,
    val date: String,
    val litres: Double,
    val pricePerLitre: Double,
    val totalCost: Double,
    val kilometers: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "charging_records",
    indices = [Index(value = ["userId"]), Index(value = ["vehicleId"]), Index(value = ["date"])]
)
data class ChargingRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val vehicleId: Long,
    val date: String,
    val unitsKwh: Double,
    val electricityRate: Double,
    val chargingCost: Double,
    val kilometers: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "home_expenses",
    indices = [Index(value = ["userId"]), Index(value = ["date"]), Index(value = ["category"])]
)
data class HomeExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val category: String, // Electricity, Gas, Grocery, School Fees, Medical, Rent, etc.
    val description: String = "",
    val amount: Double,
    val paymentMethod: String = "Cash", // Cash, Bank, JazzCash, EasyPaisa, Card, Other
    val date: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "other_income",
    indices = [Index(value = ["userId"]), Index(value = ["date"]), Index(value = ["source"])]
)
data class OtherIncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val source: String, // Job Salary, Freelancing, YouTube, Facebook, Online Business, Other
    val amount: Double,
    val description: String = "",
    val date: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["userId"]), Index(value = ["monthYear"]), Index(value = ["category"])]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val monthYear: String, // YYYY-MM
    val category: String,
    val budgetAmount: Double
)

@Entity(
    tableName = "alerts",
    indices = [Index(value = ["userId"]), Index(value = ["date"])]
)
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val date: String,
    val type: String, // MaintenanceDue, HighFuel, BudgetExceeded, IncomeTarget, Warning
    val isRead: Boolean = false,
    val relatedVehicleId: Long? = null
)

@Entity(
    tableName = "custom_categories",
    indices = [Index(value = ["userId"])]
)
data class CustomCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val categoryType: String, // HomeExpense, IncomeSource, MaintenanceType
    val name: String
)

@Entity(
    tableName = "user_settings"
)
data class UserSettingEntity(
    @PrimaryKey val userId: Long,
    val language: String = "Urdu", // Urdu, English
    val currency: String = "PKR",
    val themeMode: String = "System", // System, Light, Dark
    val pinLockEnabled: Boolean = false,
    val pinCode: String = ""
)
