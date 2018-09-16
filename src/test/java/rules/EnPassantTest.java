package rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import chess.engine.Board;
import org.junit.Test;

public class EnPassantTest {

  @Test
  public void testEnPassantNormal() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    board.forceMove(6, 1, 3, 1);
    assertTrue(board.selectPieceAt(1, 0)); // Select piece test
    assertTrue(board.tryGoTo(3, 0)); // 2 steps forward test

    assertTrue(board.selectPieceAt(3, 1)); // Select opponent piece test
    assertTrue(board.tryGoTo(2, 0)); // En Passant test
  }

  @Test
  public void testEnPassantOneSquareOnly() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    board.forceMove(6, 1, 3, 1);
    board.forceMove(1, 0, 2, 0);
    assertTrue(board.selectPieceAt(2, 0));
    assertTrue(board.tryGoTo(3, 0));

    assertTrue(board.selectPieceAt(3, 1));
    assertFalse(board.tryGoTo(2, 0));
  }

  @Test
  public void testEnPassantMultipleActions() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    board.forceMove(6, 1, 4, 1);
    assertTrue(board.selectPieceAt(1, 0));
    assertTrue(board.tryGoTo(3, 0));

    assertTrue(board.selectPieceAt(4, 1));
    assertTrue(board.tryGoTo(3, 1));

    assertTrue(board.selectPieceAt(1, 5));
    assertTrue(board.tryGoTo(3, 5));

    assertTrue(board.selectPieceAt(3, 1));
    assertFalse(board.tryGoTo(2, 0));
  }

}
