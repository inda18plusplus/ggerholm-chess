import chess.engine.Action;
import chess.engine.pieces.Pawn;
import chess.engine.pieces.Piece;
import org.junit.Assert;
import org.junit.Test;

public class ActionTest {

  @Test
  public void testShallowCopy() {

    Piece original = new Pawn(0, 0, true);
    Action move = new Action(original, 2, 0, Action.Type.Move);

    Assert.assertEquals(0, move.getPiece().row());

    original.moveTo(2, 0);

    Assert.assertEquals(0, move.getPiece().row());
    Assert.assertEquals(2, original.row());

  }

}
