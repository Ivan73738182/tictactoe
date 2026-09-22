package com.ivangames.tictactoe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

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

    private var turnTimer: Timer? = null
    private var secondsLeft = 30

    private lateinit var buttons: MutableList<Button>
    private lateinit var statusText: TextView
    private lateinit var timerText: TextView
    private lateinit var playerXNameView: TextView
    private lateinit var playerONameView: TextView
    private lateinit var playerXScoreView: TextView
    private lateinit var playerOScoreView: TextView
    private lateinit var newGameBtn: Button
    private lateinit var changeModeBtn: Button
    private lateinit var themeBtn: Button
    private lateinit var boardContainer: LinearLayout
    private lateinit var rootLayout: LinearLayout
    private lateinit var prefs: android.content.SharedPreferences
    private var themeMode = "dark"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("tips", Context.MODE_PRIVATE)

        playerXName = intent.getStringExtra("playerX") ?: prefs.getString("lastX", "Игрок X") ?: "Игрок X"
        playerOName = intent.getStringExtra("playerO") ?: prefs.getString("lastO", "Игрок O") ?: "Игрок O"
        originalPlayerOName = playerOName
        vsComputer = intent.getBooleanExtra("vsComputer", false)
        difficulty = intent.getStringExtra("difficulty") ?: "medium"
        boardSize = intent.getIntExtra("boardSize", 3)
        winLength = if (boardSize == 3) 3 else 4
        themeMode = prefs.getString("themeMode", "dark") ?: "dark"

        playerXNameView = findViewById(R.id.playerXName)
        playerONameView = findViewById(R.id.playerOName)
        playerXScoreView = findViewById(R.id.playerXScore)
        playerOScoreView = findViewById(R.id.playerOScore)
        statusText = findViewById(R.id.statusText)
        timerText = findViewById(R.id.timerText)
        newGameBtn = findViewById(R.id.newGameBtn)
        changeModeBtn = findViewById(R.id.changeModeBtn)
themeBtn = findViewById(R.id.themeBtn)      
  val homeBtn = findViewById<Button>(R.id.homeBtn)
        boardContainer = findViewById(R.id.boardContainer)
        rootLayout = findViewById(R.id.rootLayout)

        playerXNameView.text = playerXName
        playerONameView.text = playerOName

        buildBoard()

        newGameBtn.setOnClickListener { startNewGame() }
        changeModeBtn.setOnClickListener { showModeDialog() }
        themeBtn.setOnClickListener { toggleTheme() }
homeBtn.setOnClickListener {
    val intent = Intent(this, NamesActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    finish()
}

        applyTheme()
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
                    setTextColor(Color.WHITE)
                    background = resources.getDrawable(R.drawable.cell_bg, null)
                    stateListAnimator = null
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1f
                    ).apply {
                        setMargins(6, 6, 6, 6)
                    }
                    setOnClickListener { onCellClick(idx) }
                }
                rowLayout.addView(btn)
                buttons.add(btn)
            }
            boardContainer.addView(rowLayout)
        }
    }

private fun toggleTheme() {
    themeMode = if (themeMode == "dark") "pink" else "dark"
    prefs.edit().putString("themeMode", themeMode).apply()
    applyTheme()
}

private fun applyTheme() {
    when (themeMode) {
        "pink" -> {
            rootLayout.setBackgroundResource(R.drawable.bg_pink)
            statusText.setTextColor(Color.WHITE)
            timerText.setTextColor(0xFFE1BEE7.toInt())
            themeBtn.text = "💗"
        }
        else -> {
            rootLayout.setBackgroundResource(R.drawable.bg_gradient)
            statusText.setTextColor(Color.WHITE)
            timerText.setTextColor(0xFFAAAAAA.toInt())
            themeBtn.text = "🌙"
        }
    }
}
    private fun showModeDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_mode, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

view.findViewById<LinearLayout>(R.id.modeTwoPlayers).setOnClickListener {
    dialog.dismiss()
    val intent = Intent(this, NamesActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    finish()
}

        view.findViewById<LinearLayout>(R.id.modeVsComputer).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, ComputerSetupActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.mode4x4).setOnClickListener {
            dialog.dismiss()
            boardSize = 4
            winLength = 4
            buildBoard()
            startNewGame()
        }

        view.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

private fun startNewGame() {
    board.clear()
    repeat(boardSize * boardSize) { board.add(' ') }
    gameOver = false
    currentPlayer = 'X'
    buttons.forEach {
        it.text = ""
        it.isEnabled = true
        it.background = resources.getDrawable(R.drawable.cell_bg, null)
        it.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
    }
    updateStatus()
    updateScores()
    startTurnTimer()

        if (vsComputer && currentPlayer == 'O') {
            buttons.forEach { it.isEnabled = false }
            buttons[0].postDelayed({ computerMove() }, 500)
        }
    }

    private fun startTurnTimer() {
        turnTimer?.cancel()
        secondsLeft = 30
        timerText.text = "Таймер: $secondsLeft сек"
        turnTimer = Timer()
        turnTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    secondsLeft--
                    timerText.text = "Таймер: $secondsLeft сек"
                    if (secondsLeft <= 0 && !gameOver) {
                        turnTimer?.cancel()
                        val free = board.indices.filter { board[it] == ' ' }
                        if (free.isNotEmpty()) {
                            makeMove(free.random(), currentPlayer)
                        }
                    }
                }
            }
        }, 1000, 1000)
    }

    private fun onCellClick(index: Int) {
        if (gameOver) return
        if (board[index] != ' ') return
        if (vsComputer && currentPlayer == 'O') return

        if (trainingMode) {
            val best = findBestMove('X', 'O')
            if (index == best) {
                Toast.makeText(this, "Отличный ход!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Лучше в клетку ${best + 1}", Toast.LENGTH_LONG).show()
            }
        }

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
            startTurnTimer()
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
            startTurnTimer()
        }
    }

    private fun findRandomMove(): Int {
        val free = board.indices.filter { board[it] == ' ' }
        return if (free.isEmpty()) -1 else free.random()
    }

    private fun findMediumMove(): Int {
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
    // Для 4x4 — не используем минимакс (слишком долго), используем средний ИИ
    if (boardSize >= 4) {
        return findMediumMove()
    }

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

        btn.alpha = 0f
        btn.scaleX = 0.5f
        btn.scaleY = 0.5f
        btn.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(200).start()

        val tint = if (player == 'X') 0xFF2196F3.toInt() else 0xFF00BCD4.toInt()
        btn.backgroundTintList = android.content.res.ColorStateList.valueOf(tint)

        val winLine = checkWinLine(player)
        if (winLine != null) {
            gameOver = true
            turnTimer?.cancel()
            highlightWin(winLine)
            if (player == 'X') xWins++ else oWins++
            updateScores()
            saveNames()
            val winner = if (player == 'X') playerXName else playerOName
            showWinDialog(winner)
            return
        }

        if (board.all { it != ' ' }) {
            gameOver = true
            turnTimer?.cancel()
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
        val tint = android.content.res.ColorStateList.valueOf(0xFF4CAF50.toInt())
        line.forEach {
            buttons[it].backgroundTintList = tint
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

    private fun saveNames() {
        prefs.edit().putString("lastX", playerXName).putString("lastO", originalPlayerOName).apply()
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

    override fun onDestroy() {
        super.onDestroy()
        turnTimer?.cancel()
    }
}
