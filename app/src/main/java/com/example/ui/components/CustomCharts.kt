package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary

@Composable
fun FinanceBarChart(
    totalIncome: Double,
    totalExpenses: Double,
    netProfit: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = LanguageManager.getString("nav_dashboard") + " - Visual Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            val maxVal = maxOf(totalIncome, totalExpenses, maxOf(0.0, netProfit), 1.0)

            // Income Bar
            ChartBarRow(
                label = LanguageManager.getString("total_income"),
                value = totalIncome,
                maxVal = maxVal,
                color = Emerald500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expenses Bar
            ChartBarRow(
                label = LanguageManager.getString("total_expenses"),
                value = totalExpenses,
                maxVal = maxVal,
                color = Red500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Net Profit Bar
            ChartBarRow(
                label = LanguageManager.getString("net_profit"),
                value = maxOf(0.0, netProfit),
                maxVal = maxVal,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ChartBarRow(
    label: String,
    value: Double,
    maxVal: Double,
    color: Color
) {
    val fraction = (value / maxVal).coerceIn(0.0, 1.0).toFloat()
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "bar")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = FinancialEngine.formatMoney(value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun VehicleProfitabilityChart(
    summaries: List<VehicleSummary>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = LanguageManager.getString("my_vehicles") + " - Profit Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (summaries.isEmpty()) {
                Text(
                    text = LanguageManager.getString("empty_vehicles"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            } else {
                val maxProfit = maxOf(summaries.maxOfOrNull { maxOf(0.0, it.netVehicleIncome) } ?: 1.0, 1.0)

                summaries.forEach { summary ->
                    val fraction = (maxOf(0.0, summary.netVehicleIncome) / maxProfit).coerceIn(0.0, 1.0).toFloat()
                    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "vBar")

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = summary.vehicle.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = FinancialEngine.formatMoney(summary.netVehicleIncome),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.netVehicleIncome >= 0) Emerald500 else Red500
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedFraction)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (summary.netVehicleIncome >= 0) Emerald500 else Red500)
                            )
                        }
                    }
                }
            }
        }
    }
}
