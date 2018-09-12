package rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import chess.engine.Board;
import chess.engine.BoardInterface;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import org.junit.Test;

public class PawnPromotionTest {

  @Test
  public void testQueenPromotion() {
    Board board = Board.getInstance().getEngine();
    board.setupStandardBoard(true);

    Piece piece = board.getAt(1, 0);
    board.forceKill(piece, 6, 0);
    board.forceKill(piece, 7, 0);
    board.forceMove(1, 0, 6, 0);

    assertTrue(board.selectPieceAt(6, 0));
    assertTrue(board.goTo(7, 0));
    assertTrue(board.isPromoting());
    assertTrue(board.promoteTo(BoardInterface.Promotion.Queen));

    assertEquals(board.getAt(7, 0).getClass(), Queen.class);

  }

}
