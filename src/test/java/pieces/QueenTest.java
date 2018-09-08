package pieces;

import chess.pieces.Piece;
import chess.pieces.Queen;
import org.junit.Assert;
import org.junit.Test;

public class QueenTest {

  @Test
  public void testPositions() {
    Piece queen = new Queen(0, 3, true);

    int[][] correct = {
            {1, 1, 1, 0, 1, 1, 1, 1},
            {0, 0, 1, 1, 1, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 0, 0},
            {1, 0, 0, 1, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 0, 1},
            {0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0},
    };

    Assert.assertArrayEquals(queen.getPossiblePositions(), correct);

  }

}
