package com.example.hangman

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var hangmanView: HangmanView
    private lateinit var wordText: TextView
    private lateinit var infoText: TextView
    private lateinit var newGameBtn: Button

    private var secretWord: String = ""
    private val guessed = mutableSetOf<Char>()
    private var mistakes = 0
    private val maxMistakes = 6
    private var isGameOver = false

    private lateinit var allWords: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hangmanView = findViewById(R.id.hangmanView)
        wordText = findViewById(R.id.wordText)
        infoText = findViewById(R.id.infoText)
        newGameBtn = findViewById(R.id.newGameBtn)

        allWords = loadWords(this)

        newGameBtn.setOnClickListener { startNewGame() }

        startNewGame()
    }

    private fun startNewGame() {
        secretWord = allWords.random().uppercase()
        guessed.clear()
        mistakes = 0
        isGameOver = false
        hangmanView.mistakes = 0
        hangmanView.isLost = false

        buildAlphabet()
        updateUI()
    }

    private fun buildAlphabet() {
        val container = findViewById<LinearLayout>(R.id.alphabetContainer)
        container.removeAllViews()

        val letters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toList()
        val rows = 3
        val perRow = (letters.size + rows - 1) / rows

        for (r in 0 until rows) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            for (i in 0 until perRow) {
                val idx = r * perRow + i
                if (idx >= letters.size) break
                val letter = letters[idx]
                val btn = Button(this).apply {
                    text = letter.toString()
                    textSize = 12f
                    minWidth = 0
                    minimumWidth = 0
                    setPadding(4, 8, 4, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    setOnClickListener { onLetter(letter) }
                }
                row.addView(btn)
            }
            container.addView(row)
        }
    }

    private fun onLetter(letter: Char) {
        if (isGameOver) return
        if (guessed.contains(letter)) return

        guessed.add(letter)

        if (!secretWord.contains(letter)) {
            mistakes++
            hangmanView.mistakes = mistakes
        }

        updateUI()
        checkGameEnd()
    }

    private fun updateUI() {
        val display = secretWord.map { if (guessed.contains(it)) it else '_' }
            .joinToString(" ")
        wordText.text = display
        infoText.text = "Ошибки: $mistakes / $maxMistakes"

        val container = findViewById<LinearLayout>(R.id.alphabetContainer)
        for (r in 0 until container.childCount) {
            val row = container.getChildAt(r) as LinearLayout
            for (i in 0 until row.childCount) {
                val btn = row.getChildAt(i) as Button
                val ch = btn.text.first()
                btn.isEnabled = !guessed.contains(ch)
            }
        }
    }

    private fun checkGameEnd() {
        if (secretWord.all { guessed.contains(it) }) {
            isGameOver = true
            Toast.makeText(this, "🎉 Победа! Слово: $secretWord", Toast.LENGTH_LONG).show()
        } else if (mistakes >= maxMistakes) {
            isGameOver = true
            hangmanView.isLost = true
            Toast.makeText(this, "💀 Проигрыш! Слово: $secretWord", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun loadWords(context: Context): List<String> {
            return try {
                val json = context.assets.open("words.json")
                    .bufferedReader().use { it.readText() }
                val obj = JSONObject(json)
                val result = mutableListOf<String>()
                obj.keys().forEach { key ->
                    val arr = obj.getJSONArray(key)
                    for (i in 0 until arr.length()) {
                        result.add(arr.getString(i))
                    }
                }
                result
            } catch (e: Exception) {
                listOf("кошка", "собака", "тигр", "пицца", "москва")
            }
        }
    }
}
