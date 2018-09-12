package rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import chess.engine.Board;
import chess.engine.pieces.King;
import chess.engine.pieces.Rook;
import org.junit.Test;

public class KingCastlingTest {

  @Test
  public void testCastling() {
    Board board = Board.getInstance().getEngine();
    board.setupEmptyBoard(true);

    board.addPiece(new Rook(0, 0, true));
    board.addPiece(new Rook(0, 7, true));
    board.addPiece(new King(0, 4, true));
    board.addPiece(new Rook(7, 0, false));
    board.addPiece(new Rook(7, 7, false));
    board.addPiece(new King(7, 4, false));

    assertTrue(board.selectPieceAt(0, 4));
    assertTrue(board.goTo(0, 2));

    assertEquals(board.getAt(0, 2).getClass(), King.class);
    assertEquals(board.getAt(0, 3).getClass(), Rook.class);

  }

}
