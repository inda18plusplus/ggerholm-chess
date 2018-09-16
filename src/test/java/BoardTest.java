import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import chess.engine.Board;
import chess.engine.pieces.Bishop;
import chess.engine.pieces.King;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Rook;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testDraw() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new King(0, 0, true));
    board.addPiece(new King(0, 1, false));

    assertTrue(board.isGameADraw());

    board.addPiece(new Knight(0, 2, false));

    assertTrue(board.isGameADraw());

    board.addPiece(new Bishop(0, 3, false));

    assertFalse(board.isGameADraw());

    board.setupEmptyBoard(true);

    board.addPiece(new King(0, 0, true));
    board.addPiece(new King(0, 1, false));
    board.addPiece(new Bishop(0, 2, true));
    board.addPiece(new Bishop(0, 3, false));

    assertFalse(board.isGameADraw());

    board.forceMove(0, 3, 0, 4);

    assertTrue(board.isGameADraw());

  }

  @Test
  public void testSquareUnderAttack() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    assertFalse(board.isSquareUnderAttack(2, 0, true, false));
    assertTrue(board.isSquareUnderAttack(2, 0, false, false));

  }

  @Test
  public void testKingCheck() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new King(2, 3, true));
    board.addPiece(new Bishop(2, 5, true));
    board.addPiece(new Knight(3, 7, true));
    board.addPiece(new King(5, 5, false));
    board.addPiece(new Bishop(5, 6, false));

    assertTrue(board.isKingInCheck(true));
    assertFalse(board.isTeamInCheckmate(true));

  }

  @Test
  public void testCheckmate() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new King(0, 5, true));
    board.addPiece(new Rook(3, 7, true));
    board.addPiece(new Queen(7, 7, true));
    board.addPiece(new King(7, 6, false));
    board.addPiece(new Rook(7, 5, false));
    board.addPiece(new Pawn(6, 5, false));
    board.addPiece(new Pawn(6, 6, false));

    assertTrue(board.isKingInCheck(false));
    assertTrue(board.isTeamInCheckmate(false));

  }

  @Test
  public void testStalemate() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new King(0, 6, true));
    board.addPiece(new King(2, 5, false));
    board.addPiece(new Queen(2, 7, false));

    assertTrue(board.isTeamInStalemate(true));

  }

  @Test
  public void testAttackOnKing() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new Queen(7, 7, true));
    board.addPiece(new King(7, 6, false));

    assertTrue(board.selectPieceAt(7, 7));
    assertFalse(board.tryGoTo(7, 6));

  }

}
