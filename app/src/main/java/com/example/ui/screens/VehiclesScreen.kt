package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.VehicleEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary

@Composable
fun VehiclesScreen(
    vehicles: List<VehicleEntity>,
    vehicleSummaries: List<VehicleSummary>,
    onAddOrUpdateVehicle: (VehicleEntity) -> Unit,
    onDeleteVehicle: (VehicleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedVehicleForEdit by remember { mutableStateOf<VehicleEntity?>(null) }
    var selectedVehicleDetail by remember { mutableStateOf<VehicleSummary?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getString("my_vehicles"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = {
                        selectedVehicleForEdit = null
                        showAddDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_vehicle_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vehicle")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = LanguageManager.getString("add_vehicle"), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (vehicles.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LanguageManager.getString("empty_vehicles"),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                        ) {
                            Text("+ پہلی گاڑی شامل کریں")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(vehicles) { vehicle ->
                        val summary = vehicleSummaries.find { it.vehicle.id == vehicle.id }
                        VehicleCardItem(
                            vehicle = vehicle,
                            summary = summary,
                            onClick = {
                                selectedVehicleDetail = summary
                            },
                            onEdit = {
                                selectedVehicleForEdit = vehicle
                                showAddDialog = true
                            },
                            onDelete = {
                                onDeleteVehicle(vehicle)
                            }
                        )
                    }
                }
            }
        }
    }

    // Vehicle Add/Edit Dialog
    if (showAddDialog) {
        VehicleFormDialog(
            existingVehicle = selectedVehicleForEdit,
            onDismiss = { showAddDialog = false },
            onSave = { v ->
                onAddOrUpdateVehicle(v)
                showAddDialog = false
            }
        )
    }

    // Vehicle Detail Dialog
    if (selectedVehicleDetail != null) {
        VehicleDetailDialog(
            summary = selectedVehicleDetail!!,
            onDismiss = { selectedVehicleDetail = null }
        )
    }
}

@Composable
private fun VehicleCardItem(
    vehicle: VehicleEntity,
    summary: VehicleSummary?,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconVector = when (vehicle.vehicleType) {
                        "Rickshaw" -> Icons.Default.DirectionsBus
                        "Car" -> Icons.Default.DirectionsCar
                        else -> Icons.Default.TwoWheeler
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = vehicle.vehicleType,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = vehicle.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${vehicle.vehicleType} • ${vehicle.fuelType} • ${vehicle.registrationNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Red500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = LanguageManager.getString("gross_income"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FinancialEngine.formatMoney(summary?.grossIncome ?: 0.0),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald500
                    )
                }

                Column {
                    Text(
                        text = LanguageManager.getString("vehicle_expenses"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FinancialEngine.formatMoney(summary?.totalVehicleCost ?: 0.0),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Red500
                    )
                }

                Column {
                    Text(
                        text = LanguageManager.getString("net_profit"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val netVal = summary?.netVehicleIncome ?: 0.0
                    Text(
                        text = FinancialEngine.formatMoney(netVal),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (netVal >= 0) Emerald500 else Red500
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleFormDialog(
    existingVehicle: VehicleEntity?,
    onDismiss: () -> Unit,
    onSave: (VehicleEntity) -> Unit
) {
    var name by remember { mutableStateOf(existingVehicle?.name ?: "") }
    var vehicleType by remember { mutableStateOf(existingVehicle?.vehicleType ?: "Motorcycle") }
    var fuelType by remember { mutableStateOf(existingVehicle?.fuelType ?: "Petrol") }
    var purchasePrice by remember { mutableStateOf(existingVehicle?.purchasePrice?.toString() ?: "") }
    var regNumber by remember { mutableStateOf(existingVehicle?.registrationNumber ?: "") }
    var dailyTarget by remember { mutableStateOf(existingVehicle?.dailyIncomeTarget?.toString() ?: "") }
    var monthlyTarget by remember { mutableStateOf(existingVehicle?.monthlyIncomeTarget?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (existingVehicle == null) LanguageManager.getString("add_vehicle") else LanguageManager.getString("edit")) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(LanguageManager.getString("vehicle_name")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { regNumber = it },
                    label = { Text(LanguageManager.getString("reg_number")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dailyTarget,
                    onValueChange = { dailyTarget = it },
                    label = { Text(LanguageManager.getString("daily_target") + " (Rs.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = monthlyTarget,
                    onValueChange = { monthlyTarget = it },
                    label = { Text(LanguageManager.getString("monthly_target") + " (Rs.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = (existingVehicle ?: VehicleEntity(userId = 0, name = name, vehicleType = vehicleType, fuelType = fuelType)).copy(
                        name = name.ifBlank { "My Vehicle" },
                        vehicleType = vehicleType,
                        fuelType = fuelType,
                        registrationNumber = regNumber,
                        purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                        dailyIncomeTarget = dailyTarget.toDoubleOrNull() ?: 0.0,
                        monthlyIncomeTarget = monthlyTarget.toDoubleOrNull() ?: 0.0
                    )
                    onSave(v)
                }
            ) {
                Text(LanguageManager.getString("save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LanguageManager.getString("cancel"))
            }
        }
    )
}

@Composable
private fun VehicleDetailDialog(
    summary: VehicleSummary,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = summary.vehicle.name, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "Gross Income: " + FinancialEngine.formatMoney(summary.grossIncome), fontWeight = FontWeight.Bold, color = Emerald500)
                Text(text = "Total Expenses: " + FinancialEngine.formatMoney(summary.totalVehicleCost), fontWeight = FontWeight.Bold, color = Red500)
                Text(text = "Net Profit: " + FinancialEngine.formatMoney(summary.netVehicleIncome), fontWeight = FontWeight.Bold)
                Text(text = "Total KM Driven: " + FinancialEngine.formatKm(summary.totalKm))
                Text(text = "Cost Per KM: " + FinancialEngine.formatMoney(summary.costPerKm))
                Text(text = "Income Per Hour: " + FinancialEngine.formatMoney(summary.incomePerHour))
                Text(text = "Net Profit Per KM: " + FinancialEngine.formatMoney(summary.netProfitPerKm))
                Text(text = "Profit Margin: " + FinancialEngine.formatNumber(summary.profitMarginPercent) + "%")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
