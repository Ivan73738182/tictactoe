package com.example.tictactoe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val board = CharArray(9) { ' ' }
    private var playerTurn = true
    private var gameOver = false
    private var wins = 0
    private var losses = 0
    private var draws = 0

    private lateinit var buttons: List<Button>
    private lateinit var statusText: TextView
    private lateinit var scoreText: TextView
    private lateinit var newGameBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        scoreText = findViewById(R.id.scoreText)
        newGameBtn = findViewById(R.id.newGameBtn)

        buttons = listOf(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClick(index) }
        }

        newGameBtn.setOnClickListener { startNewGame() }

        startNewGame()
    }

    private fun startNewGame() {
        board.fill(' ')
        playerTurn = true
        gameOver = false
        buttons.forEach {
            it.text = ""
            it.isEnabled = true
        }
        statusText.text = "Твой ход (X)"
        updateScore()
    }

    private fun onCellClick(index: Int) {
        if (gameOver) return
        if (board[index] != ' ') return

        board[index] = 'X'
        buttons[index].text = "X"
        buttons[index].isEnabled = false

        if (checkWin('X')) {
            wins++
            gameOver = true
            statusText.text = "Ты победил!"
            updateScore()
            Toast.makeText(this, "Победа!", Toast.LENGTH_SHORT).show()
            return
        }
        if (isFull()) {
            draws++
            gameOver = true
            statusText.text = "Ничья"
            updateScore()
            return
        }

        playerTurn = false
        statusText.text = "Ход компьютера..."
        buttons.forEach { if (board[buttons.indexOf(it)] == ' ') it.isEnabled = false }

        buttons[0].postDelayed({ computerMove() }, 500)
    }

    private fun computerMove() {
        if (gameOver) return

        val move = findBestMove()
        board[move] = 'O'
        buttons[move].text = "O"
        buttons[move].isEnabled = false

        if (checkWin('O')) {
            losses++
            gameOver = true
            statusText.text = "Компьютер победил"
            updateScore()
            return
        }
        if (isFull()) {
            draws++
            gameOver = true
            statusText.text = "Ничья"
            updateScore()
            return
        }

        playerTurn = true
        statusText.text = "Твой ход (X)"
        buttons.forEachIndexed { i, b -> b.isEnabled = board[i] == ' ' }
    }

    private fun findBestMove(): Int {
        for (i in 0..8) {
            if (board[i] == ' ') {
                board[i] = 'O'
                if (checkWin('O')) { board[i] = ' '; return i }
                board[i] = ' '
            }
        }
        for (i in 0..8) {
            if (board[i] == ' ') {
                board[i] = 'X'
                if (checkWin('X')) { board[i] = ' '; return i }
                board[i] = ' '
            }
        }
        if (board[4] == ' ') return 4
        val corners = listOf(0, 2, 6, 8).filter { board[it] == ' ' }
        if (corners.isNotEmpty()) return corners.random()
        return (0..8).first { board[it] == ' ' }
    }

    private fun checkWin(player: Char): Boolean {
        val lines = listOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )
        return lines.any { line ->
            board[line[0]] == player && board[line[1]] == player && board[line[2]] == player
        }
    }

    private fun isFull(): Boolean = board.all { it != ' ' }

    private fun updateScore() {
        scoreText.text = "Победы: $wins | Ничьи: $draws | Поражения: $losses"
    }
}
