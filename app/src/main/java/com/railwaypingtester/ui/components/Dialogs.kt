package com.railwaypingtester.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.railwaypingtester.R
import com.railwaypingtester.data.model.ScanRecord
import com.railwaypingtester.ui.theme.*

@Composable
fun AddServerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, ip: String) -> Unit
) {
    if (!show) return

    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        titleContentColor = DarkTextPrimary,
        textContentColor = DarkTextSecondary,
        title = {
            Text(
                "Add Server",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Domain / Hostname", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("example.proxy.rlwy.net", fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = BlueAccent,
                        focusedLabelColor = BlueAccent,
                        unfocusedLabelColor = DarkTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it; error = null },
                    label = { Text("IP Address", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("66.33.22.240", fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = BlueAccent,
                        focusedLabelColor = BlueAccent,
                        unfocusedLabelColor = DarkTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = RedOffline,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Hostname is required"
                        return@Button
                    }
                    if (ip.isBlank()) {
                        error = "IP address is required"
                        return@Button
                    }
                    onConfirm(name.trim(), ip.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    "Add Server",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = DarkTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}

@Composable
fun SettingsDialog(
    show: Boolean,
    currentTimeoutMs: Int,
    currentPacketCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (timeoutMs: Int, packetCount: Int) -> Unit
) {
    if (!show) return

    var timeout by remember { mutableStateOf(currentTimeoutMs.toString()) }
    var count by remember { mutableStateOf(currentPacketCount.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        titleContentColor = DarkTextPrimary,
        textContentColor = DarkTextSecondary,
        title = {
            Text(
                "Ping Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = timeout,
                    onValueChange = { timeout = it; error = null },
                    label = { Text("Timeout (ms)", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("3000") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = BlueAccent,
                        focusedLabelColor = BlueAccent,
                        unfocusedLabelColor = DarkTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = count,
                    onValueChange = { count = it; error = null },
                    label = { Text("Packet Count", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("4") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DarkTextPrimary,
                        unfocusedTextColor = DarkTextPrimary,
                        focusedBorderColor = BlueAccent,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = BlueAccent,
                        focusedLabelColor = BlueAccent,
                        unfocusedLabelColor = DarkTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = RedOffline,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val t = timeout.toIntOrNull()
                    val c = count.toIntOrNull()
                    if (t == null || t < 500 || t > 30000) {
                        error = "Timeout must be 500-30000"
                        return@Button
                    }
                    if (c == null || c < 1 || c > 20) {
                        error = "Count must be 1-20"
                        return@Button
                    }
                    onConfirm(t, c)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    "Save",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = DarkTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}

@Composable
fun HistoryDialog(
    show: Boolean,
    records: List<ScanRecord>,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        titleContentColor = DarkTextPrimary,
        textContentColor = DarkTextSecondary,
        title = {
            Text(
                "Scan History",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .animateContentSize()
            ) {
                if (records.isEmpty()) {
                    Text(
                        "No scan history yet.",
                        color = DarkTextDisabled,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                } else {
                    records.take(20).forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = java.text.SimpleDateFormat(
                                        "MM-dd HH:mm", java.util.Locale.getDefault()
                                    ).format(java.util.Date(record.timestamp)),
                                    fontSize = 12.sp,
                                    color = DarkTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${record.results.size} servers",
                                    fontSize = 11.sp,
                                    color = DarkTextDisabled,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "Avg: ${"%.0f".format(record.averagePing)} ms",
                                fontSize = 12.sp,
                                color = BlueAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                    }
                }
            }
        },
        confirmButton = {
            if (records.isNotEmpty()) {
                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(containerColor = RedOffline),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        "Clear",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = DarkTextSecondary, fontFamily = FontFamily.Monospace)
            }
        }
    )
}

@Composable
fun ContactDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onOpenLink: (String) -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Contact Me",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ── Telegram SPiDER ──
                ContactRow(
                    iconRes = R.drawable.ic_telegram,
                    label = "Telegram — SPiDER",
                    url = "https://t.me/SPiDER_VPN1",
                    onClick = { onOpenLink("https://t.me/SPiDER_VPN1") }
                )
                // ── Telegram g1ithub ──
                ContactRow(
                    iconRes = R.drawable.ic_telegram,
                    label = "Telegram — g1ithub",
                    url = "https://t.me/g1ithub",
                    onClick = { onOpenLink("https://t.me/g1ithub") }
                )
                // ── GitHub ──
                ContactRow(
                    iconRes = R.drawable.ic_github,
                    label = "GitHub — amirappleidfd-stack",
                    url = "https://github.com/amirappleidfd-stack",
                    onClick = { onOpenLink("https://github.com/amirappleidfd-stack") }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = DarkTextSecondary, fontFamily = FontFamily.Monospace)
            }
        }
    )
}

@Composable
private fun ContactRow(
    iconRes: Int,
    label: String,
    url: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = DarkCardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkBorder),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(26.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = url,
                    fontSize = 11.sp,
                    color = BlueAccent,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
