package chess.pieces;

import chess.Utils;
import chess.rules.Rule;

public class Pawn extends Piece {


  public Pawn(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.remove(Rule.ATTACK_MOVE);
    rules.add(Rule.PAWN_ATTACK);
    rules.add(Rule.EN_PASSANT);

  }

  @Override
  void calculatePossiblePositions() {
    Utils.tryPutAt(m -> positions[row() + (isTop() ? 1 : -1)][col()] = 1, 0);

    if (!hasMoved) {
      Utils.tryPutAt(m -> positions[row() + (isTop() ? 2 : -2)][col()] = 1, 0);
    }

    Utils.tryPutAt(m -> attackPositions[row() + (isTop() ? 1 : -1)][col() + 1] = 1, 0);
    Utils.tryPutAt(m -> attackPositions[row() + (isTop() ? 1 : -1)][col() - 1] = 1, 0);
  }

}
