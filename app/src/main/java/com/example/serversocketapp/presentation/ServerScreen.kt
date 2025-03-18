package com.example.serversocketapp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serversocketapp.R
import com.example.serversocketapp.data.SocketServer

class ServerScreen : ComponentActivity() {
    private lateinit var viewModel: ServerScreenViewModel
    private lateinit var serverIpAddress: TextView
    private lateinit var port: EditText
    private lateinit var backlog: EditText
    private lateinit var startStopButton: Button
    private lateinit var allMessages: RecyclerView
    private lateinit var messagesAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        setupViewModel()
        setupUI()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupViewModel() {
        val socketServer = SocketServer()
        viewModel = ServerScreenViewModel(socketServer)
    }

    private fun setupUI() {
        serverIpAddress = findViewById(R.id.tvServerIp)
        port = findViewById(R.id.etPort)
        backlog = findViewById(R.id.etBacklog)
        startStopButton = findViewById(R.id.btnStartStopServer)
        allMessages = findViewById(R.id.allMessages)

        messagesAdapter = MessageAdapter()
        allMessages.layoutManager = LinearLayoutManager(this)
        allMessages.adapter = messagesAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.serverIPAddress.observe(this) { ipAddress ->
            serverIpAddress.text = "Server IP: $ipAddress"
        }

        viewModel.isServerRunning.observe(this) { isRunning ->
            startStopButton.text = if (isRunning) "Stop Server" else "Start Server"
        }

        viewModel.messages.observe(this) { messages ->
            messagesAdapter.submitList(messages)
        }
    }

    private fun setupClickListeners() {
        startStopButton.setOnClickListener {
            val port = port.text.toString().toIntOrNull() ?: 6868
            val backlog = backlog.text.toString().toIntOrNull() ?: 10

            if (viewModel.isServerRunning.value == true) {
                viewModel.stopServer()
            } else {
                viewModel.startServer(port, backlog)
            }
        }
    }
}

