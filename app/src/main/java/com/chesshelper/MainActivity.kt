package com.chesshelper

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.HorizontalLayout
import android.widget.ScrollView
import android.widget.EditText
import android.widget.Toast
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity

class MainActivity : Activity() {
    private lateinit var boardView: BoardView
    private lateinit var statusText: TextView
    private lateinit var computerMoveText: TextView
    private lateinit var gameModeText: TextView
    private lateinit var playVsComputerBtn: Button
    private lateinit var playVsFriendsBtn: Button
    private lateinit var exitBtn: Button
    
    private var chessBoard: Array<CharArray?> = arrayOf(
        charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'),
        charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
        charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
        charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r')
    )
    
    private var currentPlayer = 'w' // белые ходят первыми
    private var gameState = "MENU" // MENU, PLAYING, RESULT
    private var moveHistory = mutableListOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        boardView = findViewById(R.id.boardView)
        statusText = findViewById(R.id.statusText)
        computerMoveText = findViewById(R.id.computerMoveText)
        gameModeText = findViewById(R.id.gameModeText)
        playVsComputerBtn = findViewById(R.id.playVsComputerBtn)
        playVsFriendsBtn = findViewById(R.id.playVsFriendsBtn)
        exitBtn = findViewById(R.id.exitBtn)
        
        // Стиль зеленого фона
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor("#2E7D32"))
        
        playVsComputerBtn.setOnClickListener { startGame("COMPUTER") }
        playVsFriendsBtn.setOnClickListener { startGame("FRIENDS") }
        exitBtn.setOnClickListener { finish() }
        
        updateMenu()
    }
    
    private fun startGame(mode: String) {
        gameState = "PLAYING"
        currentPlayer = 'w'
        moveHistory.clear()
        resetBoard()
        updateUI()
    }
    
    private fun resetBoard() {
        chessBoard = arrayOf(
            charArrayOf('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'),
            charArrayOf('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'),
            charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
            charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
            charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
            charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
            charArrayOf('p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'),
            charArrayOf('r', 'n', 'b', 'q', 'k', 'b', 'n', 'r')
        )
    }
    
    private fun updateUI() {
        boardView.setBoard(chessBoard, currentPlayer)
        statusText.text = if (currentPlayer == 'w') "Ваш ход (Белые)" else "Ход компьютера"
        gameModeText.visibility = View.GONE
        playVsComputerBtn.visibility = View.GONE
        playVsFriendsBtn.visibility = View.GONE
        exitBtn.visibility = View.GONE
        computerMoveText.visibility = if (currentPlayer == 'b') View.VISIBLE else View.GONE
        
        if (currentPlayer == 'b') {
            makeComputerMove()
        }
    }
    
    private fun makeComputerMove() {
        val bestMove = findBestMove()
        if (bestMove != null) {
            val (fromRow, fromCol, toRow, toCol) = bestMove
            movePiece(fromRow, fromCol, toRow, toCol)
            currentPlayer = 'w'
            computerMoveText.visibility = View.GONE
            updateUI()
        } else {
            statusText.text = "Шах и мат!"
            gameState = "RESULT"
        }
    }
    
    private fun findBestMove(): IntArray? {
        var bestMove: IntArray? = null
        var maxEval = -1000
        
        for (fromRow in 0..7) {
            for (fromCol in 0..7) {
                val piece = chessBoard[fromRow]?.get(fromCol) ?: ' '
                if (piece.isUpperCase()) continue
                
                // Генерируем возможные ходы для черной фигуры
                for (toRow in 0..7) {
                    for (toCol in 0..7) {
                        if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                            // Выполняем ход
                            val tempPiece = chessBoard[toRow]?.get(toCol) ?: ' '
                            chessBoard[fromRow]?.set(fromCol, ' ')
                            chessBoard[toRow]?.set(toCol, piece)
                            
                            val eval = evaluatePosition()
                            
                            // Возвращаем ход обратно
                            chessBoard[fromRow]?.set(fromCol, piece)
                            chessBoard[toRow]?.set(toCol, tempPiece)
                            
                            if (eval > maxEval) {
                                maxEval = eval
                                bestMove = intArrayOf(fromRow, fromCol, toRow, toCol)
                            }
                        }
                    }
                }
            }
        }
        return bestMove
    }
    
    private fun isValidMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val piece = chessBoard[fromRow]?.get(fromCol) ?: ' '
        val target = chessBoard[toRow]?.get(toCol) ?: ' '
        
        if (piece.isLowerCase() && piece != 'p') return false
        if (target.isLowerCase() && piece.isUpperCase()) return false
        
        when (piece) {
            'P' -> {
                // Ход пешки
                if (fromRow == 6 && toRow == 4 && fromCol == toCol) return true
                if (toRow == fromRow - 1 && fromCol == toCol && target == ' ') return true
                if (toRow == fromRow - 1 && kotlin.math.abs(toCol - fromCol) == 1 && target.isUpperCase()) return true
            }
            'N' -> {
                // Ход коня
                return kotlin.math.abs(toRow - fromRow) == 2 && kotlin.math.abs(toCol - fromCol) == 1 ||
                       kotlin.math.abs(toRow - fromRow) == 1 && kotlin.math.abs(toCol - fromCol) == 2
            }
            'B' -> {
                // Ход слона
                if (kotlin.math.abs(toRow - fromRow) != kotlin.math.abs(toCol - fromCol)) return false
                val stepRow = if (toRow > fromRow) 1 else -1
                val stepCol = if (toCol > fromCol) 1 else -1
                var r = fromRow + stepRow
                var c = fromCol + stepCol
                while (r != toRow && c != toCol) {
                    if (chessBoard[r]?.get(c) != ' ') return false
                    r += stepRow
                    c += stepCol
                }
                return true
            }
            'R' -> {
                // Ход ладьи
                if (fromRow != toRow && fromCol != toCol) return false
                if (fromRow == toRow) {
                    val start = kotlin.math.min(fromCol, toCol) + 1
                    val end = kotlin.math.max(fromCol, toCol)
                    for (c in start until end) {
                        if (chessBoard[fromRow]?.get(c) != ' ') return false
                    }
                } else {
                    val start = kotlin.math.min(fromRow, toRow) + 1
                    val end = kotlin.math.max(fromRow, toRow)
                    for (r in start until end) {
                        if (chessBoard[r]?.get(fromCol) != ' ') return false
                    }
                }
                return true
            }
            'Q' -> {
                // Ход ферзя
                if (fromRow != toRow && fromCol != toCol && kotlin.math.abs(toRow - fromRow) != kotlin.math.abs(toCol - fromCol)) return false
                return isPathClear(fromRow, fromCol, toRow, toCol)
            }
            'K' -> {
                // Ход короля
                return kotlin.math.abs(toRow - fromRow) <= 1 && kotlin.math.abs(toCol - fromCol) <= 1
            }
        }
        return false
    }
    
    private fun isPathClear(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val stepRow = if (toRow > fromRow) 1 else -1
        val stepCol = if (toCol > fromCol) 1 else -1
        var r = fromRow + stepRow
        var c = fromCol + stepCol
        while (r != toRow && c != toCol) {
            if (chessBoard[r]?.get(c) != ' ') return false
            r += stepRow
            c += stepCol
        }
        return true
    }
    
    private fun evaluatePosition(): Int {
        var eval = 0
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = chessBoard[row]?.get(col) ?: ' '
                when (piece) {
                    'P' -> eval += 10
                    'N' -> eval += 30
                    'B' -> eval += 30
                    'R' -> eval += 50
                    'Q' -> eval += 90
                    'K' -> eval += 100
                    'p' -> eval -= 10
                    'n' -> eval -= 30
                    'b' -> eval -= 30
                    'r' -> eval -= 50
                    'q' -> eval -= 90
                    'k' -> eval -= 100
                }
            }
        }
        return eval
    }
    
    private fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        val piece = chessBoard[fromRow]?.get(fromCol) ?: ' '
        val target = chessBoard[toRow]?.get(toCol) ?: ' '
        
        // Проверка на взятие фигуры
        if (target.isUpperCase()) {
            Toast.makeText(this, "Взята фигура: ${target}", Toast.LENGTH_SHORT).show()
        }
        
        // Выполнение хода
        chessBoard[fromRow]?.set(fromCol, ' ')
        chessBoard[toRow]?.set(toCol, piece)
        
        // Добавление хода в историю
        val moveNotation = "${fromCol + 1}${fromRow + 1} -> ${toCol + 1}${toRow + 1}"
        moveHistory.add(moveNotation)
        
        boardView.invalidate()
    }
    
    private fun updateMenu() {
        gameModeText.text = "Выберите режим игры"
        gameModeText.visibility = View.VISIBLE
        playVsComputerBtn.visibility = View.VISIBLE
        playVsFriendsBtn.visibility = View.VISIBLE
        exitBtn.visibility = View.VISIBLE
        statusText.visibility = View.GONE
        boardView.visibility = View.GONE
        computerMoveText.visibility = View.GONE
    }
}

