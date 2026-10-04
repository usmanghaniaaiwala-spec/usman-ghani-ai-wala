package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.entities.BudgetEntity
import com.example.data.entities.HomeExpenseEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    budgets: List<BudgetEntity>,
    homeExpenses: List<HomeExpenseEntity>,
    onSaveBudget: (category: String, amount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSetDialog by remember { mutableStateOf(false) }

    val defaultCategories = listOf(
        "Grocery / Ration", "Electricity Bill", "Gas Bill", "Fuel",
        "Maintenance", "Rent", "Medical Expenses", "School Fees", "Other Expenses"
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
                    text = LanguageManager.getString("monthly_budget"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { showSetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = LanguageManager.getString("set_budget"), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(defaultCategories) { category ->
                    val budgetObj = budgets.find { it.category == category }
                    val budgetAmt = budgetObj?.budgetAmount ?: 0.0
                    val actualSpent = homeExpenses.filter { it.category == category }.sumOf { it.amount }
                    val remaining = budgetAmt - actualSpent
                    val percentUsed = if (budgetAmt > 0) (actualSpent / budgetAmt) * 100.0 else 0.0

                    val statusText = when {
                        budgetAmt == 0.0 -> "سیٹ نہیں ہوا"
                        percentUsed > 100.0 -> LanguageManager.getString("status_over_budget")
                        percentUsed >= 80.0 -> LanguageManager.getString("status_near_limit")
                        else -> LanguageManager.getString("status_on_track")
                    }

                    val statusColor = when {
                        percentUsed > 100.0 -> Red500
                        percentUsed >= 80.0 -> Amber500
                        else -> Emerald500
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text(text = category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = statusText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = statusColor)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Budget: " + FinancialEngine.formatMoney(budgetAmt), style = MaterialTheme.typography.bodySmall)
                                Text(text = "Spent: " + FinancialEngine.formatMoney(actualSpent), style = MaterialTheme.typography.bodySmall)
                                Text(text = "Rem: " + FinancialEngine.formatMoney(remaining), style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = (percentUsed / 100.0).coerceIn(0.0, 1.0).toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = statusColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSetDialog) {
        var category by remember { mutableStateOf(defaultCategories.first()) }
        var amountText by remember { mutableStateOf("") }
        var expandedCat by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSetDialog = false },
            title = { Text(LanguageManager.getString("set_budget"), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = !expandedCat }) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("زمرہ (Category)") },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                            defaultCategories.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; expandedCat = false }) }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text(LanguageManager.getString("budget_amount") + " (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveBudget(category, amountText.toDoubleOrNull() ?: 0.0)
                    showSetDialog = false
                }) { Text(LanguageManager.getString("save")) }
            },
            dismissButton = { TextButton(onClick = { showSetDialog = false }) { Text(LanguageManager.getString("cancel")) } }
        )
    }
}
