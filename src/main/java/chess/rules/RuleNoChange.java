package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleNoChange implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    if (!(piece.row() == action.row() && piece.col() == action.col())) {
      return Result.Passed;
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
