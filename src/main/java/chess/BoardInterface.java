package chess;

import chess.pieces.Piece;

public interface BoardInterface {

  /**
   * Setups a standard board with 16 pieces in each team.
   */
  void setupStandardBoard();

  /**
   * Setups an empty board.
   */
  void setupEmptyBoard();

  /**
   * Tries to select the piece at the provided square. If the piece is of the opponent's team
   * or the square is empty nothing will happen.
   *
   * @param row The row number.
   * @param col The column number.
   * @return True if a viable piece was selected, otherwise false.
   */
  boolean selectPieceAt(int row, int col);

  /**
   * Attempts to move the currently selected piece to the provided square.
   * The movement must be accepted by all game rules before it can be made.
   *
   * @param row The targeted row.
   * @param col The targeted column.
   * @return True if the movement was successful, otherwise false.
   */
  boolean moveTo(int row, int col);

  /**
   * Attempts to attack an enemy piece with the currently selected piece.
   *
   * @param row The targeted row.
   * @param col The targeted column.
   * @return True if the attack was successful, otherwise false.
   */
  boolean killAt(int row, int col);

  /**
   * Returns whether or not the king is currently threatened.
   *
   * @param isTop Whether to check the top or bottom king.
   * @return True or false.
   */
  boolean isKingInCheck(boolean isTop);

  boolean isTeamInCheckmate(boolean isTop);

  boolean isTeamInStalemate(boolean isTop);

  /**
   * Returns whether or not the game currently requires a promotion of a pawn.
   *
   * @return True if the promotion of a pawn has begun, otherwise false.
   */
  boolean isPromoting();

  /**
   * Promotes the pawn that has reached the opposite side of the board.
   * If no such pawn exists nothing is done.
   * Pawns can be promoted to any other type of piece.
   *
   * @param promotion The class of whatever piece the pawn should be promoted to.
   * @return True if the promotion was successful, otherwise false.
   */
  boolean promoteTo(Class<? extends Piece> promotion);

  /**
   * Returns whether or not it is currently the top team's turn to make a move.
   * Top and bottom refers to the starting positions of the teams, not any kind of score.
   *
   * @return True if the top team is allowed to make a move, otherwise false.
   */
  boolean isTopTurn();

  /**
   * Returns the current turn of the game.
   *
   * @return An integer. Each valid move increases the turn by one.
   */
  int getTurn();

  /**
   * Returns the full engine instance that can be manipulated
   * in ways not possible in an ordinary game.
   *
   * @return A Board instance.
   */
  Board getEngine();

}
