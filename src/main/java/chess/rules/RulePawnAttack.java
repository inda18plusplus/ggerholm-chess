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

    Piece piece = action.getPiece();

    int row = action.row();
    int col = action.col();

    if (col == piece.col() - 1 || col == piece.col() + 1) {
      if (piece.isTop() && row == piece.row() + 1) {
        return true;
      } else if (!piece.isTop() && row == piece.row() - 1) {
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
