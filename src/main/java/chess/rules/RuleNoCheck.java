package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleNoCheck implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();

    Board shallow = board.getShallowCopy();
    shallow.forceMove(piece.row(), piece.col(), action.row(), action.col());

    if (shallow.isKingInCheck(piece.isTop())) {
      return Result.NotPassed;
    }

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
