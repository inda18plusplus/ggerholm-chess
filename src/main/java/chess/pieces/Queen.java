package chess.pieces;

import chess.Utils;

public class Queen extends Piece {

  public Queen(int row, int col, boolean isTop) {
    super(row, col, isTop);

  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < positions.length; i++) {
      positions[i][col()] = 1;
      positions[row()][i] = 1;

      Utils.tryPutAt(m -> positions[row() + m][col() + m] = 1, i);
      Utils.tryPutAt(m -> positions[row() + m][col() - m] = 1, i);
      Utils.tryPutAt(m -> positions[row() - m][col() + m] = 1, i);
      Utils.tryPutAt(m -> positions[row() - m][col() - m] = 1, i);

    }

    positions[row()][col()] = 0;
    attackPositions = positions;
  }

}
