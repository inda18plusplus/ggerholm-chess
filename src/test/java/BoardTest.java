import chess.engine.Board;
import chess.engine.pieces.Bishop;
import chess.engine.pieces.King;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Rook;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

  @Test
  public void testSquareUnderAttack() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    assert !b.isSquareUnderAttack(2, 0, true, false);
    assert b.isSquareUnderAttack(2, 0, false, false);

  }

  @Test
  public void testKingCheck() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new King(2, 3, true));
    b.addPiece(new Bishop(2, 5, true));
    b.addPiece(new Knight(3, 7, true));
    b.addPiece(new King(5, 5, false));
    b.addPiece(new Bishop(5, 6, false));

    assert b.isKingInCheck(true);
    assert !(b.isTeamInCheckmate(true));

  }

  @Test
  public void testCheckmate() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new King(0, 5, true));
    b.addPiece(new Rook(3, 7, true));
    b.addPiece(new Queen(7, 7, true));
    b.addPiece(new King(7, 6, false));
    b.addPiece(new Rook(7, 5, false));
    b.addPiece(new Pawn(6, 5, false));
    b.addPiece(new Pawn(6, 6, false));

    assert b.isKingInCheck(false);
    assert b.isTeamInCheckmate(false);

  }

  @Test
  public void testStalemate() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new King(0, 6, true));
    b.addPiece(new King(2, 5, false));
    b.addPiece(new Queen(2, 7, false));

    assert b.isTeamInStalemate(true);

  }

  @Test
  public void testAttackOnKing() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new Queen(7, 7, true));
    b.addPiece(new King(7, 6, false));

    assertTrue(b.selectPieceAt(7, 7));
    assertFalse(b.goTo(7, 6));

  }

}
