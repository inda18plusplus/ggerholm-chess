package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Pawn;
import chess.pieces.Piece;

import java.util.List;

public class RuleEnPassant implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Move)) {
      return Result.Invalid;
    }

    List<Action> history = board.getHistory();
    if (history.size() == 0) {
      return Result.NotPassed;
    }

    Action lastAction = history.get(history.size() - 1);
    if (!(lastAction.getPiece() instanceof Pawn)) {
      return Result.NotPassed;
    }

    if (!lastAction.getType().equals(Action.Type.Move)) {
      return Result.NotPassed;
    }

    Piece last = lastAction.getPiece();
    if (last.row() != lastAction.row() + (last.isTop() ? -2 : 2)) {
      return Result.NotPassed;
    }

    if (last.col() != action.col()) {
      return Result.NotPassed;
    }

    Piece piece = action.getPiece();

    if (!(piece instanceof Pawn)) {
      return Result.NotPassed;
    }

    int row = action.row();
    int col = action.col();

    Piece target = board.getAt(piece.row(), col);
    if (target == null || target.isTop() == piece.isTop() || !(target instanceof Pawn)) {
      return Result.NotPassed;
    }

    if (col == piece.col() - 1 || col == piece.col() + 1) {

      if (piece.isTop() && row == piece.row() + 1
              || !piece.isTop() && row == piece.row() - 1) {

        action.insertAct(true, () -> board.forceKill(piece, piece.row(), col));
        return Result.Passed;
      }

    }

    return Result.NotPassed;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
