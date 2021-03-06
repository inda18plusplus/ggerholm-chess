package rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import chess.engine.Board;
import org.junit.Test;

public class NoOverlapTest {

  @Test
  public void testNoOverlap() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    assertTrue(board.selectPieceAt(0, 0));
    assertFalse(board.tryGoTo(3, 0));

    assertTrue(board.selectPieceAt(0, 2));
    assertFalse(board.tryGoTo(4, 6));

  }

  @Test
  public void testKnightOverlap() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    assertTrue(board.selectPieceAt(0, 1));
    assertTrue(board.tryGoTo(2, 2));
  }

}
