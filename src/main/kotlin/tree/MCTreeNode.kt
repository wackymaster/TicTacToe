package tree

import model.Board
import model.Move
import java.io.Serializable
import kotlin.math.ln
import kotlin.math.sqrt

class MCTreeNode(private val state: Board, private val color: Boolean) : Serializable {
    private var numerator: Double = 0.0
    private var denominator: Double = 0.0
    private var parent: MCTreeNode? = null
    private var depth = 0
    var children: HashMap<Move, MCTreeNode> = hashMapOf()
    private val exploitParameter = sqrt(2.0)

    fun getColor(): Boolean {
        return color
    }

    fun getState(): Board {
        return state
    }

    fun getNumerator(): Double {
        return numerator
    }

    fun getDenominator(): Double {
        return denominator
    }

    fun getDepth(): Int {
        return depth
    }

    fun getSize(): Int {
        if (children.size == 0) return 1
        var size = 1
        for ((_, c) in children) {
            size += c.getSize()
        }
        return size
    }

    fun addWin() {
        numerator++
        denominator++
        parent?.addLoss()
    }

    fun addDraw() {
        numerator += 0.5
        denominator++
        parent?.addDraw()
    }

    fun addLoss() {
        denominator++
        parent?.addWin()
    }

    fun calculateConfidenceBound(): Double {
        if (parent == null) return 0.0
        if (denominator == 0.0) return Double.POSITIVE_INFINITY
        return (1 - numerator / denominator) + exploitParameter * sqrt(ln(parent!!.denominator) / denominator)
    }

    fun addMove(move: Move, state: Board): MCTreeNode {
        // Already contain this transition
        if (children.containsKey(move)) {
            return children[move]!!
        }
        // Add a new child. Its color will be the opposite of this
        val newChild = MCTreeNode(state, !this.color)
        children[move] = newChild
        newChild.parent = this
        newChild.depth = this.depth + 1
        return newChild
    }

    fun classInv(): Boolean {
        var num = 0.0
        var den = 0.0
        for (child in children.values) {
            num += child.numerator
            den += child.denominator
            if (child.color == this.color) {
                println("Invalid parent-child coloring")
                return false
            }
        }
        return true
    }
}