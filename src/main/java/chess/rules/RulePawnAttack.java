package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Pawn;
import chess.pieces.Piece;

public class RulePawnAttack implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return Result.Invalid;
    }

    Piece piece = action.getPiece();
    if (!(piece instanceof Pawn)) {
      return Result.Invalid;
    }

    int row = action.row();
    int col = action.col();

    if (col == piece.col() - 1 || col == piece.col() + 1) {
      if (piece.isTop() && row == piece.row() + 1) {
        return Result.Passed;
      } else if (!piece.isTop() && row == piece.row() - 1) {
        return Result.Passed;
      }
    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }
}
