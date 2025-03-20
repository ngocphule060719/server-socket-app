package com.example.serversocketapp.di

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class SocketServer {
    private var serverSocket: ServerSocket? = null
    private var client: Socket? = null

    fun startServer(port: Int, backlog: Int) {
        serverSocket = ServerSocket(port, backlog)
    }

    fun stopServer() {
        client?.close()
        client = null
        serverSocket?.close()
        serverSocket = null
    }

    fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            interfaces.toList().flatMap { it.inetAddresses.toList() }
                .find { !it.isLoopbackAddress && it.hostAddress?.contains(".") == true }
                ?.hostAddress
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun startListenClient(onMessageReceived: (String) -> Unit) {
        try {
            client = serverSocket?.accept() ?: return
            onMessageReceived("Client connected: ${client?.inetAddress?.hostAddress}")
            client?.let {
                val reader = BufferedReader(InputStreamReader(it.getInputStream()))
                val writer = PrintWriter(it.getOutputStream(), true)

                while (true) {
                    val message = reader.readLine() ?: break
                    onMessageReceived("Client sent: $message")
                    writer.println("Echo: $message")

                    if (message.lowercase() == "stop") {
                        writer.println("Stop signal received. Bye")
                        break
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}