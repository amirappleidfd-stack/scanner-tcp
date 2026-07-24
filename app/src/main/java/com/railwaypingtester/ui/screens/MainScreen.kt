package com.railwaypingtester.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.railwaypingtester.data.model.ServerState
import com.railwaypingtester.data.model.ServerStatus
import com.railwaypingtester.ui.components.*
import com.railwaypingtester.ui.theme.*
import com.railwaypingtester.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val servers by viewModel.servers.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val onlineCount by viewModel.onlineCount.collectAsState()
    val offlineCount by viewModel.offlineCount.collectAsState()
    val bestServer by viewModel.bestServer.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val scanHistory by viewModel.scanHistory.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var exportText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Railway Proxy Tester",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DarkTextSecondary)
                    }
                    IconButton(onClick = { showHistoryDialog = true }) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = DarkTextSecondary)
                    }
                    IconButton(onClick = { showContactDialog = true }) {
                        Icon(Icons.Default.Person, contentDescription = "Contact", tint = DarkTextSecondary)
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Server", tint = DarkTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface.copy(alpha = 0.95f),
                    titleContentColor = DarkTextPrimary
                )
            )
        },
        floatingActionButton = {
            if (!isScanning && servers.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { exportText = viewModel.exportResults(); showExportDialog = true },
                    containerColor = PurpleGradientStart,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Export",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                StatsHeader(
                    onlineCount = onlineCount,
                    offlineCount = offlineCount,
                    bestServer = bestServer,
                    isScanning = isScanning
                )

                Spacer(modifier = Modifier.height(16.dp))

                bestServer?.let {
                    BestServerCard(server = it)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                ActionButtons(
                    isScanning = isScanning,
                    onStart = { viewModel.startScan() },
                    onStop = { viewModel.stopScan() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ServerList(servers = servers)

                Spacer(modifier = Modifier.height(100.dp))
            }

            AddServerDialog(
                show = showAddDialog,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, ip -> viewModel.addServer(name, ip) }
            )

            SettingsDialog(
                show = showSettingsDialog,
                currentTimeoutMs = settings.timeoutMs,
                currentPacketCount = settings.packetCount,
                onDismiss = { showSettingsDialog = false },
                onConfirm = { timeout, count -> viewModel.updateSettings(timeout, count) }
            )

            HistoryDialog(
                show = showHistoryDialog,
                records = scanHistory,
                onDismiss = { showHistoryDialog = false },
                onClear = { viewModel.clearHistory() }
            )

            ExportDialog(
                show = showExportDialog,
                text = exportText,
                onDismiss = { showExportDialog = false }
            )

            ContactDialog(
                show = showContactDialog,
                onDismiss = { showContactDialog = false },
                onOpenLink = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun StatsHeader(
    onlineCount: Int,
    offlineCount: Int,
    bestServer: ServerStatus?,
    isScanning: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Online",
                fontSize = 11.sp,
                color = DarkTextDisabled,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$onlineCount",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = GreenOnlineLight,
                fontFamily = FontFamily.Monospace
            )
        }

        VerticalDivider(color = DarkBorder, thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Offline",
                fontSize = 11.sp,
                color = DarkTextDisabled,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$offlineCount",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = RedOfflineLight,
                fontFamily = FontFamily.Monospace
            )
        }

        VerticalDivider(color = DarkBorder, thickness = 1.dp)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isScanning) {
                ScanningText()
            } else if (bestServer != null) {
                Text(
                    "Best",
                    fontSize = 11.sp,
                    color = DarkTextDisabled,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${"%.0f".format(bestServer.averagePing)} ms",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BlueAccent,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    "No Data",
                    fontSize = 11.sp,
                    color = DarkTextDisabled,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "--",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DarkTextDisabled,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun BestServerCard(server: ServerStatus) {
    val gradientStart = GreenOnline.copy(alpha = 0.3f)
    val gradientEnd = BlueAccent.copy(alpha = 0.2f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(12.dp)
                        .background(GreenOnline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Best Server",
                        fontSize = 12.sp,
                        color = DarkTextDisabled,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        server.server.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        server.server.ip,
                        fontSize = 12.sp,
                        color = DarkTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${"%.0f".format(server.averagePing)} ms",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = GreenOnlineLight,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "lowest latency",
                        fontSize = 11.sp,
                        color = DarkTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    isScanning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    if (isScanning) {
        OutlinedButton(
            onClick = onStop,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = RedOfflineLight
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulseAnimation(isActive = true, color = RedOffline)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "STOP SCAN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    } else {
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueAccent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Start"
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "START SCAN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ServerList(servers: List<ServerStatus>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        servers.forEach { server ->
            ServerCard(serverStatus = server)
        }
    }
}

@Composable
fun ExportDialog(
    show: Boolean,
    text: String,
    onDismiss: () -> Unit
) {
    if (!show) return

    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),

        title = {
            Text(
                text = "Export Results",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {


                BasicTextField(
                    value = text,
                    onValueChange = {},
                    readOnly = true,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 350.dp)
                        .padding(12.dp)
                        .background(
                            DarkBackground,
                            RoundedCornerShape(12.dp)
                        ),

                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkTextPrimary
                    )
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {


                    TextButton(
                        onClick = {

                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager


                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Ping Results",
                                    text
                                )
                            )


                            copied = true
                        }
                    ) {

                        Text(
                            text =
                                if (copied)
                                    "Copied!"
                                else
                                    "Copy to Clipboard",

                            color =
                                if (copied)
                                    GreenOnline
                                else
                                    BlueAccent,

                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        },


        confirmButton = {},


        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    "Close",
                    color = DarkTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    )
}