class BoardView : View {
    private var board: Array<CharArray?> = emptyArray()
    private var playerColor = 'w'
    private val paint = android.graphics.Paint()
    private val textPaint = android.graphics.Paint()
    
    constructor(context: android.content.Context?) : super(context)
    constructor(context: android.content.Context?, attrs: android.util.AttributeSet?) : super(context, attrs)
    constructor(context: android.content.Context?, attrs: android.util.AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    
    fun setBoard(board: Array<CharArray?>, playerColor: Char) {
        this.board = board
        this.playerColor = playerColor
        invalidate()
    }
    
    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        
        val cellSize = kotlin.math.min(width, height) / 8
        val offsetX = (width - cellSize * 8) / 2
        val offsetY = (height - cellSize * 8) / 2
        
        paint.color = Color.parseColor("#81C784") // зеленый
        textPaint.color = Color.BLACK
        textPaint.textSize = (cellSize * 0.6).toFloat()
        textPaint.isFakeBoldText = true
        
        for (row in 0..7) {
            for (col in 0..7) {
                val x = offsetX + col * cellSize
                val y = offsetY + row * cellSize
                
                // Цвет клетки
                val isLight = (row + col) % 2 == 0
                paint.color = if (isLight) Color.parseColor("#C8E6C9") else Color.parseColor("#A5D6A7")
                canvas.drawRect(x.toFloat(), y.toFloat(), (x + cellSize).toFloat(), (y + cellSize).toFloat(), paint)
                
                val piece = board[row]?.get(col) ?: ' '
                if (piece != ' ') {
                    val text = if (piece.isUpperCase()) piece.toString() else piece.toString().uppercase()
                    val tx = x + cellSize / 2
                    val ty = y + cellSize / 2 + (textPaint.textSize / 2)
                    canvas.drawText(text, tx, ty, textPaint)
                }
            }
        }
    }
}
