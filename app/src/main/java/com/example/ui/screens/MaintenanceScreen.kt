package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entities.MaintenanceEntity
import com.example.data.entities.VehicleEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    vehicles: List<VehicleEntity>,
    maintenances: List<MaintenanceEntity>,
    onAddMaintenance: (MaintenanceEntity) -> Unit,
    onDeleteMaintenance: (MaintenanceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Engine Oil", "Oil Filter", "Air Filter", "Tyre", "Tube",
        "Brake Pads", "Brake Shoes", "Chain & Sprocket", "Battery",
        "Motor Repair", "Controller Repair", "Charger Repair", "Wiring & Lights",
        "Suspension", "Service / Labor", "Spare Parts", "Other"
    )

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
                    text = LanguageManager.getString("maintenance_module"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = LanguageManager.getString("add_maintenance"), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (maintenances.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "کوئی مرمت کا ریکارڈ نہیں ملا",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(maintenances) { item ->
                        val vehicleName = vehicles.find { it.id == item.vehicleId }?.name ?: "Vehicle #${item.vehicleId}"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = "Maint",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${item.maintenanceType} ($vehicleName)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${item.date} • Parts: Rs. ${item.partsCost} + Labor: Rs. ${item.laborCost}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = FinancialEngine.formatMoney(item.totalCost),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Red500
                                    )
                                    IconButton(onClick = { onDeleteMaintenance(item) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Red500)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var selectedVeh by remember { mutableStateOf(vehicles.firstOrNull()) }
        var date by remember { mutableStateOf(todayStr) }
        var maintType by remember { mutableStateOf(categories.first()) }
        var partsCostText by remember { mutableStateOf("") }
        var laborCostText by remember { mutableStateOf("") }
        var descText by remember { mutableStateOf("") }

        var expandedVeh by remember { mutableStateOf(false) }
        var expandedCat by remember { mutableStateOf(false) }

        val partsCost = partsCostText.toDoubleOrNull() ?: 0.0
        val laborCost = laborCostText.toDoubleOrNull() ?: 0.0
        val totalCost = partsCost + laborCost

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(LanguageManager.getString("add_maintenance"), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(LanguageManager.getString("date")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Vehicle Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedVeh,
                        onExpandedChange = { expandedVeh = !expandedVeh },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedVeh?.name ?: "گاڑی منتخب کریں",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(LanguageManager.getString("vehicle_name")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVeh) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedVeh,
                            onDismissRequest = { expandedVeh = false }
                        ) {
                            vehicles.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text(v.name) },
                                    onClick = {
                                        selectedVeh = v
                                        expandedVeh = false
                                    }
                                )
                            }
                        }
                    }

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = maintType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(LanguageManager.getString("maint_type")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        maintType = cat
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = partsCostText,
                        onValueChange = { partsCostText = it },
                        label = { Text(LanguageManager.getString("parts_cost") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = laborCostText,
                        onValueChange = { laborCostText = it },
                        label = { Text(LanguageManager.getString("labor_cost") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Total Cost: " + FinancialEngine.formatMoney(totalCost),
                        fontWeight = FontWeight.Bold,
                        color = Red500
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val entity = MaintenanceEntity(
                            userId = 0,
                            vehicleId = selectedVeh?.id ?: 1L,
                            date = date,
                            maintenanceType = maintType,
                            partsCost = partsCost,
                            laborCost = laborCost,
                            totalCost = totalCost,
                            description = descText
                        )
                        onAddMaintenance(entity)
                        showAddDialog = false
                    }
                ) {
                    Text(LanguageManager.getString("save"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(LanguageManager.getString("cancel"))
                }
            }
        )
    }
}
