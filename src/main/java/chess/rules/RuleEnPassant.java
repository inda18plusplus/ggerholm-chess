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

    Piece p = action.getPiece();

    if (!(p instanceof Pawn)) {
      return false;
    }

    int row = action.row();
    int col = action.col();

    Piece target = board.getAt(p.row(), col);
    if (target == null || target.isTop() == p.isTop() || !(target instanceof Pawn)) {
      return false;
    }

    if (col == p.col() - 1 || col == p.col() + 1) {
      Action killAction = new Action(p, p.row(), col, Action.Type.Attack);

      if (p.isTop() && row == p.row() + 1) {
        board.forceKill(killAction);
        return true;
      } else if (!p.isTop() && row == p.row() - 1) {
        board.forceKill(killAction);
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
