package chess.pieces;

import chess.Board;

public class Bishop extends Piece {

  public Bishop(int row, int col, boolean isTop) {
    super(row, col, isTop);
  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 1; i < Board.GAME_SIZE; i++) {

      new Square(row() - i, col() + i, possiblePositions);
      new Square(row() - i, col() - i, possiblePositions);
      new Square(row() + i, col() + i, possiblePositions);
      new Square(row() + i, col() - i, possiblePositions);

    }

    possibleAttacks.addAll(possiblePositions);
  }

}
