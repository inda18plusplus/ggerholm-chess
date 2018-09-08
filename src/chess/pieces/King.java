package chess.pieces;

import chess.Utils;

public class King extends Piece {

  public King(int row, int col, boolean isTop) {
    super(row, col, isTop);
  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < 8; i++) {

      Utils.tryPutAt(m -> positions[row() - 1 + m % 3][col() - 1 + m % 3] = 1, i);

    }
  }

}
