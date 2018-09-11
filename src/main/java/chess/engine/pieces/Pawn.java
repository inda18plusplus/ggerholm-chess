package chess.engine.pieces;

import chess.engine.rules.Rule;

public class Pawn extends Piece {

  /**
   * A pawn piece.
   *
   * @param row   The row of the piece.
   * @param col   The col of the piece.
   * @param isTop Whether the piece belongs to the top or bottom team.
   */
  public Pawn(int row, int col, boolean isTop) {
    super(row, col, isTop);

    rules.add(Rule.EN_PASSANT);
    rules.add(Rule.PAWN_PROMOTION);

  }

  @Override
  void calculatePossiblePositions() {
    new Square(row() + (isTop() ? 1 : -1), col(), possiblePositions);

    if (!hasMoved()) {
      new Square(row() + (isTop() ? 2 : -2), col(), possiblePositions);
    }

    new Square(row() + (isTop() ? 1 : -1), col() + 1, possibleAttacks);
    new Square(row() + (isTop() ? 1 : -1), col() - 1, possibleAttacks);
  }

  @Override
  public char toChar() {
    return isTop() ? 'P' : 'p';
  }

}
