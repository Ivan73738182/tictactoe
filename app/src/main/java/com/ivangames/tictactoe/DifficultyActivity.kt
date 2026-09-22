package com.ivangames.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class DifficultyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)

        val playerSide = intent.getStringExtra("playerSide") ?: "X"

        val easyBtn = findViewById<LinearLayout>(R.id.easyBtn)
        val mediumBtn = findViewById<LinearLayout>(R.id.mediumBtn)
        val hardBtn = findViewById<LinearLayout>(R.id.hardBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)

        easyBtn.setOnClickListener { goToGame(playerSide, "easy") }
        mediumBtn.setOnClickListener { goToGame(playerSide, "medium") }
        hardBtn.setOnClickListener { goToGame(playerSide, "hard") }
        backBtn.setOnClickListener { finish() }
    }

    private fun goToGame(playerSide: String, difficulty: String) {
        val intent = Intent(this, EnterNameActivity::class.java).apply {
            putExtra("playerSide", playerSide)
            putExtra("difficulty", difficulty)
        }
        startActivity(intent)
    }
}
