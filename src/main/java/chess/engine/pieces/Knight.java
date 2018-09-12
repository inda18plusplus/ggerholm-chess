package chess.engine.pieces;

import chess.engine.rules.Rule;

public class Knight extends Piece {

  /**
   * A knight piece.
   *
   * @param row The row of the piece.
   * @param col The col of the piece.
   * @param isTop Whether the piece belongs to the top or bottom team.
   */
  public Knight(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.remove(Rule.NO_OVERLAP);

  }

  @Override
  void calculatePossiblePositions() {

    new Square(row() + 2, col() + 1, possiblePositions);
    new Square(row() + 2, col() - 1, possiblePositions);
    new Square(row() - 2, col() + 1, possiblePositions);
    new Square(row() - 2, col() - 1, possiblePositions);

    new Square(row() + 1, col() + 2, possiblePositions);
    new Square(row() - 1, col() + 2, possiblePositions);
    new Square(row() + 1, col() - 2, possiblePositions);
    new Square(row() - 1, col() - 2, possiblePositions);

    possibleAttacks.addAll(possiblePositions);
  }

  @Override
  public char toChar() {
    return isTop() ? 'H' : 'h';
  }

}
