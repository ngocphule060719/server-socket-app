package com.example.serversocketapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ServerSocket

class ServerViewModel : ViewModel() {
    private val _isServerRunning = MutableLiveData(false)
    val isServerRunning: LiveData<Boolean> get() = _isServerRunning

    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> get() = _messages

    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null

    fun startServer(port: Int, backlog: Int, clientHandler: ClientHandler) {
        if (_isServerRunning.value == true) {
            return
        }

        serverJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(port, backlog)
                _isServerRunning.postValue(true)
                addMessage("Server started on port $port with backlog $backlog.\nIP address: ${serverSocket!!.inetAddress.hostAddress}")

                while (_isServerRunning.value == true) {
                    val clientSocket = serverSocket!!.accept()
                    addMessage("Client connected: ${clientSocket.inetAddress.hostAddress}")
                    launch { clientHandler.handleClient(clientSocket) }
                }
            } catch (e: Exception) {
                addMessage("Server error: ${e.message}")
            }
        }
    }

    fun stopServer() {
        _isServerRunning.postValue(false)
        serverSocket?.close()
        serverJob?.cancel()
        serverSocket = null
        serverJob = null
        addMessage("Server stopped.")
    }

    private fun addMessage(message: String) {
        val newMessages = _messages.value?.toMutableList() ?: mutableListOf()
        newMessages.add(message)
        _messages.postValue(newMessages)
    }
}