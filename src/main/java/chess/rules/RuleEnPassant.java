package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Pawn;
import chess.pieces.Piece;

import java.util.List;

public class RuleEnPassant implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Move)) {
      return false;
    }

    List<Action> history = board.getHistory();
    if (history.size() == 0) {
      return false;
    }

    Action lastAction = history.get(history.size() - 1);
    if (!(lastAction.getPiece() instanceof Pawn)) {
      return false;
    }

    if (!lastAction.getType().equals(Action.Type.Move)) {
      return false;
    }

    Piece last = lastAction.getPiece();
    if (last.row() != lastAction.row() + (last.isTop() ? -2 : 2)) {
      return false;
    }

    if (last.col() != action.col()) {
      return false;
    }

    Piece piece = action.getPiece();

    if (!(piece instanceof Pawn)) {
      return false;
    }

    int row = action.row();
    int col = action.col();

    Piece target = board.getAt(piece.row(), col);
    if (target == null || target.isTop() == piece.isTop() || !(target instanceof Pawn)) {
      return false;
    }

    if (col == piece.col() - 1 || col == piece.col() + 1) {

      if (piece.isTop() && row == piece.row() + 1
              || !piece.isTop() && row == piece.row() - 1) {

        action.insertAct(true, () -> board.forceKill(piece, piece.row(), col));
        return true;
      }

    }

    return false;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
