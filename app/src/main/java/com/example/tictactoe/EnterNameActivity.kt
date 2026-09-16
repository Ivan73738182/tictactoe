package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EnterNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_name)

        val playerSide = intent.getStringExtra("playerSide") ?: "X"

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val startBtn = findViewById<Button>(R.id.startBtn)
        val skipBtn = findViewById<Button>(R.id.skipBtn)

        startBtn.setOnClickListener {
            val name = nameInput.text.toString().trim().ifEmpty { "Вы" }
            goToGame(name, playerSide)
        }

        skipBtn.setOnClickListener {
            goToGame("Вы", playerSide)
        }
    }

    private fun goToGame(name: String, playerSide: String) {
        val xName = if (playerSide == "X") name else "Компьютер"
        val oName = if (playerSide == "O") name else "Компьютер"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("playerX", xName)
            putExtra("playerO", oName)
            putExtra("vsComputer", true)
        }
        startActivity(intent)
        finish()
    }
}
