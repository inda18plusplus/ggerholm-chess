package chess.pieces;

import chess.rules.RuleNoOverlap;

public class Pawn extends Piece {

  private boolean hasMoved = false;

  public Pawn(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.add(new RuleNoOverlap());

  }

  @Override
  void calculatePossiblePositions() {
    try {
      positions[row() + (isTop() ? 1 : -1)][col()] = 1;

      if (!hasMoved) {
        positions[row() + (isTop() ? 2 : -2)][col()] = 1;
      }
    } catch (ArrayIndexOutOfBoundsException ignored) {
    }

  }

  @Override
  public void moveTo(int row, int col) {
    if (row != row() || col != col()) {
      hasMoved = true;
      super.moveTo(row, col);
    }
  }

  @Override
  public boolean canMoveTo(int row, int col) {
    return positions[row][col] == 1;
  }

  @Override
  public boolean canAttackAt(int row, int col) {
    if (isTop()) {
      return row == row() - 1 && (col == col() - 1 || col == col() + 1);
    }
    return row == row() + 1 && (col == col() - 1 || col == col() + 1);
  }

}
