import chess.Board;
import org.junit.Assert;
import org.junit.Test;

public class BoardTest {

  @Test
  public void testSquareUnderAtttack() {
    Board b = Board.getInstance().getEngine();
    b.setupStandardBoard();

    Assert.assertFalse(b.isSquareUnderAttack(2, 0, true));
    Assert.assertTrue(b.isSquareUnderAttack(2, 0, false));

  }

}
