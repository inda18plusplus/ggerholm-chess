package chess.rules;

import chess.Action;
import chess.Board;

public class RuleMovement implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Move)) {
      return true;
    }

    return action
            .getPiece()
            .getPossiblePositions()
            .stream()
            .anyMatch(m -> m.isAt(action.row(), action.col())
                    && board.getAt(action.row(), action.col()) == null);
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
