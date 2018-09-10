package chess.engine.pieces;

import chess.engine.Board;

public class Queen extends Piece {

  public Queen(int row, int col, boolean isTop) {
    super(row, col, isTop);

  }

  @Override
  void calculatePossiblePositions() {

    for (int i = 0; i < Board.BOARD_LENGTH; i++) {
      possiblePositions.add(new Square(i, col()));
      possiblePositions.add(new Square(row(), i));

      if (i > 0) {
        new Square(row() - i, col() + i, possiblePositions);
        new Square(row() - i, col() - i, possiblePositions);
        new Square(row() + i, col() + i, possiblePositions);
        new Square(row() + i, col() - i, possiblePositions);
      }

    }

    possiblePositions.removeIf(m -> m.isAt(row(), col()));
    possibleAttacks.addAll(possiblePositions);
  }

  @Override
  public char toChar() {
    return isTop() ? 'Q' : 'q';
  }

}
