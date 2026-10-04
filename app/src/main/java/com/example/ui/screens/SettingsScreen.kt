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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.utils.AppLanguage
import com.example.data.utils.LanguageManager
import com.example.ui.theme.Red500

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    isPinLocked: Boolean,
    onToggleLanguage: () -> Unit,
    onTogglePinLock: (Boolean) -> Unit,
    onBackupJson: () -> String,
    onDeleteAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = LanguageManager.getString("nav_more"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Option
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                    Icon(imageVector = Icons.Default.Language, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Language / زبان", fontWeight = FontWeight.Bold)
                        Text(text = if (currentLanguage == AppLanguage.URDU) "اردو (Urdu)" else "English", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Button(
                    onClick = onToggleLanguage,
                    modifier = Modifier.testTag("toggle_language_btn")
                ) {
                    Text(text = if (currentLanguage == AppLanguage.URDU) "English" else "اردو")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Security / PIN Lock
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = LanguageManager.getString("pin_lock"), fontWeight = FontWeight.Bold)
                        Text(text = if (isPinLocked) "Active (1234)" else "Disabled", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Switch(
                    checked = isPinLocked,
                    onCheckedChange = { onTogglePinLock(it) },
                    modifier = Modifier.testTag("pin_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Backup & Restore Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "💾 Backup & Data Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val json = onBackupJson()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("HisabPro Backup JSON", json)
                        clipboard.setPrimaryClip(clip)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("backup_json_btn")
                ) {
                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Backup")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(LanguageManager.getString("backup_db"))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Danger Zone: Delete Data
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚠️ Danger Zone",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delete_all_data_btn")
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Delete", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(LanguageManager.getString("delete_all_data"), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("⚠️ " + LanguageManager.getString("delete_all_data"), fontWeight = FontWeight.Bold) },
            text = { Text(LanguageManager.getString("confirm_delete_msg")) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllData()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500)
                ) {
                    Text("ہاں، تمام ڈیٹا صاف کریں", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(LanguageManager.getString("cancel"))
                }
            }
        )
    }
}
