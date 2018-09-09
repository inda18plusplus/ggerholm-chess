package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.Piece;

public class RuleNoCheck implements Rule {

  @Override
  public boolean isActionAllowed(Board board, Action action) {
    Piece piece = action.getPiece();

    Board shallow = board.getShallowCopy();
    shallow.forceMove(piece.row(), piece.col(), action.row(), action.col());

    return !shallow.isKingInCheck(piece.isTop());
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
