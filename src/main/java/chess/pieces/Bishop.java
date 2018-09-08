package chess.pieces;

import chess.Utils;

public class Bishop extends Piece {

  public Bishop(int row, int col, boolean isTop) {
    super(row, col, isTop);
  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < positions.length; i++) {

      Utils.tryPutAt(m -> positions[row() + m][col() + m] = 1, i);
      Utils.tryPutAt(m -> positions[row() + m][col() - m] = 1, i);
      Utils.tryPutAt(m -> positions[row() - m][col() + m] = 1, i);
      Utils.tryPutAt(m -> positions[row() - m][col() - m] = 1, i);

    }

    positions[row()][col()] = 0;
    attackPositions = positions;
  }

}
