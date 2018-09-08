package chess.pieces;

import chess.Utils;
import chess.rules.Rule;

public class Pawn extends Piece {

  /**
   * A pawn piece.
   *
   * @param row   The row of the piece.
   * @param col   The col of the piece.
   * @param isTop Whether or not the piece belongs to the top or bottom team.
   */
  public Pawn(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.remove(Rule.ATTACK_MOVE);
    rules.add(Rule.PAWN_ATTACK);
    rules.add(Rule.EN_PASSANT);
    rules.add(Rule.PAWN_PROMOTION);

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
