package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleNoChange implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();
    return !(piece.row() == action.row() && piece.col() == piece.col());
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
