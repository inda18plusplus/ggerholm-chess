package chess;

import chess.pieces.Piece;

public interface BoardInterface {

  void setupStandardBoard();

  void setupEmptyBoard();

  boolean selectPieceAt(int row, int col);

  boolean moveTo(int row, int col);

  boolean killAt(int row, int col);

  boolean isSquareUnderAttack(int row, int col, boolean isTop, boolean isPawn);

  boolean isPromoting();

  boolean promoteTo(Class<? extends Piece> promotion);

  boolean isTopTurn();

  int getTurn();

  Board getEngine();

}
