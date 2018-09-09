package rules;

import chess.Board;
import chess.pieces.King;
import chess.pieces.Piece;
import chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

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

    Assert.assertTrue(b.selectPieceAt(0, 4));
    Assert.assertTrue(b.moveTo(0, 2));

    Assert.assertEquals(b.getAt(0, 2).getClass(), King.class);
    Assert.assertEquals(b.getAt(0, 3).getClass(), Rook.class);

  }

}
