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

    Piece p = action.getPiece();
    Piece target = board.getAt(action.row(), action.col());

    return p.isTop() != target.isTop() && p.getPossiblePositions()[action.row()][action.col()] == 1;
  }
}
