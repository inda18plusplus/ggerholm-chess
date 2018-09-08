package pieces;

import chess.Board;
import chess.pieces.Pawn;
import chess.pieces.Piece;
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
    Board b = new Board();
    b.setupStandardBoard();

    b.forceMove(6, 1, 3, 1);
    Assert.assertTrue(b.selectPieceAt(1, 0)); // Select piece test
    Assert.assertTrue(b.moveTo(3, 0)); // 2 steps forward test

    Assert.assertTrue(b.selectPieceAt(3, 1)); // Select opponent piece test
    Assert.assertTrue(b.moveTo(2, 0)); // En Passant test
  }

  @Test
  public void testEnPassantOneSquareOnly() {
    Board b = new Board();
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
    Board b = new Board();
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

}
