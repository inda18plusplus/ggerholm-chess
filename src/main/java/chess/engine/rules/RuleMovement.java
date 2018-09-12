package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Board;

public class RuleMovement implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (action.getType() != Action.Type.Move) {
      return Result.Invalid;
    }

    if (action
        .getPiece()
        .getPossiblePositions()
        .stream()
        .anyMatch(m -> m.isAt(action.row(), action.col())
            && board.getAt(action.row(), action.col()) == null)) {
      return Result.Passed;
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
