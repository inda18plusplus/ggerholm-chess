package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleNoChange implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    Piece p = action.getPiece();
    return !(p.row() == action.row() && p.col() == p.col());
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
