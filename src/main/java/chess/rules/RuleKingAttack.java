package chess.rules;

import chess.Action;
import chess.Board;
import chess.pieces.King;
import chess.pieces.Piece;

public class RuleKingAttack implements Rule {

  @Override
  public Result isActionAllowed(Board board, Action action) {
    if (!action.getType().equals(Action.Type.Attack)) {
      return Result.Invalid;
    }

    Piece target = board.getAt(action.row(), action.col());
    if (target instanceof King && target.isTop() != action.getPiece().isTop()) {
      action.clearActs();
    }

    return Result.Passed;
  }

  @Override
  public boolean isSuperior() {
    return false;
  }

}
