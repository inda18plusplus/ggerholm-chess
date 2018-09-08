package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RulePawnAttack implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return true;
    }

    Piece p = action.getPiece();

    int row = action.row();
    int col = action.col();

    if (col == p.col() - 1 || col == p.col() + 1) {
      if (p.isTop() && row == p.row() + 1) {
        return true;
      } else if (!p.isTop() && row == p.row() - 1) {
        return true;
      }
    }

    return true;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }
}
