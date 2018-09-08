package pieces;

import chess.pieces.King;
import chess.pieces.Piece;
import org.junit.Assert;
import org.junit.Test;

public class KingTest {

  @Test
  public void testPositions() {
    Piece king = new King(0, 4, true);

    int[][] correct = {
            {0, 0, 0, 1, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    Assert.assertArrayEquals(king.getPossiblePositions(), correct);

  }

}
