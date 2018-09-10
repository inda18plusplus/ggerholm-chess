package chess.engine.pieces;

import chess.engine.Board;

public class Rook extends Piece {

  public Rook(int row, int col, boolean isTop) {
    super(row, col, isTop);
  }

  @Override
  void calculatePossiblePositions() {
    for (int i = 0; i < Board.GAME_SIZE; i++) {
      possiblePositions.add(new Square(i, col()));
      possiblePositions.add(new Square(row(), i));
    }

    possiblePositions.removeIf(m -> m.isAt(row(), col()));
    possibleAttacks.addAll(possiblePositions);
  }

  @Override
  public char toChar() {
    return isTop() ? 'R' : 'r';
  }

}
