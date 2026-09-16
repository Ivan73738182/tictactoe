package com.example.tictactoe

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val board = CharArray(9) { ' ' }
    private var gameOver = false

    private var playerXName = "Игрок X"
    private var playerOName = "Игрок O"
    private var vsComputer = false  // false = на двоих, true = с компьютером

    private var xWins = 0
    private var oWins = 0
    private var draws = 0

    private lateinit var buttons: List<Button>
    private lateinit var statusText: TextView
    private lateinit var playerXNameView: TextView
    private lateinit var playerONameView: TextView
    private lateinit var playerXScoreView: TextView
    private lateinit var playerOScoreView: TextView
    private lateinit var playerXPanel: LinearLayout
    private lateinit var playerOPanel: LinearLayout
    private lateinit var newGameBtn: Button
    private lateinit var changeModeBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerXName = intent.getStringExtra("playerX") ?: "Игрок X"
        playerOName = intent.getStringExtra("playerO") ?: "Игрок O"

        playerXNameView = findViewById(R.id.playerXName)
        playerONameView = findViewById(R.id.playerOName)
        playerXScoreView = findViewById(R.id.playerXScore)
        playerOScoreView = findViewById(R.id.playerOScore)
        playerXPanel = findViewById(R.id.playerXPanel)
        playerOPanel = findViewById(R.id.playerOPanel)
        statusText = findViewById(R.id.statusText)
        newGameBtn = findViewById(R.id.newGameBtn)
        changeModeBtn = findViewById(R.id.changeModeBtn)

        buttons = listOf(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onCellClick(index) }
        }

        newGameBtn.setOnClickListener { startNewGame() }
        changeModeBtn.setOnClickListener { changeMode() }

        playerXNameView.text = playerXName
        playerONameView.text = playerOName

        startNewGame()
    }

    private fun changeMode() {
        val options = arrayOf("На двоих", "С компьютером")
        AlertDialog.Builder(this)
            .setTitle("Режим игры")
            .setItems(options) { _, which ->
                vsComputer = (which == 1)
                if (vsComputer) {
                    playerOName = "Компьютер"
                } else if (playerOName == "Компьютер") {
                    playerOName = "Игрок O"
                }
                playerONameView.text = playerOName
                startNewGame()
            }
            .show()
    }

    private fun startNewGame() {
        board.fill(' ')
        gameOver = false
        buttons.forEachIndexed { i, btn ->
            btn.text = ""
            btn.isEnabled = true
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF212121.toInt())
        }
        updateStatus()
        updateScores()
        highlightActivePlayer()
    }

    private fun onCellClick(index: Int) {
        if (gameOver) return
        if (board[index] != ' ') return

        // Ход игрока X
        makeMove(index, 'X')
        if (gameOver) return

        if (vsComputer) {
            // Ход компьютера (O)
            buttons.forEach { it.isEnabled = false }
            buttons[0].postDelayed({
                val move = findBestMove()
                if (move >= 0) makeMove(move, 'O')
                if (!gameOver) {
                    buttons.forEachIndexed { i, b -> b.isEnabled = board[i] == ' ' }
                    updateStatus()
                    highlightActivePlayer()
                }
            }, 400)
        } else {
            updateStatus()
            highlightActivePlayer()
        }
    }

    private fun makeMove(index: Int, player: Char) {
        board[index] = player
        val btn = buttons[index]
        btn.text = player.toString()
        btn.isEnabled = false

        if (player == 'X') {
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF2196F3.toInt())
        } else {
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF00BCD4.toInt())
        }

        val winLine = checkWinLine(player)
        if (winLine != null) {
            gameOver = true
            highlightWin(winLine)
            if (player == 'X') xWins++ else oWins++
            updateScores()
            val winnerName = if (player == 'X') playerXName else playerOName
            showWinDialog(winnerName)
            return
        }

        if (isFull()) {
            gameOver = true
            draws++
            updateScores()
            showWinDialog(null)
        }
    }

    private fun findBestMove(): Int {
        // 1. Победный ход для O
        for (i in 0..8) {
            if (board[i] == ' ') {
                board[i] = 'O'
                if (checkWin('O')) { board[i] = ' '; return i }
                board[i] = ' '
            }
        }
        // 2. Блокировать X
        for (i in 0..8) {
            if (board[i] == ' ') {
                board[i] = 'X'
                if (checkWin('X')) { board[i] = ' '; return i }
                board[i] = ' '
            }
        }
        // 3. Центр
        if (board[4] == ' ') return 4
        // 4. Углы
        val corners = listOf(0, 2, 6, 8).filter { board[it] == ' ' }
        if (corners.isNotEmpty()) return corners.random()
        // 5. Любая
        val free = (0..8).filter { board[it] == ' ' }
        return if (free.isEmpty()) -1 else free.random()
    }

    private fun checkWin(player: Char): Boolean {
        return checkWinLine(player) != null
    }

    private fun checkWinLine(player: Char): IntArray? {
        val lines = listOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )
        for (line in lines) {
            if (board[line[0]] == player && board[line[1]] == player && board[line[2]] == player) {
                return line
            }
        }
        return null
    }

    private fun isFull(): Boolean = board.all { it != ' ' }

    private fun highlightWin(line: IntArray) {
        for (i in line) {
            buttons[i].backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt())
        }
    }

    private fun updateStatus() {
        val current = if (vsComputer) playerXName else "игрока X"
        statusText.text = if (vsComputer) "$playerXName, ваш ход" else "Ход игрока X"
    }

    private fun updateScores() {
        playerXScoreView.text = xWins.toString()
        playerOScoreView.text = oWins.toString()
    }

    private fun highlightActivePlayer() {
        playerXPanel.setBackgroundColor(0xFF212121.toInt())
        playerOPanel.setBackgroundColor(0xFF212121.toInt())
    }

    private fun showWinDialog(winner: String?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_win, null)
        val winText = view.findViewById<TextView>(R.id.winText)
        val playAgain = view.findViewById<Button>(R.id.playAgainBtn)

        if (winner == null) {
            winText.text = "Ничья!"
        } else {
            winText.text = "Победил(а) $winner!"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        playAgain.setOnClickListener {
            dialog.dismiss()
            startNewGame()
        }

        dialog.show()
    }
}
