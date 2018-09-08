package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RulePawnPromotion implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    int row = action.row();
    int col = action.col();

    if (action.getType().equals(Action.Type.Move)) {

      if (Rule.MOVEMENT.isActionAllowed(board, action)) {

        if (piece.isTop() && row == 7
                || !piece.isTop() && row == 0) {

          board.forceMove(piece.row(), piece.col(), row, col);
          board.promoteAfterAction();

          return true;
        }

      }

      return false;
    }

    if (action.getType().equals(Action.Type.Attack)) {

      if (Rule.ATTACK_MOVE.isActionAllowed(board, action)) {

        if (piece.isTop() && row == 7
                || !piece.isTop() && row == 0) {

          board.forceKill(action);
          board.promoteAfterAction();

          return true;
        }

      }

      return false;
    }

    return false;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}