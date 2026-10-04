package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.utils.LanguageManager
import com.example.ui.components.FinanceBarChart
import com.example.ui.components.VehicleProfitabilityChart
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.DashboardSummary
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary
import com.example.viewmodel.DateFilterType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    vehicleSummaries: List<VehicleSummary>,
    currentFilter: DateFilterType,
    onFilterSelected: (DateFilterType) -> Unit,
    onNavigateToQuickAdd: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onNavigateToReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val currentDateStr = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = LanguageManager.getString("greeting"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currentDateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = onNavigateToQuickAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("quick_add_dashboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick Add",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = LanguageManager.getString("quick_record_title"),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Filter Chips
        Text(
            text = LanguageManager.getString("filter") + " " + LanguageManager.getString("date"),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                DateFilterType.TODAY to LanguageManager.getString("filter_today"),
                DateFilterType.THIS_WEEK to LanguageManager.getString("filter_this_week"),
                DateFilterType.THIS_MONTH to LanguageManager.getString("filter_this_month"),
                DateFilterType.LAST_MONTH to LanguageManager.getString("filter_last_month"),
                DateFilterType.CUSTOM to LanguageManager.getString("filter_custom")
            )
            items(filters) { (filterType, label) ->
                FilterChip(
                    selected = currentFilter == filterType,
                    onClick = { onFilterSelected(filterType) },
                    label = { Text(label, fontWeight = if (currentFilter == filterType) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("filter_chip_${filterType.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 9 Main Financial Stat Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "💰 " + LanguageManager.getString("total_income"),
                    amount = summary.totalIncome,
                    color = Emerald500,
                    changePercent = summary.incomeChangePercent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "💸 " + LanguageManager.getString("total_expenses"),
                    amount = summary.totalExpenses,
                    color = Red500,
                    changePercent = summary.expenseChangePercent,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "💵 " + LanguageManager.getString("net_profit"),
                    amount = summary.netProfit,
                    color = if (summary.netProfit >= 0) Emerald500 else Red500,
                    changePercent = summary.profitChangePercent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "🏦 " + LanguageManager.getString("savings"),
                    amount = summary.savings,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "🏍️ " + LanguageManager.getString("vehicle_expenses"),
                    amount = summary.vehicleExpenses,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "🏠 " + LanguageManager.getString("home_expenses"),
                    amount = summary.homeExpenses,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallStatCard(
                    title = "🔧 " + LanguageManager.getString("maintenance_expenses"),
                    amount = summary.maintenanceExpenses,
                    modifier = Modifier.weight(1f)
                )
                SmallStatCard(
                    title = "⛽ " + LanguageManager.getString("fuel_expenses"),
                    amount = summary.fuelExpenses,
                    modifier = Modifier.weight(1f)
                )
                SmallStatCard(
                    title = "⚡ " + LanguageManager.getString("charging_expenses"),
                    amount = summary.chargingExpenses,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Finance Bar Chart
        FinanceBarChart(
            totalIncome = summary.totalIncome,
            totalExpenses = summary.totalExpenses,
            netProfit = summary.netProfit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Profitability Chart
        VehicleProfitabilityChart(summaries = vehicleSummaries)

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Overview Insights Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 Quick Financial Highlights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Top Earning Vehicle: ${summary.topEarningVehicleName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "• Top Home Expense Category: ${summary.topHomeExpenseCategory}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "• Profit Margin: ${FinancialEngine.formatNumber(summary.profitMarginPercent)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald500
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    amount: Double,
    color: Color,
    changePercent: Double? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = FinancialEngine.formatMoney(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (changePercent != null && changePercent != 0.0) {
                Spacer(modifier = Modifier.height(4.dp))
                val isPos = changePercent > 0
                Text(
                    text = "${if (isPos) "▲ +" else "▼ "}${FinancialEngine.formatNumber(changePercent)}% vs prev",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPos) Emerald500 else Red500
                )
            }
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = FinancialEngine.formatMoney(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
