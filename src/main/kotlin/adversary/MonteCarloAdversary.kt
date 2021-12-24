package adversary

import model.Board
import model.Move
import tree.MCTreeNode
import kotlin.math.ln
import kotlin.math.sqrt


class MonteCarloAdversary(private val white: Boolean) : Adversary {
    private var monteCarloTree = MCTreeNode()

    private val numGames = 100
    private val maxMoves = 80
    private val exploitParameter = sqrt(2.0)

    override fun pickMove(board: Board): Move {
        return search(board)
    }

    private fun search(board: Board): Move {
        // Play a bunch of games
        monteCarloTree = MCTreeNode()
        val list: List<Int> = (1..numGames).toList()
        list.parallelStream().forEach { playGame(board.clone(), monteCarloTree) }

        // Now pick the best move
        var bestScore = Double.NEGATIVE_INFINITY
        var bestMove: Move = board.getMoves().random()
        for ((move, child) in monteCarloTree.children) {
            val score =
                (child.getNumerator() / child.getDenominator()) + exploitParameter * sqrt(ln(numGames.toDouble()) / child.getDenominator())
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }

        return bestMove
    }

    private fun playGame(board: Board, state: MCTreeNode) {
        var currentState = state
        var moves = 0
        // While the game is still running
        while (board.getStatus().first && moves < maxMoves) {
            // Play a random move and recurse
            val move = board.getMoves().random()
            board.performMoveNoCheck(move)
            // Update the tree node and move to the child
            currentState = currentState.addMove(move)
            moves++
        }
        // If game finished
        if (!board.getStatus().first) {
            // If white, 1 = win. If black, -1 = win
            when (if (white) board.getStatus().second else -1 * board.getStatus().second) {
                1 -> currentState.addWin()
                0 -> currentState.addDraw()
                -1 -> currentState.addLoss()
            }
        }
    }
}
