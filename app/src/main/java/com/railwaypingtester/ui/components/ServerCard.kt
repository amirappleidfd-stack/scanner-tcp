package com.railwaypingtester.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.railwaypingtester.data.model.ServerState
import com.railwaypingtester.data.model.ServerStatus
import com.railwaypingtester.ui.theme.*

@Composable
fun ServerCard(
    serverStatus: ServerStatus,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (serverStatus.status) {
            ServerState.ONLINE -> GreenOnline
            ServerState.OFFLINE -> RedOffline
            ServerState.TIMEOUT -> RedOffline
            ServerState.TESTING -> YellowTesting
            ServerState.IDLE -> DarkTextDisabled
        },
        animationSpec = tween(500),
        label = "statusColor"
    )

    val statusTextColor by animateColorAsState(
        targetValue = when (serverStatus.status) {
            ServerState.ONLINE -> GreenOnlineLight
            ServerState.OFFLINE -> RedOfflineLight
            ServerState.TIMEOUT -> RedOfflineLight
            ServerState.TESTING -> YellowTestingLight
            ServerState.IDLE -> DarkTextDisabled
        },
        animationSpec = tween(500),
        label = "statusTextColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            statusColor.copy(alpha = 0.15f),
                            DarkCardBackground
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(statusColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(statusColor)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Server info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = serverStatus.server.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = DarkTextPrimary,
                            fontFamily = FontFamily.Monospace
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = serverStatus.server.ip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = DarkTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    if (serverStatus.status == ServerState.ONLINE) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PingBar(latencyMs = serverStatus.averagePing)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${"%.0f".format(serverStatus.averagePing)} ms",
                                fontSize = 11.sp,
                                color = BlueAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Status badge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = when (serverStatus.status) {
                            ServerState.ONLINE -> "Online"
                            ServerState.OFFLINE -> "Offline"
                            ServerState.TIMEOUT -> "Timeout"
                            ServerState.TESTING -> "Testing..."
                            ServerState.IDLE -> "Idle"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = statusTextColor,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    if (serverStatus.status == ServerState.OFFLINE || serverStatus.status == ServerState.TIMEOUT) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "100% loss",
                            fontSize = 10.sp,
                            color = RedOffline.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PingBar(latencyMs: Float) {
    val barWidth = (latencyMs / 500f).coerceAtMost(1f)
    val barColor = when {
        latencyMs < 50 -> GreenOnline
        latencyMs < 150 -> YellowTesting
        else -> RedOffline
    }

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF2D333B))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = barWidth)
                .clip(RoundedCornerShape(3.dp))
                .background(barColor)
        )
    }
}