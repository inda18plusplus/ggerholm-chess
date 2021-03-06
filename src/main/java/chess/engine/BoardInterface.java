package chess.engine;

import chess.engine.pieces.Bishop;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Rook;
import java.util.List;

public interface BoardInterface {

  enum Promotion {
    Queen(Queen.class),
    Bishop(Bishop.class),
    Rook(Rook.class),
    Knight(Knight.class);

    Class<? extends Piece> type;

    Promotion(Class<? extends Piece> type) {
      this.type = type;
    }

    public char charCode() {
      switch (this) {
        case Queen:
          return 'q';
        case Bishop:
          return 'b';
        case Rook:
          return 'r';
        case Knight:
          return 'n';
        default:
          return 0;
      }
    }

    public static Promotion fromChar(char c) {
      switch (Character.toLowerCase(c)) {
        case 'q':
          return Promotion.Queen;
        case 'b':
          return Promotion.Bishop;
        case 'r':
          return Promotion.Rook;
        case 'n':
          return Promotion.Knight;
        default:
          break;
      }
      return null;
    }

  }

  enum State {
    Check, Checkmate, Stalemate, Draw, Normal;

    protected int turn;

  }

  enum GameType {
    Standard, Fischer
  }

  /**
   * Setups a standard board with 16 pieces in each team.
   *
   * @param topFirst Whether or not the top team should make the first move.
   */
  void setupStandardBoard(boolean topFirst);

  /**
   * Setups a standard board but with the pieces on the first ranks of each team shuffled.
   *
   * @param topFirst Whether or not the top team should make the first move.
   */
  void setupFischerBoard(boolean topFirst);

  /**
   * Setups an empty board.
   *
   * @param topFirst Whether or not the top team should make the first move.
   */
  void setupEmptyBoard(boolean topFirst);

  Action getLastMove();

  GameType getGameType();

  /**
   * Tries to select the piece at the provided square. If the piece is of the opponent's team or the
   * square is empty nothing will happen.
   *
   * @param row The row number.
   * @param col The column number.
   * @return True if a viable piece was selected, otherwise false.
   */
  boolean selectPieceAt(int row, int col);

  boolean hasSelected();

  /**
   * Attempts to move the selected unit to the provided square. If the square is currently occupied,
   * an attack is attempted.
   *
   * @param row The targeted row.
   * @param col The targeted column.
   * @return True if the move was successful, otherwise false.
   */
  boolean tryGoTo(int row, int col);

  boolean doCastling(boolean queenSide);

  /**
   * Returns whether or not the king is currently threatened.
   *
   * @param isTop Whether to check the top or bottom king.
   * @return True or false.
   */
  boolean isKingInCheck(boolean isTop);

  /**
   * Returns whether or not the king is currently threatened and there's no way to remove the
   * threat.
   *
   * @param isTop Whether to check the top or bottom king.
   * @return True or false.
   */
  boolean isTeamInCheckmate(boolean isTop);

  /**
   * Returns whether or not the team has any possible moves.
   *
   * @param isTop Whether to check the top or bottom team.
   * @return True if there's at least one possible move that can be done, otherwise false.
   */
  boolean isTeamInStalemate(boolean isTop);

  boolean isGameADraw();

  /**
   * Returns the state of the game. Either Check, Checkmate, Stalemate or Normal.
   *
   * @return A BoardInterface.State object.
   */
  State getGameState();

  /**
   * Returns whether or not the game currently requires a promotion of a pawn.
   *
   * @return True if the promotion of a pawn has begun, otherwise false.
   */
  boolean isPromoting();

  /**
   * Promotes the pawn that has reached the opposite side of the board. If no such pawn exists
   * nothing is done. Pawns can be promoted to any other type of piece.
   *
   * @param promotion The class of whatever piece the pawn should be promoted to.
   * @return True if the promotion was successful, otherwise false.
   */
  boolean promoteTo(Promotion promotion);

  /**
   * Returns whether or not it is currently the top team's turn to make a move. Top and bottom
   * refers to the starting positions of the teams, not any kind of score.
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

  List<Piece> getPieces();

  /**
   * Returns the full engine instance that can be manipulated in ways not possible in an ordinary
   * game.
   *
   * @return A Board instance.
   */
  Board getEngine();

}
