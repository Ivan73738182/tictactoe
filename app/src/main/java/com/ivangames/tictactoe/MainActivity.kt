package com.ivangames.tictactoe

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var boardSize = 3
    private var winLength = 3
    private val board = mutableListOf<Char>()
    private var gameOver = false
    private var currentPlayer = 'X'
    private var vsComputer = false
    private var difficulty = "medium"
    private var trainingMode = false

    private var playerXName = "Игрок X"
    private var playerOName = "Игрок O"
    private var originalPlayerOName = "Игрок O"

    private var xWins = 0
    private var oWins = 0
    private var draws = 0

    private lateinit var buttons: MutableList<Button>
    private lateinit var statusText: TextView
    private lateinit var playerXNameView: TextView
    private lateinit var playerONameView: TextView
    private lateinit var playerXScoreView: TextView
    private lateinit var playerOScoreView: TextView
    private lateinit var newGameBtn: Button
    private lateinit var changeModeBtn: Button
    private lateinit var boardContainer: LinearLayout
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("tips", Context.MODE_PRIVATE)

        playerXName = intent.getStringExtra("playerX") ?: "Игрок X"
        playerOName = intent.getStringExtra("playerO") ?: "Игрок O"
        originalPlayerOName = playerOName
        vsComputer = intent.getBooleanExtra("vsComputer", false)
        difficulty = intent.getStringExtra("difficulty") ?: "medium"
        boardSize = intent.getIntExtra("boardSize", 3)
        winLength = if (boardSize == 3) 3 else 4

        playerXNameView = findViewById(R.id.playerXName)
        playerONameView = findViewById(R.id.playerOName)
        playerXScoreView = findViewById(R.id.playerXScore)
        playerOScoreView = findViewById(R.id.playerOScore)
        statusText = findViewById(R.id.statusText)
        newGameBtn = findViewById(R.id.newGameBtn)
        changeModeBtn = findViewById(R.id.changeModeBtn)
        boardContainer = findViewById(R.id.boardContainer)

        playerXNameView.text = playerXName
        playerONameView.text = playerOName

        buildBoard()

        newGameBtn.setOnClickListener { startNewGame() }
        changeModeBtn.setOnClickListener { showModeDialog() }

        startNewGame()
    }

    private fun buildBoard() {
        boardContainer.removeAllViews()
        buttons = mutableListOf()

        for (row in 0 until boardSize) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
                )
            }
            for (col in 0 until boardSize) {
                val idx = row * boardSize + col
                val btn = Button(this).apply {
                    text = ""
                    textSize = if (boardSize == 3) 48f else 32f
                    setTextColor(0xFFFFFFFF.toInt())
                    backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF2A2A3A.toInt())
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                        setMargins(4, 4, 4, 4)
                    }
                    setOnClickListener { onCellClick(idx) }
                }
                rowLayout.addView(btn)
                buttons.add(btn)
            }
            boardContainer.addView(rowLayout)
        }
    }

    private fun showModeDialog() {
        val options = arrayOf("На двоих", "С компьютером", "Сбросить счёт")
        AlertDialog.Builder(this)
            .setTitle("Режим игры")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        vsComputer = false
                        trainingMode = false
                        playerOName = originalPlayerOName
                    }
                    1 -> {
                        vsComputer = true
                        playerOName = "Компьютер"
                    }
                    2 -> {
                        xWins = 0
                        oWins = 0
                        draws = 0
                    }
                }
                playerONameView.text = playerOName
                startNewGame()
            }
            .show()
    }

    private fun startNewGame() {
        board.clear()
        repeat(boardSize * boardSize) { board.add(' ') }
        gameOver = false
        currentPlayer = 'X'
        buttons.forEach {
            it.text = ""
            it.isEnabled = true
            it.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF2A2A3A.toInt())
        }
        updateStatus()
        updateScores()
    }

    private fun onCellClick(index: Int) {
        if (gameOver) return
        if (board[index] != ' ') return
        if (vsComputer && currentPlayer == 'O') return

        makeMove(index, currentPlayer)

        if (gameOver) return

        if (vsComputer && currentPlayer == 'X') {
            currentPlayer = 'O'
            updateStatus()
            buttons.forEach { it.isEnabled = false }
            buttons[0].postDelayed({ computerMove() }, 400)
        } else {
            currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
            updateStatus()
        }
    }

    private fun computerMove() {
        if (gameOver) return

        val move = when (difficulty) {
            "easy" -> findRandomMove()
            "hard" -> findBestMove('O', 'X')
            else -> findMediumMove()
        }

        if (move >= 0) makeMove(move, 'O')

        if (!gameOver) {
            currentPlayer = 'X'
            buttons.forEachIndexed { i, b -> b.isEnabled = board[i] == ' ' }
            updateStatus()
        }
    }

    private fun findRandomMove(): Int {
        val free = board.indices.filter { board[it] == ' ' }
        return if (free.isEmpty()) -1 else free.random()
    }

    private fun findMediumMove(): Int {
        // Победный ход
        for (i in board.indices) {
            if (board[i] == ' ') {
                board[i] = 'O'
                if (checkWinAll('O')) {
                    board[i] = ' '
                    return i
                }
                board[i] = ' '
            }
        }
        // Блок
        for (i in board.indices) {
            if (board[i] == ' ') {
                board[i] = 'X'
                if (checkWinAll('X')) {
                    board[i] = ' '
                    return i
                }
                board[i] = ' '
            }
        }
        return findRandomMove()
    }

    private fun findBestMove(me: Char, enemy: Char): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1
        for (i in board.indices) {
            if (board[i] == ' ') {
                board[i] = me
                val score = minimax(0, false, me, enemy)
                board[i] = ' '
                if (score > bestScore) {
                    bestScore = score
                    bestMove = i
                }
            }
        }
        return bestMove
    }

    private fun minimax(depth: Int, isMax: Boolean, me: Char, enemy: Char): Int {
        if (checkWinAll(me)) return 10 - depth
        if (checkWinAll(enemy)) return depth - 10
        if (board.all { it != ' ' }) return 0

        if (isMax) {
            var best = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == ' ') {
                    board[i] = me
                    best = maxOf(best, minimax(depth + 1, false, me, enemy))
                    board[i] = ' '
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == ' ') {
                    board[i] = enemy
                    best = minOf(best, minimax(depth + 1, true, me, enemy))
                    board[i] = ' '
                }
            }
            return best
        }
    }

    private fun makeMove(index: Int, player: Char) {
        board[index] = player
        val btn = buttons[index]
        btn.text = player.toString()
        btn.isEnabled = false

        btn.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (player == 'X') 0xFF2196F3.toInt() else 0xFF00BCD4.toInt()
        )

        val winLine = checkWinLine(player)
        if (winLine != null) {
            gameOver = true
            highlightWin(winLine)
            if (player == 'X') xWins++ else oWins++
            updateScores()
            val winner = if (player == 'X') playerXName else playerOName
            showWinDialog(winner)
            return
        }

        if (board.all { it != ' ' }) {
            gameOver = true
            draws++
            updateScores()
            showWinDialog(null)
        }
    }

    private fun checkWinAll(player: Char): Boolean = checkWinLine(player) != null

    private fun checkWinLine(player: Char): IntArray? {
        val lines = mutableListOf<IntArray>()
        for (r in 0 until boardSize) {
            for (c in 0..boardSize - winLength) {
                lines.add(IntArray(winLength) { c + it + r * boardSize })
            }
        }
        for (c in 0 until boardSize) {
            for (r in 0..boardSize - winLength) {
                lines.add(IntArray(winLength) { c + (r + it) * boardSize })
            }
        }
        for (r in 0..boardSize - winLength) {
            for (c in 0..boardSize - winLength) {
                lines.add(IntArray(winLength) { (c + it) + (r + it) * boardSize })
            }
        }
        for (r in 0..boardSize - winLength) {
            for (c in winLength - 1 until boardSize) {
                lines.add(IntArray(winLength) { (c - it) + (r + it) * boardSize })
            }
        }

        for (line in lines) {
            if (line.all { board[it] == player }) return line
        }
        return null
    }

    private fun highlightWin(line: IntArray) {
        line.forEach {
            buttons[it].backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt())
        }
    }

    private fun updateStatus() {
        statusText.text = when {
            vsComputer -> if (currentPlayer == 'X') "Ход $playerXName" else "Ход компьютера..."
            else -> if (currentPlayer == 'X') "Ход $playerXName" else "Ход $playerOName"
        }
    }

    private fun updateScores() {
        playerXScoreView.text = xWins.toString()
        playerOScoreView.text = oWins.toString()
    }

    private fun showWinDialog(winner: String?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_win, null)
        val winText = view.findViewById<TextView>(R.id.winText)
        val playAgain = view.findViewById<Button>(R.id.playAgainBtn)

        winText.text = if (winner == null) "Ничья!" else "Победил(а) $winner!"

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
