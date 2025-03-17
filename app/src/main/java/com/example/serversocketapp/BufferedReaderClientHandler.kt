package com.example.serversocketapp

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class BufferedReaderClientHandler : ClientHandler {
    override fun handleClient(clientSocket: Socket) {
        clientSocket.use {
            val reader = BufferedReader(InputStreamReader(it.getInputStream()))
            val writer = PrintWriter(it.getOutputStream(), true)

            while (true) {
                val message = reader.readLine() ?: break
                println("Client sent: $message")
                writer.println("Echo: $message")

                if (message.trim().lowercase() == ClientHandler.STOP_SIGNAL) {
                    writer.println("Stop signal received. Server is shutting down.")
                    break
                }
            }
        }
    }
}