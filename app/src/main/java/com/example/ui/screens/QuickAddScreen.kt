package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.example.data.entities.VehicleEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddScreen(
    vehicles: List<VehicleEntity>,
    onSaveRecord: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val todayDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    var selectedVehicle by remember { mutableStateOf(vehicles.firstOrNull()) }
    var expandedVehicleDropdown by remember { mutableStateOf(false) }

    var dateText by remember { mutableStateOf(todayDateStr) }
    var grossIncomeText by remember { mutableStateOf("") }
    var fuelCostText by remember { mutableStateOf("") }
    var chargingCostText by remember { mutableStateOf("") }
    var maintenanceCostText by remember { mutableStateOf("") }
    var otherVehicleExpenseText by remember { mutableStateOf("") }

    var homeExpenseText by remember { mutableStateOf("") }
    var homeExpenseCategory by remember { mutableStateOf("Grocery / Ration") }

    var otherIncomeText by remember { mutableStateOf("") }
    var otherIncomeSource by remember { mutableStateOf("Job Salary / Online") }

    var workingHoursText by remember { mutableStateOf("") }
    var kilometersText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    // Realtime Calculations
    val grossInc = grossIncomeText.toDoubleOrNull() ?: 0.0
    val fuelEx = fuelCostText.toDoubleOrNull() ?: 0.0
    val chargEx = chargingCostText.toDoubleOrNull() ?: 0.0
    val maintEx = maintenanceCostText.toDoubleOrNull() ?: 0.0
    val otherVehEx = otherVehicleExpenseText.toDoubleOrNull() ?: 0.0
    val homeEx = homeExpenseText.toDoubleOrNull() ?: 0.0
    val otherInc = otherIncomeText.toDoubleOrNull() ?: 0.0

    val totalIncomeCalc = grossInc + otherInc
    val totalExpenseCalc = fuelEx + chargEx + maintEx + otherVehEx + homeEx
    val netAmountCalc = totalIncomeCalc - totalExpenseCalc

    LaunchedEffect(vehicles) {
        if (selectedVehicle == null && vehicles.isNotEmpty()) {
            selectedVehicle = vehicles.first()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = LanguageManager.getString("quick_record_title"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = LanguageManager.getString("quick_record_desc"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Date Field
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text(LanguageManager.getString("date") + " (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_add_date_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Vehicle Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedVehicleDropdown,
                    onExpandedChange = { expandedVehicleDropdown = !expandedVehicleDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedVehicle?.name ?: "پہلے گاڑی منتخب کریں",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(LanguageManager.getString("vehicle_name")) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehicleDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVehicleDropdown,
                        onDismissRequest = { expandedVehicleDropdown = false }
                    ) {
                        vehicles.forEach { v ->
                            DropdownMenuItem(
                                text = { Text("${v.name} (${v.fuelType})") },
                                onClick = {
                                    selectedVehicle = v
                                    expandedVehicleDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "💰 " + LanguageManager.getString("nav_income") + " (Vehicle Income)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald500
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = grossIncomeText,
                    onValueChange = { grossIncomeText = it },
                    label = { Text(LanguageManager.getString("gross_income") + " (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_add_gross_income")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = workingHoursText,
                        onValueChange = { workingHoursText = it },
                        label = { Text(LanguageManager.getString("working_hours")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = kilometersText,
                        onValueChange = { kilometersText = it },
                        label = { Text(LanguageManager.getString("kilometers")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🏍️ " + LanguageManager.getString("vehicle_expenses"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Red500
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedVehicle?.fuelType == "Electric") {
                    OutlinedTextField(
                        value = chargingCostText,
                        onValueChange = { chargingCostText = it },
                        label = { Text(LanguageManager.getString("charging_expenses") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = fuelCostText,
                        onValueChange = { fuelCostText = it },
                        label = { Text(LanguageManager.getString("fuel_expenses") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = maintenanceCostText,
                        onValueChange = { maintenanceCostText = it },
                        label = { Text(LanguageManager.getString("maintenance_expenses") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = otherVehicleExpenseText,
                        onValueChange = { otherVehicleExpenseText = it },
                        label = { Text(LanguageManager.getString("other_vehicle_expense") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🏠 " + LanguageManager.getString("home_expenses") + " & " + LanguageManager.getString("other_income"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = homeExpenseText,
                        onValueChange = { homeExpenseText = it },
                        label = { Text(LanguageManager.getString("home_expenses") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = otherIncomeText,
                        onValueChange = { otherIncomeText = it },
                        label = { Text(LanguageManager.getString("other_income") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text(LanguageManager.getString("notes")) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Real-time Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = LanguageManager.getString("net_calculated"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Income: " + FinancialEngine.formatMoney(totalIncomeCalc),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Total Expense: " + FinancialEngine.formatMoney(totalExpenseCalc),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NET AMOUNT = " + FinancialEngine.formatMoney(netAmountCalc),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (netAmountCalc >= 0) Emerald500 else Red500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val vehId = selectedVehicle?.id ?: 1L
                        onSaveRecord(
                            vehId,
                            dateText,
                            grossInc,
                            fuelEx,
                            chargEx,
                            maintEx,
                            otherVehEx,
                            homeEx,
                            homeExpenseCategory,
                            otherInc,
                            otherIncomeSource,
                            workingHoursText.toDoubleOrNull() ?: 0.0,
                            kilometersText.toDoubleOrNull() ?: 0.0,
                            notesText
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quick_add_save_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageManager.getString("save_record"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
