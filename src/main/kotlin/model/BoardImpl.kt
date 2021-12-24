package model

class BoardImpl : Board {
    private lateinit var board: MutableList<MutableList<Int>>
    private var numMoves = 0
    private var xMove = true

    init {
        clear()
    }

    override fun clear() {
        xMove = true
        board = MutableList(9) { MutableList(9) { 0 } }
    }

    override fun getReadOnlyBoard(): List<List<Int>> {
        val newBoard = mutableListOf<List<Int>>()
        for (i in 0..8) {
            val row = MutableList(9) { 0 }
            for (j in 0..8) {
                row[j] = board[i][j]
            }
            newBoard.add(row)
        }
        return newBoard
    }

    override fun getStatus(): Pair<Boolean, Int> {
        TODO("Not yet implemented")
    }

    override fun getMove(): Boolean {
        return xMove
    }

    override fun getNumMoves(): Int {
        return numMoves
    }

    override fun getPieceVal(rank: Int, column: Int): Int {
        TODO("Not yet implemented")
    }


    override fun inBoard(rank: Int, column: Int): Boolean {
        TODO("Not yet implemented")
    }


    override fun clone(): Board {
        val clone = BoardImpl()
        // Clone attributes
        clone.numMoves = this.numMoves
        clone.xMove = this.xMove
        // Clone board
        for (i in 0..8) {
            for (j in 0..8) {
                clone.board[i][j] = board[i][j]
            }
        }
        return clone
    }

    override fun getMoves(): MutableList<Move> {
        TODO("Not yet implemented")
    }

    override fun performMoveNoCheck(move: Move) {
        TODO("Not yet implemented")
    }

    override fun performMove(move: Move): Boolean {
        TODO("Not yet implemented")
    }

}