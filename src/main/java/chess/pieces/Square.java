package chess.pieces;

import chess.Board;

import java.util.Set;

public class Square {

  private int row;
  private int col;

  public Square(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public Square(int row, int col, Set<Square> set) {
    this(row, col);

    if (row >= 0 && row < Board.GAME_SIZE && col >= 0 && col < Board.GAME_SIZE) {
      set.add(this);
    }
  }

  public int row() {
    return row;
  }

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

  public boolean equals(Object o) {
    if (!(o instanceof Square)) {
      return o.equals(this);
    }

    Square other = (Square) o;
    return other.row == row && other.col == col;
  }

}
