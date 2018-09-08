package chess;

import chess.pieces.Piece;

public interface BoardInterface {

  void setupStandardBoard();

  void setupEmptyBoard();

  boolean selectPieceAt(int row, int col);

  boolean moveTo(int row, int col);

  boolean killAt(int row, int col);

  /**
   * Returns whether or not the king is currently threatened.
   *
   * @param isTop Whether to check the top or bottom king.
   * @return True or false.
   */
  boolean isKingInCheck(boolean isTop);

  boolean isPromoting();

  boolean promoteTo(Class<? extends Piece> promotion);

  boolean isTopTurn();

  int getTurn();

  Board getEngine();

}
