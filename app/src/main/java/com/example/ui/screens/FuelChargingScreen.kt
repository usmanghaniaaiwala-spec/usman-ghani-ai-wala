package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocalGasStation
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
import com.example.data.entities.ChargingRecordEntity
import com.example.data.entities.FuelRecordEntity
import com.example.data.entities.VehicleEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Amber500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelChargingScreen(
    vehicles: List<VehicleEntity>,
    fuels: List<FuelRecordEntity>,
    chargings: List<ChargingRecordEntity>,
    onAddFuel: (FuelRecordEntity) -> Unit,
    onAddCharging: (ChargingRecordEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Petrol Fuel, 1 = Electric Charging
    var showAddFuelDialog by remember { mutableStateOf(false) }
    var showAddChargingDialog by remember { mutableStateOf(false) }

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
                    text = LanguageManager.getString("nav_fuel_charging"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = {
                        if (selectedTab == 0) showAddFuelDialog = true else showAddChargingDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (selectedTab == 0) "+ پٹرول شامل کریں" else "+ چارجنگ شامل کریں")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("⛽ " + LanguageManager.getString("fuel_expenses"), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("⚡ " + LanguageManager.getString("charging_expenses"), fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Petrol Records List
                if (fuels.isEmpty()) {
                    Text(
                        text = "پٹرول کا کوئی ریکارڈ نہیں ہے",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(fuels) { item ->
                            val vName = vehicles.find { it.id == item.vehicleId }?.name ?: "Vehicle #${item.vehicleId}"
                            val costPerKm = if (item.kilometers > 0) item.totalCost / item.kilometers else 0.0
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = Amber500)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "$vName (${item.litres} Litres @ Rs. ${item.pricePerLitre}/L)", fontWeight = FontWeight.Bold)
                                            Text(text = "${item.date} • ${FinancialEngine.formatKm(item.kilometers)} • Cost/KM: Rs. ${FinancialEngine.formatNumber(costPerKm)}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Text(text = FinancialEngine.formatMoney(item.totalCost), fontWeight = FontWeight.Bold, color = Red500)
                                }
                            }
                        }
                    }
                }
            } else {
                // Electric Charging Records List
                if (chargings.isEmpty()) {
                    Text(
                        text = "چارجنگ کا کوئی ریکارڈ نہیں ہے",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(chargings) { item ->
                            val vName = vehicles.find { it.id == item.vehicleId }?.name ?: "Vehicle #${item.vehicleId}"
                            val costPerKm = if (item.kilometers > 0) item.chargingCost / item.kilometers else 0.0
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.EvStation, contentDescription = "Charging", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "$vName (${item.unitsKwh} Units @ Rs. ${item.electricityRate}/Unit)", fontWeight = FontWeight.Bold)
                                            Text(text = "${item.date} • ${FinancialEngine.formatKm(item.kilometers)} • Cost/KM: Rs. ${FinancialEngine.formatNumber(costPerKm)}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Text(text = FinancialEngine.formatMoney(item.chargingCost), fontWeight = FontWeight.Bold, color = Red500)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Petrol Dialog
    if (showAddFuelDialog) {
        val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var selectedVeh by remember { mutableStateOf(vehicles.firstOrNull()) }
        var date by remember { mutableStateOf(todayStr) }
        var litresText by remember { mutableStateOf("") }
        var priceText by remember { mutableStateOf("") }
        var kmText by remember { mutableStateOf("") }
        var expandedVeh by remember { mutableStateOf(false) }

        val l = litresText.toDoubleOrNull() ?: 0.0
        val p = priceText.toDoubleOrNull() ?: 0.0
        val totalCost = l * p

        AlertDialog(
            onDismissRequest = { showAddFuelDialog = false },
            title = { Text("پٹرول کا اندراج", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("تاریخ") }, modifier = Modifier.fillMaxWidth())

                    ExposedDropdownMenuBox(expanded = expandedVeh, onExpandedChange = { expandedVeh = !expandedVeh }) {
                        OutlinedTextField(
                            value = selectedVeh?.name ?: "گاڑی چنیں",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("گاڑی") },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedVeh, onDismissRequest = { expandedVeh = false }) {
                            vehicles.forEach { v -> DropdownMenuItem(text = { Text(v.name) }, onClick = { selectedVeh = v; expandedVeh = false }) }
                        }
                    }

                    OutlinedTextField(value = litresText, onValueChange = { litresText = it }, label = { Text("لیٹر (Litres)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("فی لیٹر قیمت (Rs.)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = kmText, onValueChange = { kmText = it }, label = { Text("کلومیٹر (Driven KM)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    Text(text = "Total Petrol Cost: " + FinancialEngine.formatMoney(totalCost), fontWeight = FontWeight.Bold, color = Red500)
                }
            },
            confirmButton = {
                Button(onClick = {
                    onAddFuel(
                        FuelRecordEntity(
                            userId = 0,
                            vehicleId = selectedVeh?.id ?: 1L,
                            date = date,
                            litres = l,
                            pricePerLitre = p,
                            totalCost = totalCost,
                            kilometers = kmText.toDoubleOrNull() ?: 0.0
                        )
                    )
                    showAddFuelDialog = false
                }) { Text("محفوظ کریں") }
            },
            dismissButton = { TextButton(onClick = { showAddFuelDialog = false }) { Text("منسوخ") } }
        )
    }

    // Electric Charging Dialog
    if (showAddChargingDialog) {
        val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var selectedVeh by remember { mutableStateOf(vehicles.firstOrNull()) }
        var date by remember { mutableStateOf(todayStr) }
        var unitsText by remember { mutableStateOf("") }
        var rateText by remember { mutableStateOf("") }
        var kmText by remember { mutableStateOf("") }
        var expandedVeh by remember { mutableStateOf(false) }

        val u = unitsText.toDoubleOrNull() ?: 0.0
        val r = rateText.toDoubleOrNull() ?: 0.0
        val totalCost = u * r

        AlertDialog(
            onDismissRequest = { showAddChargingDialog = false },
            title = { Text("الیکٹرک چارجنگ کا اندراج", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("تاریخ") }, modifier = Modifier.fillMaxWidth())

                    ExposedDropdownMenuBox(expanded = expandedVeh, onExpandedChange = { expandedVeh = !expandedVeh }) {
                        OutlinedTextField(
                            value = selectedVeh?.name ?: "گاڑی چنیں",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("گاڑی") },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedVeh, onDismissRequest = { expandedVeh = false }) {
                            vehicles.forEach { v -> DropdownMenuItem(text = { Text(v.name) }, onClick = { selectedVeh = v; expandedVeh = false }) }
                        }
                    }

                    OutlinedTextField(value = unitsText, onValueChange = { unitsText = it }, label = { Text("یونٹ / kWh") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rateText, onValueChange = { rateText = it }, label = { Text("فی یونٹ بجلی کا ریٹ (Rs.)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = kmText, onValueChange = { kmText = it }, label = { Text("کلومیٹر (Driven KM)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    Text(text = "Total Charging Cost: " + FinancialEngine.formatMoney(totalCost), fontWeight = FontWeight.Bold, color = Red500)
                }
            },
            confirmButton = {
                Button(onClick = {
                    onAddCharging(
                        ChargingRecordEntity(
                            userId = 0,
                            vehicleId = selectedVeh?.id ?: 1L,
                            date = date,
                            unitsKwh = u,
                            electricityRate = r,
                            chargingCost = totalCost,
                            kilometers = kmText.toDoubleOrNull() ?: 0.0
                        )
                    )
                    showAddChargingDialog = false
                }) { Text("محفوظ کریں") }
            },
            dismissButton = { TextButton(onClick = { showAddChargingDialog = false }) { Text("منسوخ") } }
        )
    }
}
