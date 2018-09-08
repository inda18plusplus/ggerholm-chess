package chess.pieces;

import chess.Utils;
import chess.rules.Rule;

public class Knight extends Piece {

  public Knight(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.remove(Rule.NO_OVERLAP);

  }

  @Override
  void calculatePossiblePositions() {

    Utils.tryPutAt(m -> positions[row() + 2][col() + 1] = 1, 0);
    Utils.tryPutAt(m -> positions[row() + 2][col() - 1] = 1, 0);
    Utils.tryPutAt(m -> positions[row() - 2][col() + 1] = 1, 0);
    Utils.tryPutAt(m -> positions[row() - 2][col() - 1] = 1, 0);

    Utils.tryPutAt(m -> positions[row() + 1][col() + 2] = 1, 0);
    Utils.tryPutAt(m -> positions[row() - 1][col() + 2] = 1, 0);
    Utils.tryPutAt(m -> positions[row() + 1][col() - 2] = 1, 0);
    Utils.tryPutAt(m -> positions[row() - 1][col() - 2] = 1, 0);

    positions[row()][col()] = 0;
    attackPositions = positions;
  }
}
