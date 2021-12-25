package model

interface Board {
    /**
     * Returns a read-only version of the board as a 2-D array
     */
    fun getReadOnlyBoard(): List<List<Int>>

    /**
     * Returns a pair containing a boolean whether game still going and an int for the status
     * If the boolean is false, then the int corresponds to the status (1 X wins, 0 draw, -1 O wins)
     * The second value is irrelevant if the first is true
     */
    fun getStatus() : Pair<Boolean, Int>

    /**
     * Returns true if X to move, false if O to move
     */
    fun getMove() : Boolean

    /**
     * Get the number of moves played
     */
    fun getNumMoves() : Int

    /**
     * Gets the integer value associated with the piece at @rank, @column
     * Requires: rank, column are in the bounds of the board
     */
    fun getPieceVal(rank: Int, column: Int): Int

    /**
     * Clears/Resets this of all pieces
     */
    fun clear()

    /**
     * Returns whether (rank, column) corresponds to a viable location in the game board
     */
    fun inBoard(rank: Int, column: Int): Boolean

    /**
     * Returns a clone of this board
     */
    fun clone() : Board

    /**
     * Returns a list of all legal moves
     */
    fun getMoves(): MutableList<Move>

    /**
     * Performs the given move. No check is made to see if it is legal
     */
    fun performMoveNoCheck(move: Move)

    /**
     * Attempts to perform the given move. Returns true if successful
     */
    fun performMove(move: Move) : Boolean

    /**
     * Returns the active board, if there is one. Otherwise, -1
     */
    fun getActive() : Int

}