package rules;

import chess.engine.Board;
import chess.engine.pieces.King;
import chess.engine.pieces.Rook;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KingCastlingTest {

  @Test
  public void testCastling() {
    Board b = Board.getInstance().getEngine();
    b.setupEmptyBoard();

    b.addPiece(new Rook(0, 0, true));
    b.addPiece(new Rook(0, 7, true));
    b.addPiece(new King(0, 4, true));
    b.addPiece(new Rook(7, 0, false));
    b.addPiece(new Rook(7, 7, false));
    b.addPiece(new King(7, 4, false));

    assertTrue(b.selectPieceAt(0, 4));
    assertTrue(b.goTo(0, 2));

    assertEquals(b.getAt(0, 2).getClass(), King.class);
    assertEquals(b.getAt(0, 3).getClass(), Rook.class);

  }

}
