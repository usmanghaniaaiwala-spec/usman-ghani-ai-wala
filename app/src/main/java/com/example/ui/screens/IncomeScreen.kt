package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entities.VehicleEntity
import com.example.data.entities.VehicleIncomeEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine

@Composable
fun IncomeScreen(
    vehicles: List<VehicleEntity>,
    vehicleIncomes: List<VehicleIncomeEntity>,
    onDeleteIncome: (VehicleIncomeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = LanguageManager.getString("nav_income"),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (vehicleIncomes.isEmpty()) {
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
                            text = LanguageManager.getString("empty_income"),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(vehicleIncomes) { item ->
                        val vehicleName = vehicles.find { it.id == item.vehicleId }?.name ?: "Vehicle #${item.vehicleId}"
                        val totalExp = item.fuelCost + item.chargingCost + item.maintenanceCost + item.otherExpense
                        val netInc = item.grossIncome - totalExp

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
                                    Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Income", tint = Emerald500)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = vehicleName, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${item.date} • Gross: Rs. ${item.grossIncome} • Cost: Rs. $totalExp",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = FinancialEngine.formatMoney(netInc),
                                            fontWeight = FontWeight.Bold,
                                            color = if (netInc >= 0) Emerald500 else Red500
                                        )
                                        Text(text = "Net Income", style = MaterialTheme.typography.labelSmall)
                                    }
                                    IconButton(onClick = { onDeleteIncome(item) }) {
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
}
