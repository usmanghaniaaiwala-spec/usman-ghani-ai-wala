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
import androidx.compose.material.icons.filled.Home
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
import com.example.data.entities.HomeExpenseEntity
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Red500
import com.example.utils.FinancialEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    homeExpenses: List<HomeExpenseEntity>,
    onAddHomeExpense: (HomeExpenseEntity) -> Unit,
    onDeleteHomeExpense: (HomeExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val defaultCategories = listOf(
        "Grocery / Ration", "Electricity Bill", "Gas Bill", "Water Bill",
        "Internet Bill", "Mobile Bill", "School Fees", "Medical Expenses",
        "Rent", "Installments", "House Maintenance", "Transportation",
        "Clothes", "Children Expenses", "Kitchen Expenses", "Repairs", "Other Expenses"
    )

    val paymentMethods = listOf("Cash", "Bank", "JazzCash", "EasyPaisa", "Card", "Other")

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
                    text = LanguageManager.getString("home_expenses_title"),
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
                    Text(text = LanguageManager.getString("add_home_expense"), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (homeExpenses.isEmpty()) {
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
                            text = LanguageManager.getString("empty_expenses"),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(homeExpenses) { item ->
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
                                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home Expense", tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = item.category, fontWeight = FontWeight.Bold)
                                        Text(text = "${item.date} • ${item.paymentMethod} ${if (item.description.isNotBlank()) "• ${item.description}" else ""}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = FinancialEngine.formatMoney(item.amount), fontWeight = FontWeight.Bold, color = Red500)
                                    IconButton(onClick = { onDeleteHomeExpense(item) }) {
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
        var date by remember { mutableStateOf(todayStr) }
        var category by remember { mutableStateOf(defaultCategories.first()) }
        var amountText by remember { mutableStateOf("") }
        var paymentMethod by remember { mutableStateOf("Cash") }
        var descText by remember { mutableStateOf("") }

        var expandedCat by remember { mutableStateOf(false) }
        var expandedMethod by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(LanguageManager.getString("add_home_expense"), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(LanguageManager.getString("date")) }, modifier = Modifier.fillMaxWidth())

                    ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = !expandedCat }) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(LanguageManager.getString("expense_category")) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                            defaultCategories.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; expandedCat = false }) }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("رقم (Rs. Amount)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(expanded = expandedMethod, onExpandedChange = { expandedMethod = !expandedMethod }) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(LanguageManager.getString("payment_method")) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedMethod, onDismissRequest = { expandedMethod = false }) {
                            paymentMethods.forEach { method -> DropdownMenuItem(text = { Text(method) }, onClick = { paymentMethod = method; expandedMethod = false }) }
                        }
                    }

                    OutlinedTextField(value = descText, onValueChange = { descText = it }, label = { Text(LanguageManager.getString("notes")) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    onAddHomeExpense(
                        HomeExpenseEntity(
                            userId = 0,
                            category = category,
                            description = descText,
                            amount = amountText.toDoubleOrNull() ?: 0.0,
                            paymentMethod = paymentMethod,
                            date = date
                        )
                    )
                    showAddDialog = false
                }) { Text(LanguageManager.getString("save")) }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text(LanguageManager.getString("cancel")) } }
        )
    }
}
