package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.King;
import chess.pieces.Piece;
import chess.pieces.Rook;

public class RuleKingCastling implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    Piece p = action.getPiece();
    Piece target = board.getAt(action.row(), action.col() < Board.GAME_SIZE / 2 ? 0 : Board.GAME_SIZE - 1);

    if (!(p instanceof King) || !(target instanceof Rook)) {
      return false;
    }

    if (p.hasMoved() || target.hasMoved() || p.row() != target.row()) {
      return false;
    }

    if (Math.abs(action.col() - p.col()) != 2) {
      return false;
    }

    if (!Rule.NO_OVERLAP.isActionAllowed(board, action)) {
      return false;
    }

    int dir = (int) Math.signum(target.col() - p.col());
    for (int i = 0; i < 3; i++) {
      if (board.isSquareUnderAttack(p.row(), p.col() + i * dir)) {
        return false;
      }
    }

    board.forceMove(target.row(), target.col(), action.row(), action.col() + dir);

    return true;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
