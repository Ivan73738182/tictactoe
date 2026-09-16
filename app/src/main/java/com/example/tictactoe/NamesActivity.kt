package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class NamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_names)

        val xInput = findViewById<EditText>(R.id.playerXInput)
        val oInput = findViewById<EditText>(R.id.playerOInput)
        val startTwoPlayersBtn = findViewById<Button>(R.id.startTwoPlayersBtn)
        val startComputerBtn = findViewById<Button>(R.id.startComputerBtn)

        startTwoPlayersBtn.setOnClickListener {
            val nameX = xInput.text.toString().trim().ifEmpty { "Игрок X" }
            val nameO = oInput.text.toString().trim().ifEmpty { "Игрок O" }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("playerX", nameX)
                putExtra("playerO", nameO)
                putExtra("vsComputer", false)
            }
            startActivity(intent)
            finish()
        }

        startComputerBtn.setOnClickListener {
            // Переходим на выбор X или O
            val name = xInput.text.toString().trim().ifEmpty { "Вы" }
            val intent = Intent(this, ComputerSetupActivity::class.java).apply {
                putExtra("playerName", name)
            }
            startActivity(intent)
        }
    }
}
