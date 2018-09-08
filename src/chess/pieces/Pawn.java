package chess.pieces;

import chess.Action;
import chess.Board;
import chess.rules.Rule;

public class Pawn extends Piece {

  private boolean hasMoved = false;

  public Pawn(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.remove(Rule.ATTACK_MOVE);

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
  public boolean notAllowed(Board board, Action action) {
    if (action.getType().equals(Action.Type.Attack)) {
      int row = action.row();
      int col = action.col();
      boolean condition;

      if (isTop()) {
        condition = row == row() - 1 && (col == col() - 1 || col == col() + 1);
        if (!condition) {
          return true;
        }
      } else {
        condition = row == row() + 1 && (col == col() - 1 || col == col() + 1);
        if (!condition) {
          return true;
        }
      }
    }

    return super.notAllowed(board, action);
  }

}
