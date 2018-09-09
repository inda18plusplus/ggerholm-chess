package pieces;

import chess.Board;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import org.junit.Assert;
import org.junit.Test;

public class PawnTest {

  @Test
  public void testPositions() {
    Piece pawn = new Pawn(1, 1, true);

    int[][] correct = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    Assert.assertArrayEquals(pawn.getPossiblePositions(), correct);

    int[][] correctAfterMove = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    pawn.moveTo(3, 1);

    Assert.assertArrayEquals(pawn.getPossiblePositions(), correctAfterMove);

  }

  @Test
  public void testEnPassantNormal() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 3, 1);
    Assert.assertTrue(b.selectPieceAt(1, 0)); // Select piece test
    Assert.assertTrue(b.moveTo(3, 0)); // 2 steps forward test

    Assert.assertTrue(b.selectPieceAt(3, 1)); // Select opponent piece test
    Assert.assertTrue(b.moveTo(2, 0)); // En Passant test
  }

  @Test
  public void testEnPassantOneSquareOnly() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 3, 1);
    b.forceMove(1, 0, 2, 0);
    Assert.assertTrue(b.selectPieceAt(2, 0));
    Assert.assertTrue(b.moveTo(3, 0));

    Assert.assertTrue(b.selectPieceAt(3, 1));
    Assert.assertFalse(b.moveTo(2, 0));
  }

  @Test
  public void testEnPassantMultipleActions() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 4, 1);
    Assert.assertTrue(b.selectPieceAt(1, 0));
    Assert.assertTrue(b.moveTo(3, 0));

    Assert.assertTrue(b.selectPieceAt(4, 1));
    Assert.assertTrue(b.moveTo(3, 1));

    Assert.assertTrue(b.selectPieceAt(1, 5));
    Assert.assertTrue(b.moveTo(3, 5));

    Assert.assertTrue(b.selectPieceAt(3, 1));
    Assert.assertFalse(b.moveTo(2, 0));
  }

  @Test
  public void testQueenPromotion() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    Piece p = b.getAt(1, 0);
    b.forceKill(p, 6, 0);
    b.forceKill(p, 7, 0);
    b.forceMove(1, 0, 6, 0);

    Assert.assertTrue(b.selectPieceAt(6, 0));
    Assert.assertTrue(b.moveTo(7, 0));
    Assert.assertTrue(b.isPromoting());
    Assert.assertTrue(b.promoteTo(Queen.class));

    Assert.assertEquals(b.getAt(7, 0).getClass(), Queen.class);

  }

}
