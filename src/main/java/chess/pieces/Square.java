package chess.pieces;

import chess.Board;

import java.util.Set;

public class Square {

  private int row;
  private int col;

  /**
   * Stores the row and column in a combined object.
   *
   * @param row The row number.
   * @param col The column number.
   */
  public Square(int row, int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * Stores the row and column in a combined object.
   * Also adds the square to the provided set if it is within the bounds of the board.
   *
   * @param row The row number.
   * @param col The column number.
   * @param set The set to which the square will be added if it's within the board.
   */
  public Square(int row, int col, Set<Square> set) {
    this(row, col);

    if (row >= 0 && row < Board.GAME_SIZE && col >= 0 && col < Board.GAME_SIZE) {
      set.add(this);
    }
  }

  /**
   * The row of the square.
   *
   * @return Integer in the range 0 - 7.
   */
  public int row() {
    return row;
  }

  /**
   * The column of the square.
   *
   * @return Integer in the range 0 - 7.
   */
  public int col() {
    return col;
  }

  void set(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public boolean isAt(int row, int col) {
    return this.row == row && this.col == col;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Square)) {
      return o.equals(this);
    }

    Square other = (Square) o;
    return other.row == row && other.col == col;
  }

}
