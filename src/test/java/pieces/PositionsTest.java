package pieces;

import static org.junit.Assert.assertTrue;

import chess.engine.pieces.King;
import chess.engine.pieces.Knight;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Queen;
import chess.engine.pieces.Square;
import java.util.Set;
import org.junit.Test;

public class PositionsTest {

  @Test
  public void testKingPositions() {
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

    positionsMatch(king, correct);

  }

  @Test
  public void testPawnPositions() {
    Piece pawn = new Pawn(1, 1, true);

    int[][] correct = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
    };

    positionsMatch(pawn, correct);

    int[][] correctAfterMove = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
    };

    pawn.moveTo(3, 1);

    positionsMatch(pawn, correctAfterMove);

  }

  @Test
  public void testQueenPositions() {
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

    positionsMatch(queen, correct);

  }

  @Test
  public void testKnightPositions() {
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

    positionsMatch(knight, correct);

  }

  private static void positionsMatch(Piece piece, int[][] correct) {
    Set<Square> positions = piece.getPossiblePositions();
    for (int i = 0; i < correct.length; i++) {
      for (int j = 0; j < correct.length; j++) {
        final int r = i;
        final int c = j;
        if (correct[i][j] == 1) {
          assertTrue(positions.stream().anyMatch(m -> m.isAt(r, c)));
        } else {
          assertTrue(positions.stream().noneMatch(m -> m.isAt(r, c)));
        }
      }
    }

  }

}
