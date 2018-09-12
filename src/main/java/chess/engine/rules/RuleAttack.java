package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.pieces.Piece;

public class RuleAttack implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (action.getType() != Action.Type.Attack) {
      return Result.Invalid;
    }

    Piece piece = action.getPiece();

    if (piece
        .getPossibleAttackPositions()
        .stream()
        .noneMatch(m -> m.isAt(action.row(), action.col()))) {
      return Result.NotPassed;
    }

    Piece target = board.getAt(action.row(), action.col());
    if (target == null) {
      return Result.Invalid;
    }

    if (piece.isTop() != target.isTop()) {
      return Result.Passed;
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
