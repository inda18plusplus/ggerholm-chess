package chess.engine.rules;

import chess.engine.Action;
import chess.engine.Action.Type;
import chess.engine.Board;
import chess.engine.BoardInterface.GameType;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;

public class RuleKingCastling implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (action.getType() != Type.Castling) {
      return Result.Invalid;
    }

    Piece piece = action.getPiece();
    boolean queenSide = action.col() < piece.col();
    Piece target = board.getRook(piece.isTop(), queenSide);

    if (!(piece instanceof King) || !(target instanceof Rook)) {
      return Result.Invalid;
    }

    if (piece.hasMoved() || target.hasMoved() || piece.row() != target.row()) {
      return Result.NotPassed;
    }

    int targetCol = action.col();
    if (board.getGameType() == GameType.Fischer) {
      if (queenSide) {
        targetCol = 2;
      } else {
        targetCol = 6;
      }

      final int t = targetCol;
      action.clearActs();
      action.insertAct(true, () ->
          board.forceMove(piece.row(), piece.col(), piece.row(), t)
      );

    } else if (Math.abs(targetCol - piece.col()) != 2) {
      return Result.NotPassed;
    }

    if (Rule.NO_OVERLAP.isActionAllowed(board, action) == Result.NotPassed) {
      return Result.NotPassed;
    }

    int distance = Math.abs(targetCol - piece.col());
    int dir = (int) Math.signum(target.col() - piece.col());
    for (int i = 0; i <= distance; i++) {
      if (board.isSquareUnderAttack(piece.row(), piece.col() + i * dir, piece.isTop(), false)) {
        return Result.NotPassed;
      }
    }

    final int t = targetCol;
    action.insertAct(true, () ->
        board.forceMove(target.row(), target.col(), action.row(), t - dir)
    );
    action.insertAct(false, () -> action.setNote("Castling"));

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
