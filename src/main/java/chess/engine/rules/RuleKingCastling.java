package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

public class RuleKingCastling implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    Piece target = board.getAt(
        action.row(),
        action.col() < Board.BOARD_LENGTH / 2 ? 0 : Board.BOARD_LENGTH - 1);

    if (!(piece instanceof King) || !(target instanceof Rook)) {
      return Result.Invalid;
    }

    if (piece.hasMoved() || target.hasMoved() || piece.row() != target.row()) {
      return Result.NotPassed;
    }

    if (Math.abs(action.col() - piece.col()) != 2) {
      return Result.NotPassed;
    }

    if (Rule.NO_OVERLAP.isActionAllowed(board, action) == Result.NotPassed) {
      return Result.NotPassed;
    }

    int dir = (int) Math.signum(target.col() - piece.col());
    for (int i = 0; i < 3; i++) {
      if (board.isSquareUnderAttack(piece.row(), piece.col() + i * dir, piece.isTop(), false)) {
        return Result.NotPassed;
      }
    }

    action.insertAct(true, () ->
        board.forceMove(target.row(), target.col(), action.row(), action.col() - dir)
    );
    action.insertAct(false, () -> action.setMessage("Castling"));

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
