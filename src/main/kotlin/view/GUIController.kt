package view

import adversary.MonteCarloAdversary
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import model.Board
import model.BoardImpl
import model.Constants
import model.Move
import tornadofx.add
import kotlin.math.roundToInt

class GUIController(primaryStage: Stage) {
    private var gameCanvas: Canvas
    private var board: Board = BoardImpl()

    //    private var xImage = Image(javaClass.getResource("x.png")?.toString())
//    private var oImage = Image(javaClass.getResource("o.png")?.toString())
    private val adversary = MonteCarloAdversary(board, false, 1)

    init {

        // JavaFX GUI Setup
        val width = GUIConstants.WINDOW_WIDTH
        val height = GUIConstants.WINDOW_HEIGHT
        val root = Group()
        val s = Scene(root, width, height)
        gameCanvas = Canvas(width, height)

        // World Object Viewer
        gameCanvas.onMouseClicked = handleMouseClick()
        root.add(gameCanvas)
        primaryStage.apply {
            scene = s
        }
        draw()
    }

    private fun handleMouseClick() = EventHandler { e: MouseEvent? ->
        if (e != null) {
            // Find the corresponding square
            val boardWidth = GUIConstants.BOARD_WIDTH * GUIConstants.WINDOW_WIDTH
            val boardHeight = GUIConstants.BOARD_HEIGHT * GUIConstants.WINDOW_HEIGHT
            val marginX = (GUIConstants.WINDOW_WIDTH - boardWidth) / 2
            val marginY = (GUIConstants.WINDOW_HEIGHT - boardHeight) / 2
            val squareWidth = boardWidth / 9
            val squareHeight = boardHeight / 9
            val rank = ((e.y + squareHeight / 2 - marginY) / squareHeight) - 1
            val column = ((e.x + squareWidth / 2 - marginX) / squareWidth) - 1
            if (rank.roundToInt() !in 0..8 || column.roundToInt() !in 0..8) {
                return@EventHandler
            }
            val possibleMove = Move(if (board.getMove()) 1 else -1, Pair(rank.roundToInt(), column.roundToInt()))
            if (board.performMove(possibleMove)) {
                draw()
                val move = adversary.pickMove(board)
                board.performMoveNoCheck(move)
            }

            draw()
        }
    }

    private fun draw() {
        val graphicsContext = gameCanvas.graphicsContext2D
        graphicsContext.drawBoard()
    }


    private fun GraphicsContext.drawBoard() {
        // Color whole background
        fill = Color.WHITE
        fillRect(0.0, 0.0, GUIConstants.WINDOW_WIDTH, GUIConstants.WINDOW_HEIGHT)

        // Draw the board outline
        val boardWidth = GUIConstants.BOARD_WIDTH * GUIConstants.WINDOW_WIDTH
        val boardHeight = GUIConstants.BOARD_HEIGHT * GUIConstants.WINDOW_HEIGHT
        val marginX = (GUIConstants.WINDOW_WIDTH - boardWidth) / 2
        val marginY = (GUIConstants.WINDOW_HEIGHT - boardHeight) / 2

        lineWidth = GUIConstants.LINE_WIDTH
        stroke = Color.BLACK
        strokeRect(marginX, marginY, boardWidth, boardHeight)

        // Draw the squares
        val squareWidth = boardWidth / 9
        val squareHeight = boardHeight / 9
        for (rank in 0..8) {
            for (column in 0..8) {
                drawSquare(
                    marginX + (column) * squareWidth,
                    marginY + rank * squareHeight,
                    squareWidth,
                    squareHeight,
                    board.getPieceVal(rank, column)
                )
            }
        }
        // Thick lines
        lineWidth = GUIConstants.BOLD_LINE_WIDTH
        strokeLine(marginX, marginY + 3 * squareHeight, marginX + boardWidth, marginY + 3 * squareHeight)
        strokeLine(marginX, marginY + 6 * squareHeight, marginX + boardWidth, marginY + 6 * squareHeight)
        strokeLine(marginX + 3 * squareWidth, marginY, marginX + 3 * squareWidth, marginY + boardHeight)
        strokeLine(marginX + 6 * squareWidth, marginY, marginX + 6 * squareWidth, marginY + boardHeight)
        // Active board
        val activeBoard = board.getActive()
        if (activeBoard != -1) {
            stroke = Color.DEEPSKYBLUE
            strokeRect(
                marginX + 3 * squareWidth * (activeBoard % 3),
                marginY + 3 * squareHeight * (activeBoard / 3),
                squareWidth * 3,
                squareHeight * 3
            )
        } else {
            stroke = Color.DEEPSKYBLUE
            strokeRect(marginX, marginY, boardWidth, boardHeight)
        }
    }


    private fun GraphicsContext.drawSquare(
        x: Double, y: Double, width: Double, height: Double, piece: Int
    ) {
        lineWidth = GUIConstants.LINE_WIDTH
        strokeRect(x, y, width, height)
        if (piece == Constants.X_SQUARE) {
            fill = GUIConstants.X_COLOR
            fillText("X", x + width / 2, y + width / 2)
        } else if (piece == Constants.O_SQUARE) {
            fill = GUIConstants.O_COLOR
            fillText("O", x + width / 2, y + width / 2)
        }
    }
}