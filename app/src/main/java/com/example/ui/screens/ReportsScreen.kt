package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.DashboardSummary
import com.example.utils.ExporterImporter
import com.example.utils.FinancialEngine
import com.example.utils.VehicleSummary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(
    summary: DashboardSummary,
    vehicleSummaries: List<VehicleSummary>,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val currentMonthYear = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }

    var reportTextPreview by remember { mutableStateOf("") }

    LaunchedEffect(summary, vehicleSummaries) {
        val sb = StringBuilder()
        sb.append("=== HISABPRO FINANCIAL REPORT ($currentMonthYear) ===\n\n")
        sb.append("Total Income: ${FinancialEngine.formatMoney(summary.totalIncome)}\n")
        sb.append("  • Vehicle Income: ${FinancialEngine.formatMoney(summary.totalIncome - summary.homeExpenses)}\n")
        sb.append("Total Expenses: ${FinancialEngine.formatMoney(summary.totalExpenses)}\n")
        sb.append("  • Vehicle Expenses: ${FinancialEngine.formatMoney(summary.vehicleExpenses)}\n")
        sb.append("  • Home Expenses: ${FinancialEngine.formatMoney(summary.homeExpenses)}\n")
        sb.append("NET PROFIT: ${FinancialEngine.formatMoney(summary.netProfit)}\n")
        sb.append("SAVINGS: ${FinancialEngine.formatMoney(summary.savings)}\n")
        sb.append("Profit Margin: ${FinancialEngine.formatNumber(summary.profitMarginPercent)}%\n\n")

        sb.append("--- VEHICLE SUMMARY ---\n")
        vehicleSummaries.forEach { v ->
            sb.append("${v.vehicle.name}: Gross ${FinancialEngine.formatMoney(v.grossIncome)} | Cost ${FinancialEngine.formatMoney(v.totalVehicleCost)} | Net ${FinancialEngine.formatMoney(v.netVehicleIncome)}\n")
        }
        reportTextPreview = sb.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = LanguageManager.getString("nav_reports") + " ($currentMonthYear)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Export Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("HisabPro Report", reportTextPreview)
                    clipboard.setPrimaryClip(clip)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("copy_report_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Report", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onExportCsv,
                modifier = Modifier
                    .weight(1f)
                    .testTag("export_csv_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "CSV")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export CSV", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Financial Report Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "📄 Report Summary Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = reportTextPreview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
