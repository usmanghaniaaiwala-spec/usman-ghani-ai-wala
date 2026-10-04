package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary

@Composable
fun VehicleComparisonScreen(
    vehicleSummaries: List<VehicleSummary>,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = LanguageManager.getString("nav_comparison"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (vehicleSummaries.isEmpty()) {
                Text(
                    text = LanguageManager.getString("empty_vehicles"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                            .padding(16.dp)
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(12.dp)
                        ) {
                            Text("Vehicle", fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
                            Text("Gross Income", fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                            Text("Fuel/Charg", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Maintenance", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Other Cost", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Total Cost", fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                            Text("Net Income", fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                            Text("Margin %", fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                            Text("Cost/KM", fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        }

                        Divider()

                        // Rows
                        vehicleSummaries.forEach { summary ->
                            val fuelOrCharg = maxOf(summary.fuelCost, summary.chargingCost)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(summary.vehicle.name, fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
                                Text(FinancialEngine.formatMoney(summary.grossIncome), color = Emerald500, modifier = Modifier.width(110.dp))
                                Text(FinancialEngine.formatMoney(fuelOrCharg), modifier = Modifier.width(100.dp))
                                Text(FinancialEngine.formatMoney(summary.maintenanceCost), modifier = Modifier.width(100.dp))
                                Text(FinancialEngine.formatMoney(summary.otherExpenses), modifier = Modifier.width(100.dp))
                                Text(FinancialEngine.formatMoney(summary.totalVehicleCost), color = Red500, modifier = Modifier.width(100.dp))
                                Text(
                                    FinancialEngine.formatMoney(summary.netVehicleIncome),
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.netVehicleIncome >= 0) Emerald500 else Red500,
                                    modifier = Modifier.width(110.dp)
                                )
                                Text("${FinancialEngine.formatNumber(summary.profitMarginPercent)}%", modifier = Modifier.width(90.dp))
                                Text(FinancialEngine.formatMoney(summary.costPerKm), modifier = Modifier.width(90.dp))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
