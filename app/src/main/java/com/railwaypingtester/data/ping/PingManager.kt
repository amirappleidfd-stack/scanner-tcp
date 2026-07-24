package com.railwaypingtester.data.ping

import com.railwaypingtester.data.model.PingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object PingManager {

    suspend fun ping(host: String, count: Int = 4, timeoutMs: Int = 3000): PingResult = withContext(Dispatchers.IO) {
        try {
            val timeoutSec = (timeoutMs / 1000).coerceAtLeast(1)
            val process = Runtime.getRuntime().exec(
                arrayOf("/system/bin/ping", "-c", count.toString(), "-W", timeoutSec.toString(), host)
            )

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }

            while (errorReader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }

            val exitCode = process.waitFor()
            val fullOutput = output.toString()

            if (exitCode == 0 && fullOutput.contains("time=")) {
                val times = mutableListOf<Float>()
                val regex = Regex("""time=(\d+\.?\d*)""")
                regex.findAll(fullOutput).forEach { match ->
                    match.groupValues[1].toFloatOrNull()?.let { times.add(it) }
                }

                val avgTime = if (times.isNotEmpty()) times.average().toFloat() else 0f

                val lossRegex = Regex("""(\d+)% packet loss""")
                val lossMatch = lossRegex.find(fullOutput)
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
                    isTimedOut = true,
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
}