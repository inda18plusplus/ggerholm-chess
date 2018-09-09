package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleAttackMove implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return true;
    }

    Piece piece = action.getPiece();
    Piece target = board.getAt(action.row(), action.col());

    return piece.isTop() != target.isTop()
            && piece
            .getPossibleAttackPositions()
            .stream()
            .anyMatch(m -> m.isAt(target.row(), target.col()));
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
