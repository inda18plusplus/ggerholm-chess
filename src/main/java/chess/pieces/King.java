package chess.pieces;

import chess.rules.Rule;

public class King extends Piece {

  /**
   * A king piece.
   *
   * @param row   The row of the piece.
   * @param col   The col of the piece.
   * @param isTop Whether the piece belongs to the top or bottom team.
   */
  public King(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.add(Rule.KING_CASTLING);

  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < 9; i++) {

      new Square(row() - 1 + i / 3, col() - 1 + i % 3, possiblePositions);

    }

    possiblePositions.removeIf(m -> m.isAt(row(), col()));
    possibleAttacks.addAll(possiblePositions);
  }

}
