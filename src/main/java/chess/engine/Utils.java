package chess.engine;

import chess.engine.pieces.Square;

public final class Utils {

  private static final char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

  public static String getSquareNotation(int row, int col) {
    return "" + chars[row] + (8 - col);
  }

  public static String getSourceSquareNotation(Action action) {
    return getSquareNotation(action.row(), action.col());
  }

  public static String getTargetSquareNotation(Action action) {
    return getSquareNotation(action.getPiece().row(), action.getPiece().col());
  }

  public static Square getSquareFromNotation(String notation) {
    if (notation.length() != 2) {
      return null;
    }

    int row = chars.length - Integer.parseInt("" + notation.charAt(1));
    int col = notation.charAt(0) - 'a';
    return new Square(row, col);
  }

}
