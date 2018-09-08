package chess.rules;

import chess.Action;
import chess.Board;

public class RuleMovement implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Move)) {
      return true;
    }

    return action.getPiece().getPossiblePositions()[action.row()][action.col()] == 1;
  }

}
