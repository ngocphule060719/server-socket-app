package com.example.serversocketapp

import java.net.Socket

interface ClientHandler {
    companion object {
        const val STOP_SIGNAL = "stop"
    }

    fun handleClient(clientSocket: Socket)
}