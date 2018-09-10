package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.pieces.Piece;

public class RuleAttack implements Rule {

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
