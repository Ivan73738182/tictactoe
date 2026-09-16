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

        val playAsXBtn = findViewById<LinearLayout>(R.id.playAsXBtn)
        val playAsOBtn = findViewById<LinearLayout>(R.id.playAsOBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)

        playAsXBtn.setOnClickListener {
            val intent = Intent(this, EnterNameActivity::class.java).apply {
                putExtra("playerSide", "X")
            }
            startActivity(intent)
        }

        playAsOBtn.setOnClickListener {
            val intent = Intent(this, EnterNameActivity::class.java).apply {
                putExtra("playerSide", "O")
            }
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}
