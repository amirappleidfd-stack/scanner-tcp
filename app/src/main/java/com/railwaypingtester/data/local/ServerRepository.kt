package com.railwaypingtester.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.railwaypingtester.data.model.ScanRecord
import com.railwaypingtester.data.model.Server

class ServerRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("railway_ping", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SERVERS = "servers_list"
        private const val KEY_HISTORY = "scan_history"
        private const val KEY_SETTINGS = "ping_settings"

        fun getDefaultServers(): List<Server> = listOf(
            Server(name = "shuttle.proxy.rlwy.net", ip = "66.33.22.240"),
            Server(name = "monorail.proxy.rlwy.net", ip = "66.33.22.237"),
            Server(name = "caboose.proxy.rlwy.net", ip = "66.33.22.253"),
            Server(name = "viaduct.proxy.rlwy.net", ip = "66.33.22.243"),
            Server(name = "acela.proxy.rlwy.net", ip = "66.33.22.226"),
            Server(name = "sakura.proxy.rlwy.net", ip = "66.33.22.221"),
            Server(name = "tokaido.proxy.rlwy.net", ip = "66.33.22.222"),
            Server(name = "switchback.proxy.rlwy.net", ip = "66.33.22.230"),
            Server(name = "hayabusa.proxy.rlwy.net", ip = "66.33.22.223"),
            Server(name = "reseau.proxy.rlwy.net", ip = "66.33.22.224"),
            Server(name = "shortline.proxy.rlwy.net", ip = "66.33.22.244")
        )
    }

    fun loadServers(): List<Server> {
        val json = prefs.getString(KEY_SERVERS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Server>>() {}.type
            gson.fromJson(json, type)
        } else {
            defaultServers()
        }
    }

    fun saveServers(servers: List<Server>) {
        prefs.edit().putString(KEY_SERVERS, gson.toJson(servers)).apply()
    }

    fun addServer(server: Server) {
        val servers = loadServers().toMutableList()
        servers.add(server)
        saveServers(servers)
    }

    fun removeServer(id: String) {
        val servers = loadServers().toMutableList()
        servers.removeAll { it.id == id }
        saveServers(servers)
    }

    fun defaultServers(): List<Server> {
        val defaults = getDefaultServers()
        saveServers(defaults)
        return defaults
    }

    fun saveScanRecord(record: ScanRecord) {
        val history = loadScanHistory().toMutableList()
        history.add(0, record)
        if (history.size > 50) {
            history.subList(50, history.size).clear()
        }
        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun loadScanHistory(): List<ScanRecord> {
        val json = prefs.getString(KEY_HISTORY, null)
        return if (json != null) {
            val type = object : TypeToken<List<ScanRecord>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun savePingSettings(timeoutMs: Int, packetCount: Int) {
        prefs.edit()
            .putInt("ping_timeout", timeoutMs)
            .putInt("ping_count", packetCount)
            .apply()
    }

    fun loadPingTimeout(): Int = prefs.getInt("ping_timeout", 3000)
    fun loadPingCount(): Int = prefs.getInt("ping_count", 4)

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}