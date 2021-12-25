package model

class BoardImpl : Board {
    private lateinit var board: MutableList<MutableList<Int>>
    private var numMoves = 0
    private var xMove = true
    private var activeBoard = -1 // -1 means any board can be played. Otherwise, boards are numbered 0-8 (l-r, u-d)
    var boardStatus = MutableList(9) { 0 } // 1 if x wins, -1 if o wins, MAX_VALUE if draw

    init {
        clear()
    }

    override fun clear() {
        xMove = true
        activeBoard = -1
        board = MutableList(9) { MutableList(9) { 0 } }
    }

    override fun getActive(): Int {
        return activeBoard
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
        var diagSum1 = 0
        var diagSum2 = 0
        // Look at rows and cols
        for (i in 0..2) {
            var rowSum = 0
            var colSum = 0
            for (j in 0..2) {
                rowSum += boardStatus[3 * i + j]
                colSum += boardStatus[3 * j + i]
                if (i == j) diagSum1 += boardStatus[3 * i + j]
                if (i + j == 2) diagSum2 += boardStatus[3 * i + j]
            }
            if (rowSum == 3 * Constants.X_SQUARE || colSum == 3 * Constants.X_SQUARE) {
                return Pair(false, 1)
            } else if (rowSum == 3 * Constants.O_SQUARE || colSum == 3 * Constants.O_SQUARE) {
                return Pair(false, -1)
            }
        }

        // Diagonals
        if (diagSum1 == 3 * Constants.X_SQUARE || diagSum2 == 3 * Constants.X_SQUARE) {
            return Pair(false, 1)
        } else if (diagSum1 == 3 * Constants.O_SQUARE || diagSum2 == 3 * Constants.O_SQUARE) {
            return Pair(false, -1)
        }

        // All squares full, tie
        if (boardStatus.none { it == 0 })
            return Pair(false, 0)
        // Keep playing
        return Pair(true, 0)
    }

    override fun getMove(): Boolean {
        return xMove
    }

    override fun getNumMoves(): Int {
        return numMoves
    }

    override fun getPieceVal(rank: Int, column: Int): Int {
        if (!inBoard(rank, column)) return Int.MIN_VALUE
        return board[rank][column]
    }


    override fun inBoard(rank: Int, column: Int): Boolean {
        if (rank !in 0..8 || column !in 0..8) {
            return false
        }
        return true
    }


    override fun clone(): Board {
        val clone = BoardImpl()
        // Clone attributes
        clone.numMoves = this.numMoves
        clone.xMove = this.xMove
        clone.activeBoard = this.activeBoard
        // Clone the board status
        clone.boardStatus = mutableListOf()
        this.boardStatus.forEach { clone.boardStatus.add(it) }
        // Clone board
        for (i in 0..8) {
            for (j in 0..8) {
                clone.board[i][j] = board[i][j]
            }
        }
        return clone
    }

    override fun getMoves(): MutableList<Move> {
        val moves = mutableListOf<Move>()
        val pieceVal = if (xMove) Constants.X_SQUARE else Constants.O_SQUARE
        // Any board can be played so long as there is an empty square
        if (activeBoard == -1) {
            for (i in board.indices) {
                for (j in board[i].indices) {
                    if (board[i][j] == Constants.EMPTY_SQUARE) {
                        val newMove = Move(pieceVal, Pair(i, j))
                        moves.add(newMove)
                    }
                }
            }
            return moves
        }
        // Case for each active board
        val boardRow = 3 * (activeBoard / 3)
        val boardCol = 3 * (activeBoard % 3)
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[boardRow + i][boardCol + j] == Constants.EMPTY_SQUARE) {
                    val newMove = Move(pieceVal, Pair(boardRow + i, boardCol + j))
                    moves.add(newMove)
                }
            }
        }
        return moves
    }

    private fun fillBoard(boardNum: Int, value: Int) {
        val boardRow = 3 * (boardNum / 3)
        val boardCol = 3 * (boardNum % 3)
        for (i in 0..2) {
            for (j in 0..2) {
                board[boardRow + i][boardCol + j] = value
            }
        }
        boardStatus[boardNum] = value
    }

    private fun checkBoard(boardNum: Int) {
        val boardRow = 3 * (boardNum / 3)
        val boardCol = 3 * (boardNum % 3)
        // Check each row and column
        for (i in 0..2) {
            var rowSum = 0
            var colSum = 0
            for (j in 0..2) {
                rowSum += board[boardRow + i][boardCol + j]
                colSum += board[boardRow + j][boardCol + i]
            }
            if (rowSum == Constants.X_SQUARE * 3 || colSum == Constants.X_SQUARE * 3) {
                fillBoard(boardNum, Constants.X_SQUARE)
                return
            } else if (rowSum == Constants.O_SQUARE * 3 || colSum == Constants.O_SQUARE * 3) {
                fillBoard(boardNum, Constants.O_SQUARE)
                return
            }
        }
        // Check diagonals
        var diagSum1 = 0
        var diagSum2 = 0
        for (i in 0..2) {
            diagSum1 += board[boardRow + i][boardCol + i]
            diagSum2 += board[boardRow + i][boardCol + (2 - i)]
        }
        if (diagSum1 == Constants.X_SQUARE * 3 || diagSum2 == Constants.X_SQUARE * 3) {
            fillBoard(boardNum, Constants.X_SQUARE)
            return
        } else if (diagSum1 == Constants.O_SQUARE * 3 || diagSum2 == Constants.O_SQUARE * 3) {
            fillBoard(boardNum, Constants.O_SQUARE)
            return
        }

        // Now check for cat's game on the board
        var catGame = true
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[boardRow + i][boardCol + j] == Constants.EMPTY_SQUARE) {
                    catGame = false
                    break
                }
            }
        }
        if (catGame) boardStatus[boardNum] = Int.MAX_VALUE
        return
    }

    override fun performMoveNoCheck(move: Move) {
        val loc = move.getLoc()
        // Perform the move
        board[loc.first][loc.second] = move.getValue()
        // See if the board is now winning
        val boardPlayedRow = loc.first / 3
        val boardPlayedCol = loc.second / 3
        val boardPlayed = 3 * boardPlayedRow + boardPlayedCol
        checkBoard(boardPlayed)
        // Assuming the board is not complete, this is the new active board
        val activeRow = loc.first % 3
        val activeCol = loc.second % 3
        activeBoard = activeRow * 3 + activeCol
        // Board has already been won, pick from any board
        if (activeBoard == -1 || boardStatus[activeBoard] != 0) activeBoard = -1
        // Switch turn
        xMove = !xMove

        return
    }

    override fun performMove(move: Move): Boolean {
        val legalMoves = getMoves()
        if (move !in legalMoves) return false
        // Check complete
        performMoveNoCheck(move)
        return true
    }

}