package com.railwaypingtester.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railwaypingtester.data.model.PingResult
import com.railwaypingtester.data.model.PingSettings
import com.railwaypingtester.data.model.ScanRecord
import com.railwaypingtester.data.model.Server
import com.railwaypingtester.data.model.ServerState
import com.railwaypingtester.data.model.ServerStatus
import com.railwaypingtester.data.local.ServerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ServerRepository
) : ViewModel() {

    private val _servers = MutableStateFlow<List<ServerStatus>>(emptyList())
    val servers: StateFlow<List<ServerStatus>> = _servers

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _settings = MutableStateFlow(PingSettings())
    val settings: StateFlow<PingSettings> = _settings

    private val _onlineCount = MutableStateFlow(0)
    val onlineCount: StateFlow<Int> = _onlineCount

    private val _offlineCount = MutableStateFlow(0)
    val offlineCount: StateFlow<Int> = _offlineCount

    private val _bestServer = MutableStateFlow<ServerStatus?>(null)
    val bestServer: StateFlow<ServerStatus?> = _bestServer

    private val _scanHistory = MutableStateFlow<List<ScanRecord>>(emptyList())
    val scanHistory: StateFlow<List<ScanRecord>> = _scanHistory

    private var scanJob: Job? = null

    init {
        loadServers()
        loadSettings()
        loadHistory()
    }

    private fun loadServers() {
        viewModelScope.launch(Dispatchers.IO) {
            val servers = repository.loadServers()
            _servers.value = servers.map { ServerStatus(server = it) }
            updateCounts()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val timeout = repository.loadPingTimeout()
            val count = repository.loadPingCount()
            _settings.value = PingSettings(timeout, count)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _scanHistory.value = repository.loadScanHistory()
        }
    }

    private fun updateCounts() {
        val currentServers = _servers.value
        _onlineCount.value = currentServers.count { it.status == ServerState.ONLINE }
        _offlineCount.value = currentServers.count { 
            it.status == ServerState.OFFLINE || it.status == ServerState.TIMEOUT 
        }
        _bestServer.value = currentServers
            .filter { it.status == ServerState.ONLINE && it.averagePing > 0 }
            .minByOrNull { it.averagePing }
    }

    fun startScan() {
        if (_isScanning.value) return
        _isScanning.value = true

        val settings = _settings.value
        val initialServers = _servers.value

        _servers.value = initialServers.map { it.copy(status = ServerState.IDLE, averagePing = 0f) }
        updateCounts()

        scanJob = viewModelScope.launch {
            val results = mutableListOf<ServerStatus>()
            val servers = initialServers

            for (i in servers.indices) {
                if (!_isScanning.value) break

                val testingStatus = servers[i].copy(status = ServerState.TESTING)
                val before = results.take(i)
                val after = servers.drop(i + 1).map { it.copy(status = ServerState.IDLE) }
                _servers.value = before + testingStatus + after

                val pingResult = performPing(servers[i].server.ip, settings)

                val finalStatus = if (pingResult.isOnline) {
                    ServerStatus(
                        server = servers[i].server.copy(lastPing = pingResult),
                        status = ServerState.ONLINE,
                        averagePing = pingResult.latencyMs
                    )
                } else {
                    ServerStatus(
                        server = servers[i].server.copy(lastPing = pingResult),
                        status = if (pingResult.isTimedOut) ServerState.TIMEOUT else ServerState.OFFLINE,
                        averagePing = 0f
                    )
                }

                results.add(finalStatus)
                val remaining = servers.drop(i + 1).map { it.copy(status = ServerState.IDLE) }
                _servers.value = results + remaining
                updateCounts()
            }

            val sortedResults = results.sortedWith(
                compareByDescending<ServerStatus> { it.status == ServerState.ONLINE }
                    .thenBy { if (it.status == ServerState.ONLINE) it.averagePing else Float.MAX_VALUE }
            )
            _servers.value = sortedResults
            updateCounts()

            val record = ScanRecord(
                results = sortedResults,
                averagePing = sortedResults
                    .filter { it.status == ServerState.ONLINE }
                    .map { it.averagePing }
                    .let { if (it.isNotEmpty()) it.average().toFloat() else 0f }
            )
            repository.saveScanRecord(record)
            _scanHistory.value = listOf(record) + _scanHistory.value
            repository.saveServers(sortedResults.map { it.server })
            _isScanning.value = false
        }
    }

    private suspend fun performPing(ip: String, settings: PingSettings): PingResult {
        return try {
            val timeoutSec = (settings.timeoutMs / 1000).coerceAtLeast(1)
            val process = Runtime.getRuntime().exec(
                arrayOf("/system/bin/ping", "-c", settings.packetCount.toString(), "-W", timeoutSec.toString(), ip)
            )
            val output = process.inputStream.bufferedReader().readText()
            val errorOutput = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            val fullOutput = "$output\n$errorOutput"

            if (exitCode == 0 && fullOutput.contains("time=")) {
                val times = mutableListOf<Float>()
                Regex("""time=(\d+\.?\d*)""").findAll(fullOutput).forEach { match ->
                    match.groupValues[1].toFloatOrNull()?.let { times.add(it) }
                }
                val avgTime = if (times.isNotEmpty()) times.average().toFloat() else 0f
                val lossMatch = Regex("""(\d+)% packet loss""").find(fullOutput)
                val packetLoss = lossMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
                PingResult(
                    latencyMs = avgTime,
                    isOnline = packetLoss < 100,
                    isTimedOut = false,
                    packetLoss = packetLoss,
                    rawOutput = fullOutput.trim()
                )
            } else {
                PingResult(
                    latencyMs = 0f,
                    isOnline = false,
                    isTimedOut = fullOutput.contains("100% packet loss") || exitCode != 0,
                    packetLoss = 100,
                    rawOutput = fullOutput.trim()
                )
            }
        } catch (e: Exception) {
            PingResult(
                latencyMs = 0f,
                isOnline = false,
                isTimedOut = true,
                packetLoss = 100,
                rawOutput = "Error: ${e.message}"
            )
        }
    }

    fun stopScan() {
        _isScanning.value = false
        scanJob?.cancel()
    }

    fun addServer(name: String, ip: String) {
        repository.addServer(Server(name = name, ip = ip))
        loadServers()
    }

    fun updateSettings(timeoutMs: Int, packetCount: Int) {
        _settings.value = PingSettings(timeoutMs, packetCount)
        repository.savePingSettings(timeoutMs, packetCount)
    }

    fun exportResults(): String {
        val sb = StringBuilder()
        val servers = _servers.value
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("  Railway Proxy Ping Test Results")
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine("Date: ${dateFormat.format(Date())}")
        sb.appendLine("Online: ${servers.count { it.status == ServerState.ONLINE }}")
        sb.appendLine("Offline: ${servers.count { it.status == ServerState.OFFLINE || it.status == ServerState.TIMEOUT }}")
        sb.appendLine("═══════════════════════════════════════")
        sb.appendLine()
        servers.forEach { status ->
            val state = when (status.status) {
                ServerState.ONLINE -> "ONLINE"
                ServerState.OFFLINE -> "OFFLINE"
                ServerState.TIMEOUT -> "TIMEOUT"
                ServerState.TESTING -> "TESTING"
                ServerState.IDLE -> "IDLE"
            }
            sb.appendLine("[$state]")
            sb.appendLine(status.server.name)
            sb.appendLine(status.server.ip)
            sb.appendLine("Ping: ${if (status.status == ServerState.ONLINE) "${"%.0f".format(status.averagePing)} ms" else "Timeout"}")
            sb.appendLine("───────────────────────────")
            sb.appendLine()
        }
        return sb.toString()
    }

    fun clearHistory() {
        repository.clearHistory()
        _scanHistory.value = emptyList()
    }
}