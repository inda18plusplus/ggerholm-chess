import chess.Board;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testSquareUnderAtttack() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    Assert.assertFalse(b.isSquareUnderAttack(2, 0, true, false));
    Assert.assertTrue(b.isSquareUnderAttack(2, 0, false, false));

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

    Assert.assertTrue(b.isKingInCheck(true));
    Assert.assertFalse(b.isTeamInCheckmate(true));

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

    Assert.assertTrue(b.isKingInCheck(false));
    Assert.assertTrue(b.isTeamInCheckmate(false));

  }

  @Test
  public void testAttackOnKing() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new Queen(7, 7, true));
    b.addPiece(new King(7, 6, false));

    Assert.assertTrue(b.selectPieceAt(7, 7));
    Assert.assertFalse(b.killAt(7, 6));

  }

}
