package rules;

import chess.engine.Board;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KingCastlingTest {

  @Test
  public void testCastling() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    Piece p = b.getAt(0, 4);
    b.forceKill(p, 0, 1);
    b.forceKill(p, 0, 2);
    b.forceKill(p, 0, 3);
    b.forceKill(p, 7, 3);

    assertTrue(b.selectPieceAt(0, 4));
    assertTrue(b.goTo(0, 2));

    assertEquals(b.getAt(0, 2).getClass(), King.class);
    assertEquals(b.getAt(0, 3).getClass(), Rook.class);

  }

}
