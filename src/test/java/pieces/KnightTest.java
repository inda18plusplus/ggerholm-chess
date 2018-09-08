package pieces;

import chess.pieces.Knight;
import chess.pieces.Piece;
import org.junit.Assert;
import org.junit.Test;

public class KnightTest {

  @Test
  public void testPositions() {
    Piece knight = new Knight(3, 3, true);

    int[][] correct = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 0, 0},
            {0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

    Assert.assertArrayEquals(knight.getPossiblePositions(), correct);

  }

}
