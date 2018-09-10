package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Board;
import chess.engine.pieces.Piece;

public class RulePawnPromotion implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    int row = action.row();
    int col = action.col();

    if (action.getType() == Action.Type.Move) {

      if (Rule.MOVEMENT.isActionAllowed(board, action) == Result.Passed) {

        if (piece.isTop() && row == Board.GAME_SIZE - 1
            || !piece.isTop() && row == 0) {

          action.insertAct(true, board::promoteAfterAction);
          action.insertAct(true, () -> board.forceMove(piece.row(), piece.col(), row, col));

          return Result.Passed;
        }

      }

      return Result.NotPassed;
    }

    if (action.getType() == Action.Type.Attack) {

      if (Rule.ATTACK.isActionAllowed(board, action) == Result.Passed) {

        if (piece.isTop() && row == Board.GAME_SIZE - 1
            || !piece.isTop() && row == 0) {

          action.insertAct(true, board::promoteAfterAction);
          action.insertAct(true, () -> board.forceKill(piece, row, col));

          return Result.Passed;
        }

      }

      return Result.NotPassed;
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
