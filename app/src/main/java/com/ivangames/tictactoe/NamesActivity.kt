package com.ivangames.tictactoe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class NamesActivity : AppCompatActivity() {

    private lateinit var rootLayout: LinearLayout
    private lateinit var themeBtn: Button
    private lateinit var prefs: android.content.SharedPreferences
    private var themeMode = "dark"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_names)

        prefs = getSharedPreferences("tips", Context.MODE_PRIVATE)
        themeMode = prefs.getString("themeMode", "dark") ?: "dark"

        rootLayout = findViewById(R.id.rootLayout)
        themeBtn = findViewById(R.id.themeBtn)

        val xInput = findViewById<EditText>(R.id.playerXInput)
        val oInput = findViewById<EditText>(R.id.playerOInput)
        val startTwoPlayersBtn = findViewById<Button>(R.id.startTwoPlayersBtn)
        val startComputerBtn = findViewById<Button>(R.id.startComputerBtn)
val trainingBtn = findViewById<Button>(R.id.trainingBtn)

        applyTheme()

themeBtn.setOnClickListener {
    themeMode = if (themeMode == "dark") "pink" else "dark"
    prefs.edit().putString("themeMode", themeMode).apply()
    applyTheme()
}

        startTwoPlayersBtn.setOnClickListener {
            val nameX = xInput.text.toString().trim().ifEmpty { "Игрок X" }
            val nameO = oInput.text.toString().trim().ifEmpty { "Игрок O" }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("playerX", nameX)
                putExtra("playerO", nameO)
                putExtra("vsComputer", false)
            }
            startActivity(intent)
        }

        startComputerBtn.setOnClickListener {
            val name = xInput.text.toString().trim().ifEmpty { "Вы" }
            val intent = Intent(this, ComputerSetupActivity::class.java).apply {
                putExtra("playerName", name)
            }
            startActivity(intent)
        }
    }

trainingBtn.setOnClickListener {
    val name = xInput.text.toString().trim()
    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra("playerX", name.ifEmpty { "Игрок X" })
    intent.putExtra("vsComputer", true)
    intent.putExtra("difficulty", "easy")
    intent.putExtra("training", true)
    startActivity(intent)
}

    private fun applyTheme() {
        when (themeMode) {
            "pink" -> {
                rootLayout.setBackgroundResource(R.drawable.bg_pink)
                themeBtn.text = "💗"
            }
            else -> {
                rootLayout.setBackgroundResource(R.drawable.bg_gradient)
                themeBtn.text = "🌙"
            }
        }
    }
}
