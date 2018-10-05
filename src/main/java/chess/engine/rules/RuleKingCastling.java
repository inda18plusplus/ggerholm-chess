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
      action.setType(Type.Move);
      return Result.NotPassed;
    }

    final int targetCol = action.col();
    if (board.getGameType() == GameType.Fischer) {
      int fixedCol;
      if (queenSide) {
        fixedCol = 2;
      } else {
        fixedCol = 6;
      }

      action.clearActs();
      action.insertAct(true, () ->
          board.forceMove(piece.row(), piece.col(), piece.row(), fixedCol)
      );

    } else if (Math.abs(targetCol - piece.col()) != 2) {
      action.setType(Type.Move);
      return Result.NotPassed;
    }

    if (Rule.NO_OVERLAP.isActionAllowed(board, action) == Result.NotPassed) {
      action.setType(Type.Move);
      return Result.NotPassed;
    }

    int distance = Math.abs(targetCol - piece.col());
    int dir = (int) Math.signum(target.col() - piece.col());
    for (int i = 0; i <= distance; i++) {
      if (board.isSquareUnderAttack(piece.row(), piece.col() + i * dir, piece.isTop(), false)) {
        action.setType(Type.Move);
        return Result.NotPassed;
      }
    }

    action.insertAct(true, () ->
        board.forceMove(target.row(), target.col(), action.row(), targetCol - dir)
    );
    action.insertAct(false, () -> action.setNote("Castling"));

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return true;
  }

}
