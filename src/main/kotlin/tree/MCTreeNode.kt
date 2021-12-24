package tree

import model.Move
import java.io.Serializable

class MCTreeNode : Serializable {
    private var numerator: Double = 0.0
    private var denominator: Double = 0.0
    private var parent: MCTreeNode? = null
    var children: HashMap<Move, MCTreeNode> = hashMapOf()

    fun getNumerator(): Double {
        return numerator
    }

    fun getDenominator(): Double {
        return denominator
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
        parent?.addWin()
    }

    fun addDraw() {
        numerator += 0.5
        denominator++
        parent?.addDraw()
    }

    fun addLoss() {
        numerator--
        denominator++
        parent?.addLoss()
    }

    fun addMove(move: Move): MCTreeNode {
        // Already contain this transition
        if (children.containsKey(move)) {
            return children[move]!!
        }
        // Add a new node
        val newChild = MCTreeNode()
        children[move] = MCTreeNode()
        newChild.parent = this
        return newChild
    }

    fun classInv(): Boolean {
        var num = 0.0
        var den = 0.0
        for (child in children.values) {
            num += child.numerator
            den += child.denominator
        }
        return (num == numerator && den == denominator)
    }
}