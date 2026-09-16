package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ComputerSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_computer_setup)

        val playerName = intent.getStringExtra("playerName") ?: "Вы"

        val playAsXBtn = findViewById<LinearLayout>(R.id.playAsXBtn)
        val playAsOBtn = findViewById<LinearLayout>(R.id.playAsOBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)

        playAsXBtn.setOnClickListener {
            startGame(playerName, "Компьютер")
        }

        playAsOBtn.setOnClickListener {
            startGame("Компьютер", playerName)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun startGame(xName: String, oName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("playerX", xName)
            putExtra("playerO", oName)
            putExtra("vsComputer", true)
        }
        startActivity(intent)
    }
}
