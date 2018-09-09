package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleAttackMove implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return Result.Invalid;
    }

    Piece piece = action.getPiece();
    Piece target = board.getAt(action.row(), action.col());
    if (target == null) {
      return Result.NotPassed;
    }

    if (piece.isTop() != target.isTop()
            && piece
            .getPossibleAttackPositions()
            .stream()
            .anyMatch(m -> m.isAt(action.row(), action.col()))) {
      return Result.Passed;
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
