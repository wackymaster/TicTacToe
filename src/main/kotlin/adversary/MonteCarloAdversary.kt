package adversary

import model.Board
import model.Move
import tree.MCTreeNode
import kotlin.math.pow
import kotlin.system.exitProcess


class MonteCarloAdversary(board: Board, private val isX: Boolean, difficulty: Int) : Adversary {
    private var monteCarloTree = MCTreeNode(board, isX)
    private val timeLimit = difficulty * (10.0).pow(9)

    override fun pickMove(board: Board): Move {
        return search(board, timeLimit)
    }

    private fun search(board: Board, timeLimit: Double): Move {
        val startTime = System.nanoTime()
        var currentTime = startTime
        monteCarloTree = MCTreeNode(board, isX)
        var iterations = 0
        while (currentTime - startTime < timeLimit) {
            val leaf = select(monteCarloTree)
            explore(leaf) // explore, simulate, and propagate
            currentTime = System.nanoTime()
            iterations++
        }
        println("Iterations: $iterations")
        println("Games played " + monteCarloTree.getDenominator())
        return bestChild(monteCarloTree) ?: board.getMoves().random()
    }

    private fun select(node: MCTreeNode): MCTreeNode {
        var bestScore = Double.NEGATIVE_INFINITY
        if (node.children.isEmpty()) return node // Leaf
        // Pick a child based on formula
        var selectedChild: MCTreeNode = node.children.values.first()
        for ((_, child) in node.children) {
            if (child.calculateConfidenceBound() > bestScore) {
                bestScore = child.calculateConfidenceBound()
                selectedChild = child
            }
        }
        return select(selectedChild)
    }

    private fun explore(state: MCTreeNode) {
        var currentState: MCTreeNode
        val board = state.getState()
//        for(row in board.getReadOnlyBoard()){
//            println(row)
//        }
        for (move in board.getMoves()) {
            currentState = state // Reset the state
            // Create a clone of the board and play a move
            val boardClone = board.clone()
            boardClone.performMoveNoCheck(move)
            // Add child and update the state
            currentState = currentState.addMove(move, boardClone)
            simulate(currentState)
        }

    }

    private fun simulate(state: MCTreeNode) {
        val board = state.getState().clone()
        // While the game is still running
        while (board.getStatus().first) {
            // Play a random move and recurse
            val move = board.getMoves().random()
            board.performMove(move)
        }
        // Back propagate
        backPropagate(board.getStatus().second, state)
    }

    private fun backPropagate(result: Int, node: MCTreeNode) {
        // If white (x), 1 = win. If black (o), -1 = win
        // MCTreeNodes automagically back-propagate
        when (if (node.getColor()) result else -1 * result) {
            1 -> node.addWin()
            0 -> node.addDraw()
            -1 -> node.addLoss()
        }
    }


    private fun bestChild(root: MCTreeNode): Move? {
        var bestMove: Move? = null
        var mostVisits = Double.NEGATIVE_INFINITY
        var winProb = 0.0
        for ((move, child) in root.children) {
            if (child.getDenominator() > mostVisits) {
                bestMove = move
                mostVisits = child.getDenominator()
                winProb = 100 * (1 - (child.getNumerator() / child.getDenominator()))
            }
        }
        println("Win rate: $winProb%")
        return bestMove
    }
}
