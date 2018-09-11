package pieces;

import chess.engine.Board;
import chess.engine.BoardInterface;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import org.junit.Test;

import static org.junit.Assert.*;

public class PawnTest {

  @Test
  public void testEnPassantNormal() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 3, 1);
    assertTrue(b.selectPieceAt(1, 0)); // Select piece test
    assertTrue(b.goTo(3, 0)); // 2 steps forward test

    assertTrue(b.selectPieceAt(3, 1)); // Select opponent piece test
    assertTrue(b.goTo(2, 0)); // En Passant test
  }

  @Test
  public void testEnPassantOneSquareOnly() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 3, 1);
    b.forceMove(1, 0, 2, 0);
    assertTrue(b.selectPieceAt(2, 0));
    assertTrue(b.goTo(3, 0));

    assertTrue(b.selectPieceAt(3, 1));
    assertFalse(b.goTo(2, 0));
  }

  @Test
  public void testEnPassantMultipleActions() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    b.forceMove(6, 1, 4, 1);
    assertTrue(b.selectPieceAt(1, 0));
    assertTrue(b.goTo(3, 0));

    assertTrue(b.selectPieceAt(4, 1));
    assertTrue(b.goTo(3, 1));

    assertTrue(b.selectPieceAt(1, 5));
    assertTrue(b.goTo(3, 5));

    assertTrue(b.selectPieceAt(3, 1));
    assertFalse(b.goTo(2, 0));
  }

  @Test
  public void testQueenPromotion() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    Piece p = b.getAt(1, 0);
    b.forceKill(p, 6, 0);
    b.forceKill(p, 7, 0);
    b.forceMove(1, 0, 6, 0);

    assertTrue(b.selectPieceAt(6, 0));
    assertTrue(b.goTo(7, 0));
    assertTrue(b.isPromoting());
    assertTrue(b.promoteTo(BoardInterface.Promotion.Queen));

    assertEquals(b.getAt(7, 0).getClass(), Queen.class);

  }

}
