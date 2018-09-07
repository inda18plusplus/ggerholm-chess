package chess.pieces;

import chess.Utils;
import chess.rules.RuleNoOverlap;

public class Queen extends Piece {

  public Queen(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.add(new RuleNoOverlap());

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
  }

  @Override
  public boolean canMoveTo(int row, int col) {
    return positions[row][col] == 1;

  }

  @Override
  public boolean canAttackAt(int row, int col) {
    return canMoveTo(row, col);
  }

}
