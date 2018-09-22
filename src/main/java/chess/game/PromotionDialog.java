package chess.game;

import chess.engine.BoardInterface;
import java.awt.Component;
import javax.swing.JOptionPane;

class PromotionDialog {
  private static final BoardInterface.Promotion[] promotionChoices = {
      BoardInterface.Promotion.Queen,
      BoardInterface.Promotion.Rook,
      BoardInterface.Promotion.Bishop,
      BoardInterface.Promotion.Knight
  };

  static BoardInterface.Promotion queryPiece(Component parent) {
    int result = showDialog(parent);

    return promotionChoices[result];
  }

  private static int showDialog(Component parent) {
    return JOptionPane.showOptionDialog(
        parent,
        null,
        "Choose a promotion",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        promotionChoices,
        null
      );
  }
}
