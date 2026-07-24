package com.railwaypingtester.data.model

data class PingResult(
    val latencyMs: Float = 0f,
    val isOnline: Boolean = false,
    val isTimedOut: Boolean = true,
    val packetLoss: Int = 0,
    val rawOutput: String = ""
)

data class Server(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val ip: String,
    val pingResults: List<PingResult> = emptyList(),
    val lastPing: PingResult? = null
)

data class ServerStatus(
    val server: Server,
    val status: ServerState = ServerState.IDLE,
    val averagePing: Float = 0f
)

enum class ServerState {
    IDLE,
    TESTING,
    ONLINE,
    OFFLINE,
    TIMEOUT
}

data class PingSettings(
    val timeoutMs: Int = 3000,
    val packetCount: Int = 4
)

data class ScanRecord(
    val timestamp: Long = System.currentTimeMillis(),
    val results: List<ServerStatus> = emptyList(),
    val averagePing: Float = 0f
)