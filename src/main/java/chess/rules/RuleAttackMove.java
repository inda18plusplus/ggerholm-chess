package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.King;
import chess.pieces.Piece;

public class RuleAttackMove implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return true;
    }

    Piece p = action.getPiece();
    Piece target = board.getAt(action.row(), action.col());

    if (target instanceof King) {
      return false;
    }

    return p.isTop() != target.isTop() && p.getPossibleAttackPositions()[target.row()][target.col()] == 1;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
