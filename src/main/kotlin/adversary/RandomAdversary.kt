package adversary

import model.Board
import model.Move


class RandomAdversary : Adversary {
    override fun pickMove(board: Board): Move {
        for(move in board.getMoves()){
            println(move)
        }
        return board.getMoves().random()
    }

}
