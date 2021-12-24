package view

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import tornadofx.add

class GUIController(primaryStage: Stage) {
    private var chessCanvas: Canvas

    init {

        // JavaFX GUI Setup
        val width = GUIConstants.WINDOW_WIDTH
        val height = GUIConstants.WINDOW_HEIGHT
        val root = Group()
        val s = Scene(root, width, height)
        chessCanvas = Canvas(width, height)

        // World Object Viewer
        chessCanvas.onMouseClicked = handleMouseClick()
        root.add(chessCanvas)
        primaryStage.apply {
            scene = s
        }
        draw()
    }

    private fun handleMouseClick() = EventHandler { e: MouseEvent? ->
        if (e != null) {
            draw()
        }
    }

    private fun draw() {
        val graphicsContext = chessCanvas.graphicsContext2D
        graphicsContext.drawBoard()
    }


    private fun GraphicsContext.drawBoard() {

    }


}